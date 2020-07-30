package com.cosmeticos.service.order;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.FirebasePushNotifierService;
import com.cosmeticos.service.WalletService;
import com.cosmeticos.validation.OrderValidationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.Exception;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.cosmeticos.model.OrderStatus.*;
import static com.cosmeticos.model.Payment.Type.CC;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by matto on 17/06/2017.
 */
@Slf4j
@org.springframework.stereotype.Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerResponsitory;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private PriceRuleRepository priceRuleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FirebasePushNotifierService firebasePushNotifierService;

    @Autowired
    private ApplicationContext applicationContext;

    public Optional<Order> find(Long idOrder) {
        return (orderRepository.findById(idOrder));
    }

    public Order create(Order receivedOrder) throws OrderValidationException {

        /********************************************************/
        /*****   PREPARACAO DOS PRINCIPAIS OBJETOS **************/
        /********************************************************/

        Collection<Payment> paymentCollection = receivedOrder.getPaymentCollection();

            /*
             * Buscando o cliente que foi informado no request. Do que chega no request, so
             * preciso confiar no ID dessas entidades. Os outros atributos as vezes podem
             * nao vir preenchidos ou preenchidos de qualquer forma so pra nao ser barrado
             * pelo @Valid, portanto devemos buscar o objeto real no banco.
             */
        Customer persistentCustomer = customerResponsitory.findById(receivedOrder.getIdCustomer().getIdCustomer()).get();

        Long receivedProfessionalCategoryId =
                receivedOrder.getProfessionalCategory().getProfessionalCategoryId();

        ProfessionalCategory persistentProfessionalCategory =
                professionalCategoryRepository.findById(receivedProfessionalCategoryId).get();

        /********************************************************/
        /*****   VALIDACOES    **********************************/
        /********************************************************/
        Payment validatedPayment = null;
        if (paymentCollection.isEmpty()) {
            throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_CONFIGURATION, "Nao foi configurado objeto payment.");
        } else {
            // Apesar de ser uma lista, so trabalhamos com 1 payment
            validatedPayment = paymentCollection.stream().findFirst().get();
        }

        if (persistentProfessionalCategory == null) {
            throw new OrderValidationException(
                    ResponseCode.INVALID_PROFESSIONAL_CATEGORY,
                    "ProfessionalCategoryId invalido: " + receivedProfessionalCategoryId);

        }

        Payment receivedPayment = validatedPayment;
        Payment.Type paymentType = receivedPayment.getType();

        /*
        Se for venda a cartao, pegamos o cartao associado ao User.
         */
        if (CC.equals(paymentType)) {

            // Ha um risco serio de stackoverflow fazendo isso, mas precisamos da Order setada aqui para nao tomarmos
            // nullpointer nas implementacoes de Charger.addCard9(). TODO: Ver pq os testes nao detectaram essa falha.
            receivedPayment.setOrder(receivedOrder);

            // fev 2019: O cliente nao grava mais cartao durante a compra: https://trello.com/c/Y48N2tPo.
            // dev 2019: testes unitarios q usam cartao devem fazer o request de insercao de cartao

            User persistentUser = persistentCustomer.getUser();

            assertUserHasCreditCard(persistentUser);

            Collection<CreditCard> persistentCreditCards = persistentUser.getCreditCardCollection();

            CreditCard cc = persistentCreditCards.stream().findFirst().get();

            receivedPayment.setCreditCard(cc);
        }

        // Validamos o Payment recebido para que o cron nao tenha que descobrir que o payment esta mal configurado.
        validateAndApplyPaymentPriceRule(validatedPayment);


        /********************************************************/
        /*****   EXECUCAO      **********************************/
        /********************************************************/

        //Gera numeros de ate 8 digitos
        validatedPayment.setExternalTransactionId(
                String.valueOf(System.currentTimeMillis() % 8)
        );

        MDC.put("superpayNumeroTransacao", validatedPayment.getExternalTransactionId());

        Order order = new Order();
        order.setScheduleId(receivedOrder.getScheduleId());
        order.setIdLocation(receivedOrder.getIdLocation());
        order.setIdCustomer(persistentCustomer);
        order.setDate(Calendar.getInstance().getTime());
        order.setLastStatusUpdate(order.getDate());
        order.setStatus(OrderStatus.OPEN); // O STATUS INICIAL SERA DEFINIDO COMO CRIADO
        order.setProfessionalCategory(persistentProfessionalCategory);
        order.setAttendanceType(receivedOrder.getAttendanceType());
        order.setExpireTime(new Date(order.getDate().getTime() +

                // 6 horas de validade
                21600000));

        order.addPayment(validatedPayment);

        Order newOrder = orderRepository.save(order);

        paymentRepository.save(validatedPayment);// Pra ver se grava o pricerule pq nao esta salvando.

        MDC.put("idOrder", newOrder.getIdOrder().toString());

        // Buscando se o customer que chegou no request esta na wallet
        addInWallet(persistentProfessionalCategory.getProfessional(), persistentCustomer);

        firebasePushNotifierService.push(newOrder);

        return newOrder;
    }

    public Order update(Order receivedOrder) throws Exception {

        Order persistentOrder = orderRepository.findById(receivedOrder.getIdOrder()).get();

        OrderStatus previousOrderStatus = persistentOrder.getStatus();

        MDC.put("previousOrderStatus", String.valueOf(previousOrderStatus));

        //ADICIONEI ESSA VALIDACAO DE TENTATIVA DE ATUALIZACAO DE STATUS PARA O MESMO QUE JA ESTA EM ORDER
        if (persistentOrder.getStatus() == receivedOrder.getStatus()) {
            throw new OrderValidationException(ResponseCode.INVALID_ORDER_STATUS, "PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
        }

        if (OrderStatus.CLOSED == persistentOrder.getStatus()) {
            throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
        }

        if (OrderStatus.EXPIRED == persistentOrder.getStatus()) {
            throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
        }

        if (!isEmpty(receivedOrder.getDate())) {
            persistentOrder.setDate(receivedOrder.getDate());
        }
        //AQUI SETAMOS O STATUS VINDO DO REQUEST
        if (!isEmpty(receivedOrder.getStatus())) {
            if (!isEmpty(persistentOrder.getStatus())) {
                if (!receivedOrder.getStatus().equals(persistentOrder.getStatus())) {
                    // Houve mudança de status, logo atualizo lastStatusUpdate.
                    persistentOrder.setStatus(receivedOrder.getStatus());
                    persistentOrder.setLastStatusUpdate(Calendar.getInstance().getTime());
                }
            } else {
                persistentOrder.setStatus(receivedOrder.getStatus());
            }
        }

        /* Removendo isso pq senao estou permitindo que uma Order mude de customer, o que nao eh permitido.
        Na verdade deveria haver uma validacao de que se o customer da Order que veio do request eh diferente
        do customer que esta na Order gravada no banco.
        */
        if (!isEmpty(receivedOrder.getIdCustomer())) {
            if (receivedOrder.getIdCustomer().getIdCustomer() != persistentOrder.getIdCustomer().getIdCustomer()) {
                throw new OrderValidationException(ResponseCode.ILLEGAL_ORDER_OWNER_CHANGE,
                        "Nao se pode alterar o customer de uma order para outro customer");
            }
        }

        if (!isEmpty(receivedOrder.getIdLocation())) {
            persistentOrder.setIdLocation(receivedOrder.getIdLocation());
        }

        if (receivedOrder.getAttendanceType() != null) {
            persistentOrder.setAttendanceType(receivedOrder.getAttendanceType());
        }

        if (receivedOrder.isScheduled()) {

            Schedule receivedSchedule = receivedOrder.getScheduleId();
            Schedule persistentSchedule = persistentOrder.getScheduleId();

            if (persistentSchedule == null) {

                persistentOrder.setScheduleId(receivedSchedule);
            } else {
                // Atualizamos campo a campo caso ja haja schedule registrada no banco
                persistentSchedule.setTitle(receivedSchedule.getTitle() == null ?
                        persistentSchedule.getTitle() : receivedSchedule.getTitle());

                persistentSchedule.setDescription(receivedSchedule.getDescription() == null ?
                        persistentSchedule.getDescription() : receivedSchedule.getDescription());

                persistentSchedule.setScheduleStart(receivedSchedule.getScheduleStart() == null ?
                        persistentSchedule.getScheduleStart() : receivedSchedule.getScheduleStart());

                persistentSchedule.setScheduleEnd(receivedSchedule.getScheduleEnd() == null ?
                        persistentSchedule.getScheduleEnd() : receivedSchedule.getScheduleEnd());
            }
        }

        if(receivedOrder.getStatus() != null)
        {
            receivedOrder.getStatus().handle(applicationContext, receivedOrder, persistentOrder);
        }

        OrderStatus newStatus = persistentOrder.getStatus();

        boolean orderStatusHasChanged = !previousOrderStatus.equals(newStatus);

        if (orderStatusHasChanged) {
            orderRepository.save(persistentOrder);

            persistentOrder.getStatus().afterPersist(applicationContext, persistentOrder);

            //if (CLOSED.equals(persistentOrder.getStatus())
            //        || AUTO_CLOSED.equals(persistentOrder.getStatus())) {
//
            //    if (persistentOrder.isCreditCard()) {
            //        balanceItemService.create(creditFromOrder(persistentOrder));
            //    }
//
            //    // TODO: acumular pontos ao prof e ao cliente.
            //}

            // TODO: Mandar pruma fila, ser assincrono.
            firebasePushNotifierService.push(persistentOrder);
        }

        MDC.put("newOrderStatus", String.valueOf(persistentOrder.getStatus()));

        return persistentOrder;
    }

    /**
     * Apensar de ser uma collection, so trabalharemos com 1 Payment inicialmente, o qual este metodo estara retornando.
     *
     * @param receivedPayment
     * @return Persistent Payment
     */
    private void validateAndApplyPaymentPriceRule(Payment receivedPayment) {


        PriceRule chosenPriceRule = receivedPayment.getPriceRule();

        if (chosenPriceRule == null) {
            throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_CONFIGURATION, "Regra de preco nao foi enviada pelo cliente");
        } else {
            chosenPriceRule = priceRuleRepository.findById(chosenPriceRule.getId()).get();

            /**
             * Buscamos o pricerule no banco pq o q chega no request é so o ID.
             */
            chosenPriceRule.addPayment(receivedPayment);

            MDC.put("price: ", String.valueOf(chosenPriceRule.getPrice()));

        }
    }

    /**
     * Valida o cartao de credito para que quando a cron rode no dia de cobrar, ja estara garantido que o cliente possui
     * cartao registrado.
     *
     * @param persistentUser
     */
    private void assertUserHasCreditCard(User persistentUser) {
        Collection<CreditCard> persistentCreditCards = persistentUser.getCreditCardCollection();

        if (persistentCreditCards.isEmpty()) {
            throw new OrderValidationException(
                    ResponseCode.INVALID_PAYMENT_TYPE,
                    "Cliente solicitou compra por cartao de credito mas nao possui cartao de credito cadastrado: " +
                            persistentUser.toString()
            );
        }
    }

    /**
     * Adiciona o cliente na carteira do profissional se as condicoes forem satisfeitas.
     *
     * @param persistentProfessional
     * @param customer
     */
    private void addInWallet(Professional persistentProfessional, Customer customer) {
        Optional<Wallet> optionalWallet = walletService.findByProfessionalId(persistentProfessional.getIdProfessional());
        Optional<Customer> customerInWallet = Optional.empty();

        // Verificando se pelo menos existe a wallet.
        if (optionalWallet.isPresent()) {
            customerInWallet = optionalWallet.get().getCustomers()
                    .stream()
                    .filter(c -> c.getIdCustomer().equals(customer.getIdCustomer()))
                    .findAny();
        }

        // Se nao existe wallet ou se o cliente nao esta na wallet, entao aplicamos a logica de adicionar na wallet.
        if (!optionalWallet.isPresent() || !customerInWallet.isPresent()) {
            List<Order> savedOrders = orderRepository.findByIdCustomer_idCustomer(customer.getIdCustomer());

            int totalOrders = 0;

            for (int i = 0; i < savedOrders.size(); i++) {
                Order o = savedOrders.get(i);
                if (o.getProfessionalCategory().getProfessional().getIdProfessional() == persistentProfessional
                        .getIdProfessional()) {
                    totalOrders++;
                }
            }

            if (totalOrders >= 2) {
                if (persistentProfessional.getWallet() == null) {
                    persistentProfessional.setWallet(new Wallet());
                    persistentProfessional.getWallet().setProfessional(persistentProfessional);
                }
                persistentProfessional.getWallet().getCustomers().add(customer);
                professionalRepository.save(persistentProfessional);
            }
        }
    }

    public String delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Order> findByStatusNotCancelledOrClosed() {
        return orderRepository.findByStatusNotIn(Arrays.asList(CANCELLED, CLOSED, AUTO_CLOSED, EXPIRED));
    }

    public void abort(Order order) {
        Order o = orderRepository.findById(order.getIdOrder()).get();

        penaltyService.apply(o.getIdCustomer().getUser(), com.cosmeticos.penalty.PenaltyType.Value.NONE);
    }

    public List<Order> findActiveByCustomerEmail(String email) {
        return orderRepository.findByStatusNotInAndIdCustomer_user_email( //
                Arrays.asList(EXECUTED), // Usando um status que nunca vai pra banco.
                email);
    }

    /**
     * Retorna pedidos do profissional e de seus empregados.
     *
     * @param email
     * @return
     */
    public List<Order> findActiveByProfessionalEmail(String email) {
        List<Order> fullList = new ArrayList<>();

        List<Order> bossOrdersList = orderRepository.findByProfessionalEmail(email);
        List<Order> employeesOrdersList = orderRepository.findChildrenOrdersByOwnerProfessional(email);

        fullList.addAll(bossOrdersList);
        fullList.addAll(employeesOrdersList);

        return fullList;
    }

    public class ValidationException extends Exception {
        public ValidationException(String s) {
            super(s);
        }
    }

    @Scheduled(cron = "${order.unfinished.cron}")
    public void scheduledUpdateStatus() {

        List<Order> onlyOrsersFinishedByProfessionals = orderRepository.findByStatus(OrderStatus.SEMI_CLOSED);

        int count = onlyOrsersFinishedByProfessionals.size();

        for (Order o : onlyOrsersFinishedByProfessionals) {
            LocalDate lastUpdateLocalDate = o.getLastStatusUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate limitDate = LocalDate.now().minusDays(5);

            if (lastUpdateLocalDate.isBefore(limitDate)) {
                o.setStatus(OrderStatus.AUTO_CLOSED);
                o.setLastStatusUpdate(Calendar.getInstance().getTime());
                orderRepository.save(o);

                o.getStatus().afterPersist(applicationContext, o);
            }
        }
        log.debug("{} orders foram atualizada para {}.", count, OrderStatus.AUTO_CLOSED.toString());
    }


    /**
     * @param receivedOrder TODO: Ta escroto essas duas excecoes fazendo amesma coisa.. depois arrumo.. tem q ficar so a
     *                      OrderValidationException mas tem q alterar  a classe la ainda
     * @throws OrderValidationException
     * @throws ValidationException
     */
    public void validateCreate(Order receivedOrder) throws OrderValidationException, ValidationException {//

        if (receivedOrder.isScheduled()) {
            validateScheduleEndDate(receivedOrder);

            // Aqui vc escolhe o que quer usar.
            //validateBusyScheduled2(order);
            validateBusyScheduled1(receivedOrder);
        } else {
            Long idProfessionalCategory = receivedOrder.getProfessionalCategory().getProfessionalCategoryId();

            ProfessionalCategory professionalCategory = professionalCategoryRepository.findById(idProfessionalCategory).get();

            Professional professional = professionalCategory.getProfessional();

            MDC.put("idProfessional: ", String.valueOf(professional.getIdProfessional()));
            MDC.put("professionalUserStatus: ", String.valueOf(professional.getUser().getStatus()));

            validateIfThereAreOrderToSameProfessionalAndSameService(receivedOrder, professionalCategory);
            validateIfThereAreRunningOrders(receivedOrder, professional);
        }
    }

    /**
     * @param receivedOrder TODO: Ta escroto essas duas excecoes fazendo amesma coisa.. depois arrumo.. tem q ficar so a
     *                      OrderValidationException mas tem q alterar  a classe la ainda
     * @throws OrderValidationException
     * @throws ValidationException
     */
    public void validateUpdate(Order receivedOrder) throws OrderValidationException, ValidationException {//

        Long idOrder = receivedOrder.getIdOrder();

        MDC.put("idOrder", String.valueOf(idOrder));

        Order persistentOrder = orderRepository.findById(idOrder).get();

        Professional professional;
        ProfessionalCategory professionalCategory = receivedOrder.getProfessionalCategory();

        if (professionalCategory == null) {
            professionalCategory = persistentOrder.getProfessionalCategory();
        }

        professional = professionalCategory.getProfessional();

        // So a Order gravada no banco eh que sabe dizer se a order eh agendada ou nao.
        if (persistentOrder.isScheduled()) {
            // Se for nulo, entao nao eh intencao do cliente atualizar o agendamento, logo, nao validamos.
            if (receivedOrder.getScheduleId() != null) {
                validateScheduleEndDate(receivedOrder);
            }
        } else {
            // Pedidos agendados nao precisam dessas validações.
            validateIfThereAreRunningOrders(receivedOrder, professional);
            validateIfThereAreOrderToSameProfessionalAndSameService(receivedOrder, professionalCategory);
        }
    }

    private void validateIfThereAreOrderToSameProfessionalAndSameService(Order receivedOrder, ProfessionalCategory professionalCategory) {

        // Nao validamos orders agendadas pq os horarios sao diferentes.
        if (!receivedOrder.isScheduled()) {
            if (OrderStatus.OPEN.equals(receivedOrder.getStatus()) ||
                    OrderStatus.ACCEPTED.equals(receivedOrder.getStatus()) ||
                    OrderStatus.INPROGRESS.equals(receivedOrder.getStatus())) {

                Long idOrder = receivedOrder.getIdOrder() == null ? 0L : receivedOrder.getIdOrder();
                List<Order> orderList = null;

                // Se nao veio customer, ignoramos a etapa de validar orders duplicadas.
                if (receivedOrder.getIdCustomer() != null) {
                    // Profissional ja possui order em andamento?...
                    orderList = orderRepository.findOpenedDuplicatedOrders(
                            professionalCategory.getProfessional().getIdProfessional(),
                            receivedOrder.getIdCustomer().getIdCustomer(),
                            idOrder,
                            professionalCategory.getCategory().getIdCategory()
                    );

                    if (!orderList.isEmpty()) {
                        // Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
                        throw new OrderValidationException(ResponseCode.DUPLICATE_RUNNING_ORDER,
                                "Voce ja possui pedido de " + professionalCategory.getCategory().getName()
                                        + " aberto para o profissional " + professionalCategory.getProfessional().getNameProfessional());
                    }
                }

            }
        }
    }

    private void validateIfThereAreRunningOrders(Order receivedOrder, Professional professional) {

        //ESSA VALIDACAO SO DEVERA SER EXECUTADA NOS STATUS ABAIXO
        if (OrderStatus.OPEN.equals(receivedOrder.getStatus()) ||
                OrderStatus.ACCEPTED.equals(receivedOrder.getStatus()) ||
                OrderStatus.INPROGRESS.equals(receivedOrder.getStatus())) {

            Long idOrder = receivedOrder.getIdOrder() == null ? 0L : receivedOrder.getIdOrder();

            // Profissional ja possui order em andamento?...
            List<Order> orderList = orderRepository.findRunningOrdersByProfessional(
                    professional.getIdProfessional(), idOrder);

            if (!orderList.isEmpty()) {
                // Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
                throw new OrderValidationException(ResponseCode.DUPLICATE_RUNNING_ORDER,
                        "Profissional esta ocupado com outro serviço em andamento.");
            }
        }
    }

    private void validateBusyScheduled1(Order order) throws ValidationException, OrderValidationException {

        Date newOrderScheduleStart = order.getScheduleId().getScheduleStart();
        if (newOrderScheduleStart == null) {
            throw new OrderValidationException(ResponseCode.INVALID_SCHEDULE_START, "Data de inicio de agendamento vazia");
        }


		/*
        Nao coloco muitos filtros na query e retorno bastante orders e aplico a logica no if la em baixo.
		 */
        if (order.getProfessionalCategory() != null) {
            List<Order> persistentScheduledOrders = orderRepository.findScheduledOrdersByProfessionalCategory(
                    order.getProfessionalCategory().getProfessionalCategoryId()
            );

            for (int i = 0; i < persistentScheduledOrders.size(); i++) {
                Order o = persistentScheduledOrders.get(i);

                if (o.getScheduleId() != null) {
                    Date existingOrderStart = o.getScheduleId().getScheduleStart();
                    Date existingOrderEnd = o.getScheduleId().getScheduleEnd();

                    /*
                     Se nao ha hora de finalizacao do pedido configurado, consideramos que o pedido sera finalizado em
                     15 minutos. 15 minutos pois o maior fluxo de clientes eh de clientes homens, que sao atendidos em media em
                     20 minutos. Colocando 15 minutos de atendimento, garantimos que o proximo cliente chegue ao salao
                     minutos antes do profissional finalizar o cliente anterior.
                      */
                    if (existingOrderEnd == null) {
                        LocalDateTime existingOrderStartLDT = Timestamp.from(existingOrderStart.toInstant())
                                .toLocalDateTime()
                                .plusMinutes(15);

                        existingOrderEnd = Timestamp.valueOf(existingOrderStartLDT);
                    }

                    if (existingOrderStart.before(newOrderScheduleStart) &&
                            existingOrderEnd.after(newOrderScheduleStart)) {
                        // conflitou!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        throw new OrderValidationException(ResponseCode.CONFLICTING_SCHEDULES, "Ja existe agendamento marcado no horario de  " + newOrderScheduleStart.toString());
                    }
                }
            }
        }
    }

    @Scheduled(cron = "${order.expired.cron}")
    public void updateStatusOpenToExpired() {

        List<Order> onlyOrsersFinishedByProfessionals = orderRepository.findByStatus(OrderStatus.OPEN);

        int count = 0;

        // TODO: colocar esses minutos no properties
        LocalDateTime oneHourAfter = LocalDateTime.now().plusHours(1);

        for (Order o : onlyOrsersFinishedByProfessionals) {
            LocalDateTime orderCreationDate = o.getLastStatusUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


            if (orderCreationDate.isAfter(oneHourAfter)) {
                o.setStatus(OrderStatus.EXPIRED);
                o.setLastStatusUpdate(Calendar.getInstance().getTime());
                orderRepository.save(o);
                count++;
            }
        }
        log.debug("{} orders foram atualizada para {}.", count, OrderStatus.EXPIRED.toString());
    }

    public void validateScheduleEndDate(Order receivedOrder) throws OrderValidationException {

        if (receivedOrder.getScheduleId().getScheduleEnd() == null
                && OrderStatus.SCHEDULED.equals(receivedOrder.getStatus())) {
            throw new OrderValidationException(ResponseCode.INVALID_SCHEDULE_END, "Precisa de data final no agendamento");
        }
    }
}
