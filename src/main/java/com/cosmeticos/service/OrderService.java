package com.cosmeticos.service;

import com.cosmeticos.commons.ErrorCode;
import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.*;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.cosmeticos.model.Order.Status.*;
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
	private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerResponsitory;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private PriceRuleRepository priceRuleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VoteService voteService;

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
            throw new OrderValidationException(ErrorCode.INVALID_PAYMENT_CONFIGURATION, "Nao foi configurado objeto payment.");
        }
        else {
            validatedPayment = paymentCollection.stream().findFirst().get();
        }

        if(persistentProfessionalCategory == null)
        {
            throw new OrderValidationException(
                    ErrorCode.INVALID_PROFESSIONAL_CATEGORY,
                    "ProfessionalCategoryId invalido: " + receivedProfessionalCategoryId);

        }

        if (paymentCollection.isEmpty()) {
            throw new OrderValidationException(ErrorCode.INVALID_PAYMENT_CONFIGURATION, "Nao foi configurado objeto payment.");
        }
        else {
            validatedPayment = paymentCollection.stream().findFirst().get();
        }

        // Validamos o Payment recebido para que o cron nao tenha que descobrir que o payment esta mal configurado.
        validateAndApplyPaymentPriceRule(validatedPayment);

        // Valida se o usuario que paga com cartao realmente possui cartao cadastrado.
        validateAndApplyPaymentCreditcard(persistentCustomer, validatedPayment);


        /********************************************************/
        /*****   EXECUCAO      **********************************/
        /********************************************************/

        Order order = new Order();
        order.setScheduleId(receivedOrder.getScheduleId());
        order.setIdLocation(receivedOrder.getIdLocation());
        order.setIdCustomer(persistentCustomer);
        order.setDate(Calendar.getInstance().getTime());
        order.setLastUpdate(order.getDate());
        order.setStatus(Order.Status.OPEN); // O STATUS INICIAL SERA DEFINIDO COMO CRIADO
        order.setProfessionalCategory(persistentProfessionalCategory);
        order.setExpireTime(new Date(order.getDate().getTime() +

                // 6 horas de validade
                21600000));
        order.addPayment(validatedPayment);

        Order newOrder = orderRepository.save(order);
        // Buscando se o customer que chegou no request esta na wallet

        addInWallet(persistentProfessionalCategory.getProfessional(), persistentCustomer);

        return newOrder;
    }

    /**
     * Apensar de ser uma collection, so trabalharemos com 1 Payment inicialmente, o qual este metodo estara retornando.
     *
     * @param receivedPayment
     * @return
     */
    private void validateAndApplyPaymentPriceRule(Payment receivedPayment) {

        PriceRule chosenPriceRule = receivedPayment.getPriceRule();

        if (chosenPriceRule == null) {
            throw new OrderValidationException(ErrorCode.INVALID_PAYMENT_CONFIGURATION, "Regra de preco nao foi enviada pelo cliente");
        }
        else
        {
            chosenPriceRule = priceRuleRepository.findOne(chosenPriceRule.getId());

            /**
             * Buscamos o pricerule no banco pq o q chega no request é so o ID.
             */
            receivedPayment.setPriceRule(chosenPriceRule);

            MDC.put("price: ", String.valueOf(chosenPriceRule.getPrice()));
        }
    }

    /**
     * Valida o cartao de credito para que quando a cron rode no dia de cobrar, ja estara garantido que o cliente possui
     * cartao registrado.
     *
     * @param persistentCustomer
     * @param receivedPayment
     */
    private void validateAndApplyPaymentCreditcard(Customer persistentCustomer, Payment receivedPayment) {
        if (Payment.Type.CC.equals(receivedPayment.getType())) {
            Collection<CreditCard> persistentCreditCards = persistentCustomer.getUser().getCreditCardCollection();

            if (persistentCreditCards.isEmpty()) {
                throw new OrderValidationException(
                        ErrorCode.INVALID_PAYMENT_TYPE,
                        "Cliente solicitou compra por cartao de credito mas nao possui cartao de credito cadastrado."
                );
            }
            else
            {
                Optional<CreditCard> cc = persistentCreditCards.stream().findFirst();

                receivedPayment.setCreditCard(cc.get());
            }
        }
    }

    /**
     * Adiciona o cliente na carteira do profissional se as condicoes forem satisfeitas.
     *
     * @param professional
     * @param customer
     */
    private void addInWallet(Professional professional, Customer customer) {
        Optional<Wallet> optionalWallet = Optional.ofNullable(professional.getWallet());
        Optional<Customer> customerInWallet = Optional.empty();

        // Verificando se pelo menos existe a wallet.
        if (optionalWallet.isPresent()) {
            customerInWallet = professional.getWallet().getCustomers()
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
                if (o.getProfessionalCategory().getProfessional().getIdProfessional() == professional
                        .getIdProfessional()) {
                    totalOrders++;
                }
            }

            if (totalOrders >= 2) {
                if (professional.getWallet() == null) {
                    professional.setWallet(new Wallet());
                    professional.getWallet().setProfessional(professional);
                }
                professional.getWallet().getCustomers().add(customer);
                professionalRepository.save(professional);
            }
        }
    }

    public Order update(OrderRequestBody request) throws Exception {
        Order receivedOrder = request.getOrder();// Po, ta pegando o q veio do request.. ate agora . nada anormal...
        Order persistentOrder = orderRepository.findOne(receivedOrder.getIdOrder());

		MDC.put("previousOrderStatus", String.valueOf(receivedOrder.getStatus()));

		//ADICIONEI ESSA VALIDACAO DE TENTATIVA DE ATUALIZACAO DE STATUS PARA O MESMO QUE JA ESTA EM ORDER
		if(persistentOrder.getStatus() == receivedOrder.getStatus()) {
			//throw new IllegalStateException("PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
			throw new OrderValidationException(ErrorCode.INVALID_ORDER_STATUS, "PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
		}

        if (Order.Status.CLOSED == persistentOrder.getStatus()) {
            throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
        }

        if (Order.Status.EXPIRED == persistentOrder.getStatus()) {
            throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
        }

        //TODO - SE ORDER NO BANCO FOR READY2CHARGE E PAGAMENTO EM DINHEIRO, ENTAO MUDAMOS O STATUS PARA O SOLICITADO???
        //NAO ESTOU ENTENDENDO ISSO!!!
        //TODO - VERIFICAR POIS SER FOR ENVIADO CLOSED PODE BATER AQUI E GERAR PROBLEMA
        // ACCEPTED ou READY2CHARGE?  Deivison quer que pague so apos executar o servico
        // Garry: Ta estranho mesmo.. vamos apagar esta instrucao
        if (Order.Status.READY2CHARGE == persistentOrder.getStatus()) {

            Payment payment = persistentOrder.getPaymentCollection()
                    .stream()
                    .findFirst()
                    .get();

            if (Payment.Type.CASH.equals(payment.getType())) {
                persistentOrder.setStatus(receivedOrder.getStatus());
            }
        }

        if (!isEmpty(receivedOrder.getDate())) {
            persistentOrder.setDate(receivedOrder.getDate());
        }
        //AQUI SETAMOS O STATUS VINDO DO REQUEST
        if (!isEmpty(receivedOrder.getStatus())) {
            persistentOrder.setStatus(receivedOrder.getStatus());
        }

        /* Removendo isso pq senao estou permitindo que uma Order mude de customer, o que nao eh permitido.
        Na verdade deveria haver uma validacao de que se o customer da Order que veio do request eh diferente
        do customer que esta na Order gravada no banco.
        if (!isEmpty(receivedOrder.getIdCustomer())) {
            persistentOrder.setIdCustomer(receivedOrder.getIdCustomer());
        }*/
        if (!isEmpty(receivedOrder.getIdCustomer())) {
            if(receivedOrder.getIdCustomer().getIdCustomer() != persistentOrder.getIdCustomer().getIdCustomer())
            {
                throw new OrderValidationException(ErrorCode.ILLEGAL_ORDER_OWNER_CHANGE,
                        "Nao se pode alterar o customer de uma order para outro customer");
            }
        }

        if (!isEmpty(receivedOrder.getIdLocation())) {
            persistentOrder.setIdLocation(receivedOrder.getIdLocation());
        }

        if (receivedOrder.getScheduleId() != null) {

            Schedule receivedSchedule = receivedOrder.getScheduleId();
            Schedule persistentSchedule = persistentOrder.getScheduleId();

            if(persistentSchedule == null ) {

                persistentOrder.setScheduleId(receivedSchedule);
            }
            else
            {
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

        orderRepository.save(persistentOrder);

        //AQUI TRATAMOS O STATUS ACCEPTED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
        // Utilizamos a order persistente pois ela possui TODOS os atributos setados
        if (receivedOrder.getStatus() == Order.Status.ACCEPTED) {
            this.sendPaymentRequest(persistentOrder);
        }

        //TIVE QUE COMENTAR A VALIDACAO ABAIXO POIS ESTAVA DANDO O ERRO ABAIXO:
        //QUANDO VAMOS ATUALIZAR PARA SCHEDULED, AINDA NAO TEMOS OS DADOS QUE VAO SER ATUALIZADOS
        //-- VALIDANDO O COMENTARIO ACIMA --//
        //AQUI TRATAMOS O STATUS SCHEDULED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
        if (receivedOrder.getStatus() == Order.Status.SCHEDULED) {
            this.validateScheduledAndsendPaymentRequest(persistentOrder);
        }

		//TODO - CRIAR METODO DE VALIDAR PAYMENT RESPONSE LANCANDO ORDER VALIDATION EXCEPTION COM...
		//HTTPSTATUS DEFINIDO PARA CADA STATUS DE PAGAMENTO DA SUPERPAY
		//CARD: https://trello.com/c/fyPMjNJI/113-adequar-status-do-pagamento-do-superpay-aos-nossos-status-da-order
		//BRANCH: RNF101

        //ACHEI MELHOR FAZER UMA NOVA VERIFICACAO APOS SALVAR, POIS PRECISAMOS TER ARMAZENADO QUANDO MUDAMOS O STATUS
        //PARA READY2CHARGE E QUANDO FIZEMOS A CAPTURA. POIS COMO ESTAVA ANTES NAO TINHAMOS O REGISTRO DE READY2CHARGE
        //POIS QUANDO ERA ESTE STATUS, JA ENVIAMOS A CAPTURA E, LOGO APOS A CAPTURA, O CORRETO EH MUDAR O STATUS PARA PAYD
        if(persistentOrder.getStatus() == Order.Status.READY2CHARGE) {
            Payment payment = persistentOrder.getPaymentCollection()
                    .stream()
                    .findFirst()
                    .get();

            if (Payment.Type.CC.equals(payment.getType())) {
                //AQUI TRATAMOS O STATUS READY2CHARGE QUE VAI NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
                if(this.sendPaymentCapture(persistentOrder)) {

                    //ADICIONEI O QUE SEGUE ABAIXO POIS PRECISAMOS TER O REGISTRO DA ATUALIZACAO DOS DOIS STATUS
                    //PRIMEIRO READY2CHARGE E, LOGO EM SEGUIDA, SE A CAPTURA FOR FEITA COM SUCESSO, MUDAMOS PARA PAID
                    //OBS.: COMO NAO TEMOS O STATUS PAID, MUDEI PARA SEMI_CLOSED
                    //persistentOrder.setStatus(Order.Status.SEMI_CLOSED);

                    //SALVAMOS NOVAMENTE PARA ATUALIZAR O STATUS DE READY2CHARGE PARA ALGUM QUE IDENTIFIQUE QUE FOI PAGO
                    orderRepository.save(persistentOrder);
                }
            }
        }

        return persistentOrder;
    }

    private void applyVote(Order receivedOrder, Order persistentOrder) {

        if (receivedOrder.getStatus() == Order.Status.SEMI_CLOSED) {

            // Aplicado ao Customer quando o professional encerra o servico

            User persistentUser = persistentOrder.getIdCustomer().getUser();

            User receivedUser = receivedOrder.getIdCustomer().getUser();

            Vote receivedvote = receivedUser.getVoteCollection().stream().findFirst().get();

            addVotesToUser(persistentUser, receivedvote);

            MDC.put("customerVote", String.valueOf(receivedvote.getValue()));

        }
        else if(receivedOrder.getStatus() == Order.Status.READY2CHARGE) {

            // Aplicado ao Professional quando o customer confirma realização do servico e avalia o professional

            ProfessionalCategory persistentProfessionalCategory = persistentOrder.getProfessionalCategory();
            User persistentUser = persistentProfessionalCategory.getProfessional().getUser();

            ProfessionalCategory receivedProfessionalCategory = receivedOrder.getProfessionalCategory();
            User receivedUser = receivedProfessionalCategory.getProfessional().getUser();

            // tm algo errado aki...  eu nomeio os objetos com receivedXXX pra saber o q veio do request e persistentXXX pra saber o q veio do banco
            // esse erro de lazy só faz sentido se o objeto veio do banco. Acho q em algum momento rolou confussao e  o receivedXX recebeu algo
            // persistente. Vo dar uma debugada reversa rsrsjah eh
            Vote receivedvote = receivedUser.getVoteCollection().stream().findFirst().get();

            addVotesToUser(persistentUser, receivedvote);

            MDC.put("professionalVote", String.valueOf(receivedvote.getValue()));
        }
    }

    private void addVotesToUser(User persistentUser, Vote receivedVote) {

            persistentUser.addVote(receivedVote);
            voteService.create(receivedVote);

    }

	//CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
	//BRANCH: RNF101
	//TODO - ACHO QUE PRECISA DE MAIS VALIDACOES, BEM COMO QUANDO DER ERRO DE CONSULTA OU CAPTURA POR 404, 500 E ETC.
	private Boolean sendPaymentCapture(Order order) throws JsonProcessingException, URISyntaxException, OrderValidationException {

		Boolean paymentCapture = paymentService.validatePaymentStatusAndSendCapture(order);

		//DEIXEI RESERVADO ABAIXO PARA FAZER ALGUMA COISA, CASO NAO TENHA SUCESSO NA CAPTURA QUANDO FOR READY2CHARGE
		//POIS O CRON PEGA TODOS OS READY2CHARGE E ENVIA PARA ESTE METODO, SE DER ALGUM ERRO, TEMOS QUE FAZER ALGO
		//CASO CONTRARIO, VAI FICAR TENTANDO CAPTURAR E NUNCA VAI CONSEGUIR ATE CADUCAR!
		/*
		if(!paymentCapture) {

		}
		*/

		return paymentCapture;
	}

	private Boolean validateScheduledAndsendPaymentRequest(Order persistenOrder) throws Exception {


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
		if(sdf.format(now).equals(sdf.format(dateToStartNotification))) {
			//TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
			if(!sendPaymentRequest(persistenOrder)) {
				//AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
				log.error("Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
			} else {
			    success = true;
            }

		//TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
		} else if(sdf.format(now).equals(sdf.format(scheduleDateStart))) {
			//TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
			if(!sendPaymentRequest(persistenOrder)) {
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
			if(sendPaymentRequest(persistenOrder)) {
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
	private void findReady2ChargeOrdersAndSendPaymentCron() throws Exception {

		List<Order> orderList = orderRepository.findByStatus(Order.Status.READY2CHARGE);

		for (Order order: orderList) {
			this.sendPaymentCapture(order);
		}

	}

	//CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
	//BRANCH: RNF101
	//TODO - ACHEI NECESSARIO CRIAR UM CRON PARA PEDIDOS QUE AINDA ESTAO EM ACCEPTED POR ALGUM ERRO OCORRIDO SEM PAYMENT
	//SE FOR USAR ESSE CRON, SERA NECESSARIO DESCOMENTAR AQUI E EM PROPERTIES
	//@Scheduled(cron = "${order.payment.accepted.cron}")
	private void findAcceptedOrdersAndSendPaymentCron() throws Exception {

		List<Order> orderList = orderRepository.findByStatus(Order.Status.ACCEPTED);

		for (Order order: orderList) {
			//TODO - URGENTE: SE DER ALGUM ERRO NA HORA DE EFETUAR A RESERVA QUANDO MUDAR O STATUS PARA ACCEPTED...
			//AQUI FARIAMOS A RETENTATIVA NO CRON, POREM, EM QUAL SITUACAO DEVEMOS EFETUAR A TENTATIVA NOVAMENTE...
			//E EM QUAL DEVEMOS TOMAR OUTRAS ATITUDES, BEM COMO NAO PERMITIR A CONTINUIDADE DE ORDER SE NAO PAGAR EM DINHEIRO
			if (order.getPaymentCollection().isEmpty()) {
				this.sendPaymentCapture(order);
			}

		}

	}

	//CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
	//BRANCH: RNF101
	@Scheduled(cron = "${order.payment.secheduled.cron}")
	private void findScheduledOrdersValidateAndSendPaymentRequest() throws Exception {

		List<Order> orderList = orderRepository.findByStatus(Order.Status.SCHEDULED);

		int daysToStart = Integer.parseInt(daysToStartPayment);
		int daysBeforeStart = Integer.parseInt(daysBeforeStartToNotification);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		//INSTANCIAMOS O CALENDARIO
		Calendar c = Calendar.getInstance();

		//DATA ATUAL
		//EX.: 20/08/2017
		Date now = c.getTime();

		for (Order order: orderList) {

		    // TODO: requer refactoring quando comecarmos a fazer pagamento com 2 cartoes.
			if(Payment.Status.PAGO_E_NAO_CAPTURADO ==
                    order.getPaymentCollection()
                            .stream()
                            .findFirst()
                            .get()
                            .getStatus()) {

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
				if(sdf.format(now).equals(sdf.format(dateToStartNotification))) {
					//TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
					if(!sendPaymentRequest(order)) {
						//AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
						log.error("Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
					}

					//TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
				} else if(sdf.format(now).equals(sdf.format(scheduleDateStart))) {
					//TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
					if(!sendPaymentRequest(order)) {
						//AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
						log.error("Erro ao efetuar a reserva do pagamento, seu agendamento não poderá prosseguir até que a" +
								" forma de pagamento seja alterado para dinheiro");
					}

					//TODO - URGENTE: DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
					//SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
				} else if (now.after(dateToStartPayment)) {
					//AQUI NAO FAZEMOS NENHUMA VERIFICACAO, POIS SE DER ERRO, AINDA TEREMOS OUTROS DIAS PARA TENTAR NOVAMENTE.
					sendPaymentRequest(order);

					//TODO - VAMOS FAZER ALGO CASO NAO ESTEJA EM NEMHUMA DAS CONDICOES ACIMA?
				} else {
					log.error("Fora do período defenido para iniciar a reserva do valor para pagamento. ORDER ID: " + order.getIdOrder());
				}
			}
		}

	}

	//TODO - ACHO QUE ESSE METODO MERECE UMA ATENCAO ESPECIAL PARA MELHORIAS NOS SEUS TRATAMENTOS DE ERROS E VALIDACOES
	private Boolean sendPaymentRequest(Order orderRequest) throws ParseException, JsonProcessingException, Exception {

		Boolean senPaymentStatus = false;

        Optional<RetornoTransacao> retornoTransacaoSuperpay = paymentService.sendRequest(orderRequest);

        if (retornoTransacaoSuperpay.isPresent()) {

            Integer statusTransacao = retornoTransacaoSuperpay.get().getStatusTransacao();

            MDC.put("superpayStatusTrasacao", String.valueOf(statusTransacao));

            //VALIDAMOS SE VEIO O STATUS QUE ESPERAMOS
            //1 = Pago e Capturado | 2 = Pago e não Capturado (O CORRETO SERIA 2, POIS FAREMOS A CAPTURA POSTERIORMENTE)
            switch (statusTransacao)
            {
                case 1:
                case 2:

				//SE FOR PAGO E CAPTURADO, HOUVE UM ERRO NAS DEFINICOES DA SUPERPAY, MAS FOI FEITO O PAGAMENTO
				if (retornoTransacaoSuperpay.get().getStatusTransacao() == 1) {
					log.warn("Pedido retornou como PAGO E CAPTURADO, mas o correto seria PAGO E 'NÃO' CAPTURADO.");
					senPaymentStatus = true;
				}

				//SE FOR PAGO E NAO CAPTURADO, CORREU TUDO CERTO!
				if (retornoTransacaoSuperpay.get().getStatusTransacao() == 2) {
				    senPaymentStatus = true;
				}

                //SE TRANSACAO JA PAGA, ESTAMOS TENTANDO EFETUAR O PAGAMENTO DE UM PEDIDO JA PAGO ANTERIORMENTE
                if (retornoTransacaoSuperpay.get().getStatusTransacao() == 31) {
                    log.warn("Pedido retornou como TRANSACAO JA PAGA, possível tentativa de pagamento em duplicidade.");
					senPaymentStatus = true;
                }

                    //TODO - URGENTE
                    //ENVIAMOS OS DADOS DO PAGAMENTO EFETUADO NA SUPERPAY PARA SALVAR O STATUS DO PAGAMENTO
                    //OBS.: COMO ESSE METODO AINDA NAO FOI IMPLEMENTADO, ELE ESTA RETORNANDO BOOLEAN
                    Boolean updateStatusPagamento = paymentService.updatePaymentStatus(retornoTransacaoSuperpay.get());

                    if (!updateStatusPagamento) {
                        //TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO DER UM ERRO AO ATUALIZAR O STATUS DO PAGAMENTO
                        log.error("Erro ao salvar o status do pagamento");
                        throw new RuntimeException("Erro salvar o status do pagamento");
                    }

                    //SE NAO VIER O STATUS DO PAGAMENTO 1 OU 2, VAMOS LANCAR UMA EXCECAO COM O STATUS VINDO DA SUPERPAY
                    break;

                //TIVE QUE ADICIONAR O STATUS 31 (Transação já Paga), ENQUANTO NAO FAZEMOS AS VALIDACOES DOS STATUS
                case 31:
                    throw new OrderValidationException(
                    		ErrorCode.GATEWAY_DUPLICATE_PAYMENT,
                            "Gateway de pagamento informou que a trasacacao ja consta como paga."
                    );

                default:
                    //TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO O STATUS FOR OUTRO
                    //TODO - SE FOR MESMO RETORNAR O CODIGO DO STATUS DO PAGAMENTO, PODERIA RETORNAR A MENSAGEM, NAO O CODIGO
                    throw new Exception("Erro ao efetuar o pagamento: " + retornoTransacaoSuperpay.get().getStatusTransacao());

            }

        } else {
            //TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO DER UM ERRO NO PAGAMENTO NA SUPERPAY
            log.error("Erro ao enviar a requisição de pagamento");
            throw new Exception("Erro ao enviar a requisição de pagamento");
        }

		return senPaymentStatus;

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
                Arrays.asList(CANCELLED, AUTO_CLOSED, CLOSED), //
                email);
    }

    public class ValidationException extends Exception {
        public ValidationException(String s) {
            super(s);
        }
    }

    @Scheduled(cron = "${order.unfinished.cron}")
    public void updateStatus() {

        List<Order> onlyOrsersFinishedByProfessionals = orderRepository.findByStatus(Order.Status.SEMI_CLOSED);

        int count = onlyOrsersFinishedByProfessionals.size();

        for (Order o : onlyOrsersFinishedByProfessionals) {
            LocalDate lastUpdateLocalDate = o.getLastUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate limitDate = LocalDate.now().minusDays(5);

            if (lastUpdateLocalDate.isBefore(limitDate)) {
                o.setStatus(Order.Status.AUTO_CLOSED);
                orderRepository.save(o);
            }
        }
        log.info("{} orders foram atualizada para {}.", count, Order.Status.AUTO_CLOSED.toString());
    }

    /**
     * @param order TODO: Ta escroto essas duas excecoes fazendo amesma coisa.. depois arrumo.. tem q ficar so a
     *              OrderValidationException mas tem q alterar  a classe la ainda
     * @throws OrderValidationException
     * @throws ValidationException
     */
    public void validateCreate(Order order) throws OrderValidationException, ValidationException {//

        if (order.isScheduled()) {
            validateScheduleEndDate(order);

            // Aqui vc escolhe o que quer usar.
            //validateBusyScheduled2(order);
            validateBusyScheduled1(order);
        } else {
            Long idProfessionalCategory = order.getProfessionalCategory().getProfessionalCategoryId();

            ProfessionalCategory professionalCategory = professionalCategoryRepository.findOne(idProfessionalCategory);

            Professional professional = professionalCategory.getProfessional();

            MDC.put("idProfessional: ", String.valueOf(professional.getIdProfessional()));
            MDC.put("professionalUserStatus: ", String.valueOf(professional.getUser().getStatus()));

            List<Order> orderList = orderRepository.findRunningOrdersByProfessional(
                    professional.getIdProfessional(), 0L);

            if (!orderList.isEmpty()) {
                // Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
                throw new OrderValidationException(ErrorCode.DUPLICATE_RUNNING_ORDER, "profissional ja esta com outra order em andamento.");
            }
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
        if (persistentOrder.isScheduled() &&
                // Se for nulo, entao nao eh intencao do cliente atualizar o agendamento, logo, nao validamos.
                receivedOrder.getScheduleId() != null) {
            validateScheduleEndDate(receivedOrder);

            // Nao precisa validar pq so valida scheduleStart, que ja foi validado no PUT
            //validateBusyScheduled1(receivedOrder);
        }


        List<Order> orderList = orderRepository.findRunningOrdersByProfessional(
                professional.getIdProfessional(), idOrder);

        if (!orderList.isEmpty()) {
            // Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
            throw new OrderValidationException(ErrorCode.DUPLICATE_RUNNING_ORDER, "profissional ja esta com outra order em andamento.");
        }

    }

    private void validateBusyScheduled1(Order order) throws ValidationException, OrderValidationException {

        Date newOrderScheduleStart = order.getScheduleId().getScheduleStart();
        if (newOrderScheduleStart == null) {
            throw new OrderValidationException(ErrorCode.INVALID_SCHEDULE_START, "Data de inicio de agendamento vazia");
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

                    if (existingOrderStart.before(newOrderScheduleStart) &&
                            existingOrderEnd.after(newOrderScheduleStart)) {
                        // conflitou!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        throw new OrderValidationException(ErrorCode.CONFLICTING_SCHEDULES, "Ja existe agendamento marcado no horario de  " + newOrderScheduleStart.toString());
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
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        for (Order o : onlyOrsersFinishedByProfessionals) {
            LocalDateTime orderCreationDate = o.getLastUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();


            if (orderCreationDate.isBefore(tenMinutesAgo)) {
                o.setStatus(Order.Status.EXPIRED);
                orderRepository.save(o);
                count++;
            }
        }
        log.info("{} orders foram atualizada para {}.", count, Order.Status.EXPIRED.toString());
    }

    public void validateScheduleEndDate(Order receivedOrder) throws OrderValidationException {

        if (receivedOrder.getScheduleId().getScheduleEnd() == null
                && Order.Status.SCHEDULED.equals(receivedOrder.getStatus())) {
            throw new OrderValidationException(ErrorCode.INVALID_SCHEDULE_END, "Precisa de data final no agendamento");
        }

    }
}
