package com.cosmeticos.service;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.*;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;

import javax.servlet.http.HttpSession;
import java.lang.Exception;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.cosmeticos.model.Order.Status.*;
import static com.cosmeticos.service.BalanceItemService.creditFromOrder;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by matto on 17/06/2017.
 */
@Slf4j
@org.springframework.stereotype.Service
public class OrderService {

    @Value("${order.payment.secheduled.startDay}")
    private String daysToStartPayment;

    @Value("${order.payment.secheduled.daysBeforeStartToNotification}")
    private String daysBeforeStartToNotification;

    @Autowired
    private RandomCode randomCode;

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
    @Qualifier("charger")
    private Charger paymentService;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private PriceRuleRepository priceRuleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BalanceItemService balanceItemService;

    @Autowired
    private FirebasePushNotifierService firebasePushNotifierService;

    @Autowired
    private HttpSession httpSession;

    public Optional<Order> find(Long idOrder) {
        return Optional.of(orderRepository.findOne(idOrder));
    }

    public Order create(OrderRequestBody orderRequest) throws OrderValidationException {

        /********************************************************/
        /*****   PREPARACAO DOS PRINCIPAIS OBJETOS **************/
        /********************************************************/

        Order receivedOrder = orderRequest.getOrder();

        Collection<Payment> paymentCollection = receivedOrder.getPaymentCollection();

            /*
             * Buscando o cliente que foi informado no request. Do que chega no request, so
             * preciso confiar no ID dessas entidades. Os outros atributos as vezes podem
             * nao vir preenchidos ou preenchidos de qualquer forma so pra nao ser barrado
             * pelo @Valid, portanto devemos buscar o objeto real no banco.
             */
        Customer persistentCustomer = customerResponsitory.findOne(receivedOrder.getIdCustomer().getIdCustomer());

        Long receivedProfessionalCategoryId =
                receivedOrder.getProfessionalCategory().getProfessionalCategoryId();

        ProfessionalCategory persistentProfessionalCategory =
                professionalCategoryRepository.findOne(receivedProfessionalCategoryId);

        /********************************************************/
        /*****   VALIDACOES    **********************************/
        /********************************************************/
        Payment validatedPayment = null;
        if (paymentCollection.isEmpty()) {
            throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_CONFIGURATION, "Nao foi configurado objeto payment.");
        } else {
            validatedPayment = paymentCollection.stream().findFirst().get();
        }

        if (persistentProfessionalCategory == null) {
            throw new OrderValidationException(
                    ResponseCode.INVALID_PROFESSIONAL_CATEGORY,
                    "ProfessionalCategoryId invalido: " + receivedProfessionalCategoryId);

        }

        if (paymentCollection.isEmpty()) {
            throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_CONFIGURATION, "Nao foi configurado objeto payment.");
        } else {
            validatedPayment = paymentCollection.stream().findFirst().get();
        }

        Payment receivedPayment = validatedPayment;
        Payment.Type paymentType = receivedPayment.getType();

        /*
        Como tratar pagamento com cartao:
        - Cartao com oneclick: tem q vir cartao no order.payment; cc.oneclick tem q ser true.
        - Cartao com oneclick: demais vezes
        - Cartao sem oneclick: primeira vez
         */
        if (Payment.Type.CC.equals(paymentType)) {

            // Ha um risco serio de stackoverflow fazendo isso, mas precisamos da Order setada aqui para nao tomarmos
            // nullpointer nas implementacoes de Charger.addCard9(). TODO: Ver pq os testes nao detectaram essa falha.
            receivedPayment.setOrder(receivedOrder);

            Optional<CreditCard> optionalReceivedCc = ofNullable(receivedPayment.getCreditCard());

            User persistentUser = persistentCustomer.getUser();

            if (
                // Condicoes de quando o cliente cadastra cartao oneclick
                    (optionalReceivedCc.isPresent() && Boolean.valueOf(String.valueOf(optionalReceivedCc.get().isOneClick())))

                            // Condicao pra quando o cliente compra com oneclick a partir da segunda vez (sem enviar cc no request)
                            || userHasOneClickCard(persistentUser)) {

                if (shouldSaveForOneClick(persistentUser, optionalReceivedCc)) {
                    userService.addCreditCard(persistentUser, receivedPayment);
                }

                // Validamos se ja foi gravado cartao antes.
                // Valida se o usuario que paga com cartao realmente possui cartao cadastrado.
                validateAndApplyOneclickCreditcard(persistentUser, validatedPayment);

            } else if (optionalReceivedCc.isPresent()) {
                receivedPayment.setCreditCard(optionalReceivedCc.get());
            } else {
                throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_TYPE,
                        "Request de pagamento por cartao mas cartao nao chegou com o request e o usuario nao possui cartao oneclick");
            }
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
        order.setStatus(Order.Status.OPEN); // O STATUS INICIAL SERA DEFINIDO COMO CRIADO
        order.setProfessionalCategory(persistentProfessionalCategory);
        order.setAttendanceType(receivedOrder.getAttendanceType());
        order.setExpireTime(new Date(order.getDate().getTime() +

                // 6 horas de validade
                21600000));

        order.addPayment(validatedPayment);

        Order newOrder = orderRepository.save(order);

        paymentRepository.save(validatedPayment);// Pra ver se grava o pricerule pq nao esta salvando.

        org.apache.log4j.MDC.put("idOrder", newOrder.getIdOrder());
        // Buscando se o customer que chegou no request esta na wallet

        addInWallet(persistentProfessionalCategory.getProfessional(), persistentCustomer);

        firebasePushNotifierService.push(newOrder);

        return newOrder;
    }

    private boolean userHasOneClickCard(User persistentUser) {

        Set<CreditCard> cards = persistentUser.getCreditCardCollection();

        if (cards.isEmpty()) {
            return false;
        } else {
            CreditCard persistentUserCreditCard = cards.stream()
                    .findFirst()
                    .get();

            Boolean oneclick = persistentUserCreditCard.isOneClick();
            return oneclick == null ? true : oneclick;
        }
    }


    /**
     * Nao pode ter chegado cartao de credito no request (order.payment.cc == null) e o usuario precisa ter cartao com
     * token preenchido no banco.
     *
     * @param persistentUser
     * @param optionalReceivedCc
     * @return
     */
    private boolean shouldPayWithOneClick(User persistentUser, Optional<CreditCard> optionalReceivedCc) {

        Set<CreditCard> userCards = persistentUser.getCreditCardCollection();
        if (userCards.isEmpty()) {
            return false;
        } else {
            CreditCard existentCc = userCards.stream().findFirst().get();

            if (existentCc.isOneClick()) {
                Optional<String> token = ofNullable(existentCc.getToken());

                if (!token.isPresent()) {
                    throw new OrderValidationException(ResponseCode.INVALID_PAYMENT_CONFIGURATION,
                            "Usuario possui configuracao para OneClick mas seu token esta vazio!");
                }
            }
            return existentCc.isOneClick();
        }
    }

    private boolean shouldSaveForOneClick(User persistentUser, Optional<CreditCard> optionalReceivedCc) {

        return optionalReceivedCc.isPresent() // Deve ter chegado no request um CC
                && optionalReceivedCc.get().isOneClick() // O CC que chegou no request deve estar marcado pra oneclick
                && persistentUser.getCreditCardCollection().isEmpty(); // O usuario nao deve possuir cartao pre cadastrado.
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
            chosenPriceRule = priceRuleRepository.findOne(chosenPriceRule.getId());

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
     * @param receivedPayment
     */
    private void validateAndApplyOneclickCreditcard(User persistentUser, Payment receivedPayment) {
        if (Payment.Type.CC.equals(receivedPayment.getType())) {
            Collection<CreditCard> persistentCreditCards = persistentUser.getCreditCardCollection();

            if (persistentCreditCards.isEmpty()) {
                throw new OrderValidationException(
                        ResponseCode.INVALID_PAYMENT_TYPE,
                        "Cliente solicitou compra por cartao de credito mas nao possui cartao de credito cadastrado: " +
                                persistentUser.toString()
                );
            } else {
                Optional<CreditCard> cc = persistentCreditCards.stream().findFirst();

                receivedPayment.setCreditCard(cc.get());
            }
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

    public Order update(OrderRequestBody request) throws Exception {
        Order receivedOrder = request.getOrder();// Po, ta pegando o q veio do request.. ate agora . nada anormal...
        Order persistentOrder = orderRepository.findOne(receivedOrder.getIdOrder());

        Order.Status previousOrderStatus = persistentOrder.getStatus();

        MDC.put("previousOrderStatus", String.valueOf(previousOrderStatus));

        //ADICIONEI ESSA VALIDACAO DE TENTATIVA DE ATUALIZACAO DE STATUS PARA O MESMO QUE JA ESTA EM ORDER
        if (persistentOrder.getStatus() == receivedOrder.getStatus()) {
            //throw new IllegalStateException("PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
            throw new OrderValidationException(ResponseCode.INVALID_ORDER_STATUS, "PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
        }

        if (Order.Status.CLOSED == persistentOrder.getStatus()) {
            throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
        }

        if (Order.Status.EXPIRED == persistentOrder.getStatus()) {
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
        if (!isEmpty(receivedOrder.getIdCustomer())) {
            persistentOrder.setIdCustomer(receivedOrder.getIdCustomer());
        }*/
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

        if (receivedOrder.getScheduleId() != null) {

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

        applyVote(receivedOrder, persistentOrder);

        // TODO: remover isso de antes de chamar o superpay
        //orderRepository.save(persistentOrder);

        //AQUI TRATAMOS O STATUS ACCEPTED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
        // Utilizamos a order persistente pois ela possui TODOS os atributos setados
        if (receivedOrder.getStatus() == Order.Status.ACCEPTED) {


            for (Payment newPayment : persistentOrder.getPaymentCollection()) {
                //persistentOrder.addPayment(newPayment);
                //orderRepository.save(persistentOrder);
                if (Payment.Type.CC.equals(newPayment.getType())) {
                    this.sendPaymentRequest(newPayment);
                }
            }
        }
        //TIVE QUE COMENTAR A VALIDACAO ABAIXO POIS ESTAVA DANDO O ERRO ABAIXO:
        //QUANDO VAMOS ATUALIZAR PARA SCHEDULED, AINDA NAO TEMOS OS DADOS QUE VAO SER ATUALIZADOS
        //-- VALIDANDO O COMENTARIO ACIMA --//
        //AQUI TRATAMOS O STATUS SCHEDULED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
        else if (receivedOrder.getStatus() == Order.Status.SCHEDULED) {

            for (Payment newPayment : persistentOrder.getPaymentCollection()) {
                persistentOrder.addPayment(newPayment);
                orderRepository.save(persistentOrder);

                if (Payment.Type.CC.equals(newPayment.getType())) {
                    this.validateScheduledAndsendPaymentRequest(newPayment);
                }
            }
        }

        boolean mustPersistOrder = false;

        //TODO - CRIAR METODO DE VALIDAR PAYMENT RESPONSE LANCANDO ORDER VALIDATION EXCEPTION COM...
        //HTTPSTATUS DEFINIDO PARA CADA STATUS DE PAGAMENTO DA SUPERPAY
        //CARD: https://trello.com/c/fyPMjNJI/113-adequar-status-do-pagamento-do-superpay-aos-nossos-status-da-order
        //BRANCH: RNF101

        //ACHEI MELHOR FAZER UMA NOVA VERIFICACAO APOS SALVAR, POIS PRECISAMOS TER ARMAZENADO QUANDO MUDAMOS O STATUS
        //PARA READY2CHARGE E QUANDO FIZEMOS A CAPTURA. POIS COMO ESTAVA ANTES NAO TINHAMOS O REGISTRO DE READY2CHARGE
        //POIS QUANDO ERA ESTE STATUS, JA ENVIAMOS A CAPTURA E, LOGO APOS A CAPTURA, O CORRETO EH MUDAR O STATUS PARA PAYD
        if (persistentOrder.getStatus() == Order.Status.READY2CHARGE) {
            //TODO - VERIFICAR SE ESTA FUNCIONANDO PARA EFETUAR A CAPTURA CORRETAMENTE
            //O CARTAO ESTA VINDO NULL
            Payment payment = persistentOrder.getPaymentCollection()
                    .stream()
                    .findFirst()
                    .get();

            if (Payment.Type.CC.equals(payment.getType())) {
                //AQUI TRATAMOS O STATUS READY2CHARGE QUE VAI NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
                if (this.sendPaymentCapture(payment)) {

                    //ADICIONEI O QUE SEGUE ABAIXO POIS PRECISAMOS TER O REGISTRO DA ATUALIZACAO DOS DOIS STATUS
                    //PRIMEIRO READY2CHARGE E, LOGO EM SEGUIDA, SE A CAPTURA FOR FEITA COM SUCESSO, MUDAMOS PARA PAID
                    //OBS.: COMO NAO TEMOS O STATUS PAID, MUDEI PARA SEMI_CLOSED
                    persistentOrder.setStatus(Order.Status.CLOSED);
                    persistentOrder.setLastStatusUpdate(Calendar.getInstance().getTime());

                } else {
                    persistentOrder.setStatus(Order.Status.FAILED_ON_PAYMENT);
                    persistentOrder.setLastStatusUpdate(Calendar.getInstance().getTime());
                }

                mustPersistOrder = true;
            } else if (Payment.Type.CASH.equals(payment.getType())) {

                //TODO - SE ORDER NO BANCO FOR READY2CHARGE E PAGAMENTO EM DINHEIRO, ENTAO MUDAMOS O STATUS PARA O SOLICITADO???
                //NAO ESTOU ENTENDENDO ISSO!!!
                //TODO - VERIFICAR POIS SER FOR ENVIADO CLOSED PODE BATER AQUI E GERAR PROBLEMA
                // ACCEPTED ou READY2CHARGE?  Deivison quer que pague so apos executar o servico
                // Garry: Ta estranho mesmo.. vamos apagar esta instrucao
                persistentOrder.setStatus(Order.Status.CLOSED);

                mustPersistOrder = true;
            }
        } else {

            mustPersistOrder = true;
        }

        if (mustPersistOrder) {
            orderRepository.save(persistentOrder);

            if (CLOSED.equals(persistentOrder.getStatus())
                    || AUTO_CLOSED.equals(persistentOrder.getStatus())) {
                balanceItemService.create(creditFromOrder(persistentOrder));
            }
        }

        if(!previousOrderStatus.equals(persistentOrder.getStatus()))
        {
            // TODO: Mandar pruma fila, ser assincrono.
            firebasePushNotifierService.push(persistentOrder);
        }

        MDC.put("newOrderStatus", String.valueOf(persistentOrder.getStatus()));

        return persistentOrder;
    }

    private void applyVote(Order receivedOrder, Order persistentOrder) {

        if (receivedOrder.getStatus() == Order.Status.SEMI_CLOSED) {

            // Aplicado ao Customer quando o professional encerra o servico

            User persistentUser = persistentOrder.getIdCustomer().getUser();

            Customer receivedCustomer = receivedOrder.getIdCustomer();

            if (receivedCustomer != null) {
                User receivedUser = receivedOrder.getIdCustomer().getUser();

                if (receivedUser != null) {
                    Set<Vote> voteCollection = receivedUser.getVoteCollection();
                    if (voteCollection != null && !voteCollection.isEmpty()) {
                        Vote receivedvote = voteCollection.stream().findFirst().get();

                        if (receivedvote != null) {
                            addVotesToUser(persistentUser, receivedvote);
                            MDC.put("customerVote", String.valueOf(receivedvote.getValue()));
                        }
                    }
                }
            }

        } else if (receivedOrder.getStatus() == Order.Status.READY2CHARGE) {

            // Aplicado ao Professional quando o customer confirma realização do servico e avalia o professional

            ProfessionalCategory persistentProfessionalCategory = persistentOrder.getProfessionalCategory();
            User persistentUser = persistentProfessionalCategory.getProfessional().getUser();

            ProfessionalCategory receivedProfessionalCategory = receivedOrder.getProfessionalCategory();
            User receivedUser = receivedProfessionalCategory.getProfessional().getUser();

            // tm algo errado aki...  eu nomeio os objetos com receivedXXX pra saber o q veio do request e persistentXXX pra saber o q veio do banco
            // esse erro de lazy só faz sentido se o objeto veio do banco. Acho q em algum momento rolou confussao e  o receivedXX recebeu algo
            // persistente. Vo dar uma debugada reversa rsrsjah eh
            if (!receivedUser.getVoteCollection().isEmpty()) {
                Vote receivedvote = receivedUser.getVoteCollection().stream().findFirst().get();

                addVotesToUser(persistentUser, receivedvote);

                MDC.put("professionalVote", String.valueOf(receivedvote.getValue()));
            }

        }
    }

    private void addVotesToUser(User persistentUser, Vote receivedVote) {

        persistentUser.addVote(receivedVote);
        voteService.create(receivedVote);

        persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));


    }

    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    //BRANCH: RNFapp39-templatando-plus-cartao
    private Boolean sendPaymentCapture(Payment payment) throws JsonProcessingException, URISyntaxException, OrderValidationException {
        ChargeResponse<Object> chargeResponse = paymentService.capture(new ChargeRequest<>(payment));

        switch (chargeResponse.getResponseCode()) {
            case SUCCESS:
            case GATEWAY_DUPLICATE_PAYMENT:
                return true;
            default:
                throw new OrderValidationException(chargeResponse.getResponseCode(), "Falha na captura do superpay.");
        }
    }

    private Boolean validateScheduledAndsendPaymentRequest(Payment payment) throws Exception {

        Order persistenOrder = payment.getOrder();

        Boolean success = false;

        int daysToStart = Integer.parseInt(daysToStartPayment);
        int daysBeforeStart = Integer.parseInt(daysBeforeStartToNotification);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //INSTANCIAMOS O CALENDARIO
        Calendar c = Calendar.getInstance();

        //DATA ATUAL
        //EX.: 20/08/2017
        Date now = c.getTime();

        //PEGAMOS A DATA DE INICIO DO AGENDAMENTO DO PEDIDO
        Date scheduleDateStart = persistenOrder.getScheduleId().getScheduleStart();


        //--- CONFIGURACOES PARA DEFINIR A DATA QUE DEVEMOS INICIAR AS COBRANCAS ---//
        //ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
        c.setTime(scheduleDateStart);

        //VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
        c.add(Calendar.DATE, -daysToStart);

        //DATA DO AGENDAMENTO MENOS N DIAS NO FORMADO DATE. OU SEJA, A DATA QUE DEVE INICIAR AS TENTAVIDAS DE PAGAMENTO
        Date dateToStartPayment = c.getTime();


        //--- CONFIGURACOES PARA DEFINIR A DATA QUE DEVEMOS INICIAR AS NOTIFICACOES AO CLIENTE CASO FALHE ---//
        //ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
        c.setTime(scheduleDateStart);

        //VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
        c.add(Calendar.DATE, -daysBeforeStart);

        //UM DIA ANTES PARA NOTIFICAR AO CLIENTE SE DER ERRO NA RESERVA NO CARTAO E SUGERIR TROCAR PARA DINHEIRO
        Date dateToStartNotification = c.getTime();


        //TODO - VERIFICAR SE E UM DIA ANTES E TENTAR ENVIAR REQUEST, SE DER ERRO, LOGAR PARA DEPOIS NOTIFICAR NO APP
        if (sdf.format(now).equals(sdf.format(dateToStartNotification))) {
            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
            if (!sendPaymentRequest(payment)) {
                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                log.error("Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
            } else {
                success = true;
            }

            //TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
        } else if (sdf.format(now).equals(sdf.format(scheduleDateStart))) {
            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
            if (!sendPaymentRequest(payment)) {
                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                log.error("Erro ao efetuar a reserva do pagamento, seu agendamento não poderá prosseguir até que a" +
                        " forma de pagamento seja alterado para dinheiro");
            } else {
                success = true;
            }

            //TODO - URGENTE: DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
            //SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
        } else if (now.after(dateToStartPayment)) {
            //AQUI NAO FAZEMOS NENHUMA VERIFICACAO, POIS SE DER ERRO, AINDA TEREMOS OUTROS DIAS PARA TENTAR NOVAMENTE.
            if (sendPaymentRequest(payment)) {
                success = true;
            }

            //TODO - VAMOS FAZER ALGO CASO NAO ESTEJA EM NEMHUMA DAS CONDICOES ACIMA?
        } else {
            log.error("Fora do período defenido para iniciar a reserva do valor para pagamento. ORDER ID: " + persistenOrder.getIdOrder());
        }

        return success;

    }

    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    //TODO - ACHEI NECESSARIO CRIAR UM CRON PARA PEDIDOS QUE AINDA ESTAO EM READY2CHARGE POR ALGUM ERRO OCORRIDO
    @Scheduled(cron = "${order.payment.ready2charge.cron}")
    public void findReady2ChargeOrdersAndSendPaymentCron() throws Exception {

        // TODO: nao buscamos order, buscamos payments com status de falha
        ///List<Order> orderList = orderRepository.findByStatus(Order.Status.READY2CHARGE);

        /*for (Order order: orderList) {
            this.sendPaymentCapture(order);
        }*/

        List<Payment> paymentList = paymentRepository.findByOrderStatus(Order.Status.READY2CHARGE);

        for (Payment payment : paymentList) {
            this.sendPaymentCapture(payment);
        }

    }

    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    //TODO - ACHEI NECESSARIO CRIAR UM CRON PARA PEDIDOS QUE AINDA ESTAO EM ACCEPTED POR ALGUM ERRO OCORRIDO SEM PAYMENT
    //SE FOR USAR ESSE CRON, SERA NECESSARIO DESCOMENTAR AQUI E EM PROPERTIES
    //@Scheduled(cron = "${order.payment.accepted.cron}")
    private void findAcceptedOrdersAndSendPaymentCron() throws Exception {

        // TODO:  Nao buscamos order, buscamos por payment.
        /*List<Order> orderList = orderRepository.findByStatus(Order.Status.ACCEPTED);

		for (Order order: orderList) {
			//TODO - URGENTE: SE DER ALGUM ERRO NA HORA DE EFETUAR A RESERVA QUANDO MUDAR O STATUS PARA ACCEPTED...
			//AQUI FARIAMOS A RETENTATIVA NO CRON, POREM, EM QUAL SITUACAO DEVEMOS EFETUAR A TENTATIVA NOVAMENTE...
			//E EM QUAL DEVEMOS TOMAR OUTRAS ATITUDES, BEM COMO NAO PERMITIR A CONTINUIDADE DE ORDER SE NAO PAGAR EM DINHEIRO

            // TODO:  NAO tem como estar vazio. Quando chega um pedido de pagamento, eh nossa obrigacao salvar um payment junto com a order.
			if (order.getPaymentCollection().isEmpty()) {

				this.sendPaymentCapture(order);
			}

		}*/

        List<Payment> paymentList = paymentRepository.findByOrderStatus(Order.Status.ACCEPTED);

        for (Payment payment : paymentList) {
            this.sendPaymentCapture(payment);
        }

    }

    //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
    //BRANCH: RNF101
    @Scheduled(cron = "${order.payment.secheduled.cron}")
    public void findScheduledOrdersValidateAndSendPaymentRequest() throws Exception {

        List<Payment> paymentList = paymentRepository.findByOrderStatus(Order.Status.SCHEDULED);

        int daysToStart = Integer.parseInt(daysToStartPayment);
        int daysBeforeStart = Integer.parseInt(daysBeforeStartToNotification);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //INSTANCIAMOS O CALENDARIO
        Calendar c = Calendar.getInstance();

        //DATA ATUAL
        //EX.: 20/08/2017
        Date now = c.getTime();

        for (Payment payment : paymentList) {
            if (payment.getType() != null) {
                if (payment.getType().equals(Payment.Type.CC)) {
                    // TODO: requer refactoring quando comecarmos a fazer pagamento com 2 cartoes.

                    if (Payment.Status.PAGO_E_NAO_CAPTURADO ==
                            payment.getStatus()) {

                        Order order = payment.getOrder();

                        //PEGAMOS A DATA DE INICIO DO AGENDAMENTO DO PEDIDO
                        Date scheduleDateStart = order.getScheduleId().getScheduleStart();

                        //ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
                        c.setTime(scheduleDateStart);

                        //VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
                        c.add(Calendar.DATE, -daysToStart);

                        //DATA DO AGENDAMENTO MENOS N DIAS NO FORMADO DATE. OU SEJA, A DATA QUE DEVE INICIAR AS TENTAVIDAS DE PAGAMENTO
                        Date dateToStartPayment = c.getTime();


                        //--- CONFIGURACOES PARA DEFINIR A DATA QUE DEVEMOS INICIAR AS NOTIFICACOES AO CLIENTE CASO FALHE ---//
                        //ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
                        c.setTime(scheduleDateStart);

                        //VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
                        c.add(Calendar.DATE, -daysBeforeStart);

                        //UM DIA ANTES PARA NOTIFICAR AO CLIENTE SE DER ERRO NA RESERVA NO CARTAO E SUGERIR TROCAR PARA DINHEIRO
                        Date dateToStartNotification = c.getTime();

                        //TODO - VERIFICAR SE E UM DIA ANTES E TENTAR ENVIAR REQUEST, SE DER ERRO, LOGAR PARA DEPOIS NOTIFICAR NO APP
                        if (sdf.format(now).equals(sdf.format(dateToStartNotification))) {
                            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
                            if (!sendPaymentRequest(payment)) {
                                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                                log.error("Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
                            }

                            //TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
                        } else if (sdf.format(now).equals(sdf.format(scheduleDateStart))) {
                            //TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
                            if (!sendPaymentRequest(payment)) {
                                //AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
                                log.error("Erro ao efetuar a reserva do pagamento, seu agendamento não poderá prosseguir até que a" +
                                        " forma de pagamento seja alterado para dinheiro");
                            }

                            //TODO - URGENTE: DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
                            //SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
                        } else if (now.after(dateToStartPayment)) {
                            //AQUI NAO FAZEMOS NENHUMA VERIFICACAO, POIS SE DER ERRO, AINDA TEREMOS OUTROS DIAS PARA TENTAR NOVAMENTE.
                            sendPaymentRequest(payment);

                            //TODO - VAMOS FAZER ALGO CASO NAO ESTEJA EM NEMHUMA DAS CONDICOES ACIMA?
                        } else {
                            log.error("Fora do período defenido para iniciar a reserva do valor para pagamento. ORDER ID: " + order.getIdOrder());
                        }
                    }
                }
            } else {
                log.error("Pagamento sem PaymentType: paymentId{}, orderId{}",
                        payment.getId(), payment.getOrder().getIdOrder());
            }
        }

    }

    private Boolean sendPaymentRequest(Payment payment) throws Exception {

        ChargeResponse<Object> retornoTransacaoSuperpay = paymentService.reserve(new ChargeRequest<>(payment));

        return ResponseCode.SUCCESS.equals(retornoTransacaoSuperpay.getResponseCode());
    }

    public String delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Order> findBy(Order bindableQueryObject) {
        // return orderRepository.findTop10ByOrderByDateDesc();
        return orderRepository.findAll(Example.of(bindableQueryObject));
    }

    public List<Order> findByStatusNotCancelledOrClosed() {
        // return orderRepository.findTop10ByOrderByDateDesc();
        // return orderRepository.findAll(Example.of(bindableQueryObject));
        // return orderRepository.findAllCustom();
        // return orderRepository.findByQueryAnnotation();
        return orderRepository.findByStatusNotIn(Arrays.asList(CANCELLED, CLOSED, AUTO_CLOSED, EXPIRED));
    }

    public void abort(Order order) {
        Order o = orderRepository.findOne(order.getIdOrder());

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

        List<Order> onlyOrsersFinishedByProfessionals = orderRepository.findByStatus(Order.Status.SEMI_CLOSED);

        int count = onlyOrsersFinishedByProfessionals.size();

        for (Order o : onlyOrsersFinishedByProfessionals) {
            LocalDate lastUpdateLocalDate = o.getLastStatusUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate limitDate = LocalDate.now().minusDays(5);

            if (lastUpdateLocalDate.isBefore(limitDate)) {
                o.setStatus(Order.Status.AUTO_CLOSED);
                o.setLastStatusUpdate(Calendar.getInstance().getTime());
                orderRepository.save(o);

                balanceItemService.create(creditFromOrder(o));
            }
        }
        log.info("{} orders foram atualizada para {}.", count, Order.Status.AUTO_CLOSED.toString());
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

            ProfessionalCategory professionalCategory = professionalCategoryRepository.findOne(idProfessionalCategory);

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

        Order persistentOrder = orderRepository.findOne(idOrder);

        Professional professional;
        ProfessionalCategory professionalCategory = receivedOrder.getProfessionalCategory();

        if (professionalCategory == null) {
            professionalCategory = persistentOrder.getProfessionalCategory();
        }

        professional = professionalCategory.getProfessional();

        // So a Order gravada no banco eh que sabe dizer se a order eh agendada ou nao.
        if (persistentOrder.isScheduled()) {
            // Se for nulo, entao nao eh intencao do cliente atualizar o agendamento, logo, nao validamos.
            if(receivedOrder.getScheduleId() != null) {
                validateScheduleEndDate(receivedOrder);
            }
        } else {
            // Pedidos agendados nao precisam dessas validações.
            validateIfThereAreRunningOrders(receivedOrder, professional);
            validateIfThereAreOrderToSameProfessionalAndSameService(receivedOrder, professionalCategory);
        }
    }

    private void validateIfThereAreOrderToSameProfessionalAndSameService(Order receivedOrder, ProfessionalCategory professionalCategory) {

        if (Order.Status.OPEN.equals(receivedOrder.getStatus()) ||
                Order.Status.ACCEPTED.equals(receivedOrder.getStatus()) ||
                Order.Status.INPROGRESS.equals(receivedOrder.getStatus())) {

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

    private void validateIfThereAreRunningOrders(Order receivedOrder, Professional professional) {

        //ESSA VALIDACAO SO DEVERA SER EXECUTADA NOS STATUS ABAIXO
        if (Order.Status.OPEN.equals(receivedOrder.getStatus()) ||
                Order.Status.ACCEPTED.equals(receivedOrder.getStatus()) ||
                Order.Status.INPROGRESS.equals(receivedOrder.getStatus())) {

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

    private void validateBusyScheduled2(Order order) throws ValidationException {

        Date newOrderScheduleStart = order.getScheduleId().getScheduleStart();
        newOrderScheduleStart.getTime();

        ProfessionalCategory ps = order.getProfessionalCategory();
        Professional p = ps.getProfessional();

        Long idProfessional = p.getIdProfessional();
        Date pretendedStart = order.getScheduleId().getScheduleStart();
        //Date pretendedEnd = order.getScheduleId().getScheduleEnd();
		/*
		Aplico mais filtros na query e trago só as orders que interessa.

		Eh sempre a melhor opcao deixar os filtros na responsabilidade do banco.
		 */
        List<Order> orders = orderRepository.findScheduledOrdersByProfessionalWithScheduleConflict(idProfessional, pretendedStart);
        // A query busca tudo que esta conflitando no banco. Se houver resultado, eh pq tem conflito.
        if (!orders.isEmpty()) {

            throw new ValidationException("Ja existe agendamento marcado no horario de  " + newOrderScheduleStart.toString());
        } else {
            newOrderScheduleStart = order.getScheduleId().getScheduleStart();
        }
    }

    @Scheduled(cron = "${order.expired.cron}")
    public void updateStatusOpenToExpired() {

        List<Order> onlyOrsersFinishedByProfessionals = orderRepository.findByStatus(Order.Status.OPEN);

        int count = 0;

        // TODO: colocar esses minutos no properties
        LocalDateTime oneHourAfter = LocalDateTime.now().plusHours(1);

        for (Order o : onlyOrsersFinishedByProfessionals) {
            LocalDateTime orderCreationDate = o.getLastStatusUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


            if (orderCreationDate.isAfter(oneHourAfter)) {
                o.setStatus(Order.Status.EXPIRED);
                o.setLastStatusUpdate(Calendar.getInstance().getTime());
                orderRepository.save(o);
                count++;
            }
        }
        log.info("{} orders foram atualizada para {}.", count, Order.Status.EXPIRED.toString());
    }

    public void validateScheduleEndDate(Order receivedOrder) throws OrderValidationException {

        if (receivedOrder.getScheduleId().getScheduleEnd() == null
                && Order.Status.SCHEDULED.equals(receivedOrder.getStatus())) {
            throw new OrderValidationException(ResponseCode.INVALID_SCHEDULE_END, "Precisa de data final no agendamento");
        }

    }
}
