package com.cosmeticos.service;

import static com.cosmeticos.model.Order.Status.AUTO_CLOSED;
import static com.cosmeticos.model.Order.Status.CANCELLED;
import static com.cosmeticos.model.Order.Status.CLOSED;
import static com.cosmeticos.model.Order.Status.EXPIRED;
import static com.cosmeticos.validation.OrderValidationException.Type.INVALID_ORDER_STATUS;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.cosmeticos.model.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.controller.PaymentController;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.validation.OrderValidationException;
import com.cosmeticos.validation.OrderValidationException.Type;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by matto on 17/06/2017.
 */
@Slf4j
@org.springframework.stereotype.Service
public class OrderService {

	@Value("${order.payment.secheduled.startDay}")
	private String daysToStartPayment;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerResponsitory;

	@Autowired
	private ProfessionalRepository professionalRepository;

	@Autowired
	private PenaltyService penaltyService;

	// TODO: Nao se acessa o controller por autowired mas sim seu Service
	@Autowired
	private PaymentController paymentController;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ProfessionalCategoryRepository professionalCategoryRepository;

	public Optional<Order> find(Long idOrder) {
		return Optional.of(orderRepository.findOne(idOrder));
	}

	public Order create(OrderRequestBody orderRequest) throws OrderValidationException {

		PriceRule chosenPriceRule = orderRequest.getPriceRule();

		Order receivedORder = orderRequest.getOrder();

		if(chosenPriceRule == null)
		{
			throw new OrderValidationException(Type.UNDEFINED_PRICE_RULE, "Regra de preco nao foi enviada pelo cliente");
		}
		
		MDC.put("price", String.valueOf(chosenPriceRule.getPrice()));
		
		ProfessionalCategory receivedProfessionalCategory = receivedORder.getProfessionalCategory();

		/*
		 * Buscando o cliente que foi informado no request. Do que chega no request, so
		 * preciso confiar no ID dessas entidades. Os outros atributos as vezes podem
		 * nao vir preenchidos ou preenchidos de qualquer forma so pra nao ser barrado
		 * pelo @Valid, portanto devemos buscar o objeto real no banco.
		 */
		Customer customer = customerResponsitory.findOne(receivedORder.getIdCustomer().getIdCustomer());

		// Checaremos Order.PAymentTypeCreditCard creditCard = orderRequest.getOrder().getCreditCardCollection().iterator().next();

		ProfessionalCategory professionalCategory =
				professionalCategoryRepository.findOne(receivedProfessionalCategory.getProfessionalCategoryId());

		Professional receivedProfessional = professionalCategory.getProfessional();
		Category receivedCategory = professionalCategory.getCategory();

		// Conferindo se o ProfessionalServices recebido realmente esta associado ao
		// Profissional em nossa base.
		Optional<ProfessionalCategory> persistentProfessionalServices = receivedProfessional.getProfessionalCategoryCollection()
				.stream().filter(ps -> ps.getCategory().getIdCategory()
						.equals(receivedCategory.getIdCategory()))
				.findFirst();


			if (persistentProfessionalServices.isPresent()) {

				Payment.Type paymentType = null;
				CreditCard creditCard  = null;

				if(Order.PayType.CREDITCARD.equals(receivedORder.getPaymentType())) {
					Collection<CreditCard> creditCards = customer.getUser().getCreditCardCollection();

					if (creditCards.isEmpty()) {
						throw new OrderValidationException(
								Type.INVALID_PAYMENT_TYPE,
								"Cliente solicitou compra por cartao de credito mas nao possui cartao de credito cadastrado."
						);
					}
					else
					{
						paymentType = Payment.Type.CC;
						creditCard = creditCards.stream().findFirst().get();
					}
				}
				else
				{
					paymentType = Payment.Type.CASH;
				}

				Payment payment = new Payment();
				payment.setType(paymentType);
				payment.setPriceRule(chosenPriceRule);
				payment.setCreditCard(creditCard);
				
				Order order = new Order();
				order.setScheduleId(receivedORder.getScheduleId());
				order.setIdLocation(receivedORder.getIdLocation());
				order.setIdCustomer(customer);
				order.setDate(Calendar.getInstance().getTime());
				order.setLastUpdate(order.getDate());
				order.setPaymentType(receivedORder.getPaymentType());
				order.setStatus(Order.Status.OPEN); // O STATUS INICIAL SERA DEFINIDO COMO CRIADO
				order.setProfessionalCategory(persistentProfessionalServices.get());
				order.setExpireTime(new Date(order.getDate().getTime() +

						// 6 horas de validade
						21600000));
				order.addPayment(payment);


				Order newOrder = orderRepository.save(order);
				// Buscando se o customer que chegou no request esta na wallet

				addInWallet(receivedProfessional, customer);

				return newOrder;
			} else {
				throw new OrderValidationException(Type.INVALID_PROFESSIONAL_CATEGORY_PAIR,
						"Service [id=" + receivedProfessionalCategory.getCategory().getIdCategory()
								+ "] informado no requst nao esta associado ao profissional " + "id=["
								+ receivedProfessional.getIdProfessional() + "] em nosso banco de dados.");
			}


	}

	/**
	 * Adiciona o cliente na carteira do profissional se as condicoes forem satisfeitas.
	 * @param professional
	 * @param customer
	 */
	private void addInWallet(Professional professional, Customer customer) {
		Optional<Wallet> optionalWallet = Optional.ofNullable(professional.getWallet());
		Optional<Customer> customerInWallet = Optional.empty();

		// Verificando se pelo menos existe a wallet.
		if(optionalWallet.isPresent())
		{
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

			if (totalOrders >= 2)
			{
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
		Order orderRequest = request.getOrder();
		Order order = orderRepository.findOne(orderRequest.getIdOrder());

		//ADICIONEI ESSA VALIDACAO DE TENTATIVA DE ATUALIZACAO DE STATUS PARA O MESMO QUE JA ESTA EM ORDER
		if(order.getStatus() == orderRequest.getStatus()) {
			//throw new IllegalStateException("PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
			throw new OrderValidationException(INVALID_ORDER_STATUS, "PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
		}

		if (Order.Status.CLOSED == order.getStatus()) {
			throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
		}

		if (Order.Status.EXPIRED == order.getStatus()) {
			throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
		}

		if(Order.Status.ACCEPTED == order.getStatus()){
			if(Order.PayType.CASH == order.getPaymentType()){
				order.setStatus(orderRequest.getStatus());
			}
		}

		if (!StringUtils.isEmpty(orderRequest.getDate())) {
			order.setDate(orderRequest.getDate());
		}

		if (!StringUtils.isEmpty(orderRequest.getStatus())) {
			order.setStatus(orderRequest.getStatus());
		}

		if (!StringUtils.isEmpty(orderRequest.getIdCustomer())) {
			order.setIdCustomer(orderRequest.getIdCustomer());
		}

		if (!StringUtils.isEmpty(orderRequest.getIdLocation())) {
			order.setIdLocation(orderRequest.getIdLocation());
		}

		if (!StringUtils.isEmpty(orderRequest.getProfessionalCategory())) {

			Professional p = orderRequest.getProfessionalCategory().getProfessional();
			Category s = orderRequest.getProfessionalCategory().getCategory();

			ProfessionalCategory ps = new ProfessionalCategory(p, s);

			professionalCategoryRepository.save(ps);

			order.setProfessionalCategory(ps);

		}

		if (!StringUtils.isEmpty(orderRequest.getScheduleId())) {
			order.setScheduleId(orderRequest.getScheduleId());
		}

		//AQUI TRATAMOS O STATUS ACCEPTED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
		if (orderRequest.getStatus() == Order.Status.ACCEPTED) {
			this.sendPaymentRequest(orderRequest);
		}

		//TIVE QUE COMENTAR A VALIDACAO ABAIXO POIS ESTAVA DANDO O ERRO ABAIXO:
        //QUANDO VAMOS ATUALIZAR PARA SCHEDULED, AINDA NAO TEMOS OS DADOS QUE VAO SER ATUALIZADOS
		//-- VALIDANDO O COMENTARIO ACIMA --//
		//AQUI TRATAMOS O STATUS SCHEDULED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
		if (orderRequest.getStatus() == Order.Status.SCHEDULED) {
			this.validateScheduledAndsendPaymentRequest(orderRequest);
		}


		return orderRepository.save(order);
	}

	private void validateScheduledAndsendPaymentRequest(Order orderRequest) throws Exception {

	    Order order = orderRepository.findOne(orderRequest.getIdOrder());

		int daysToStart = Integer.parseInt(daysToStartPayment);

		//INSTANCIAMOS O CALENDARIO
		Calendar c = Calendar.getInstance();

		//DATA ATUAL
		//EX.: 20/08/2017
		Date now = c.getTime();

		//PEGAMOS A DATA DE INICIO DO AGENDAMENTO DO PEDIDO
		Date scheduleDateStart = order.getScheduleId().getScheduleStart();

		//ATRIBUIMOS A DATA DO AGENDAMENTO DO PEDIDO AO CALENDARIO
		c.setTime(scheduleDateStart);

		//VOLTAMOS N DIAS, DEFINIDO EM PROPRIEDADES, NO CALENDARIO BASEADO NA DATA DO AGENDAMENTO
		c.add(Calendar.DATE, -daysToStart);

		//DATA DO AGENDAMENTO MENOS N DIAS NO FORMADO DATE. OU SEJA, A DATA QUE DEVE INICIAR AS TENTAVIDAS DE PAGAMENTO
		Date dateToStartPayment = c.getTime();
        //TODO - DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
		//SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
		if (now.after(dateToStartPayment)) {
			sendPaymentRequest(orderRequest);
		}
	}

	private void sendPaymentRequest(Order orderRequest) throws ParseException, JsonProcessingException, Exception {

		Optional<RetornoTransacao> retornoTransacaoSuperpay = paymentController.sendRequest(orderRequest);

		if(retornoTransacaoSuperpay.isPresent()) {

			//VALIDAMOS SE VEIO O STATUS QUE ESPERAMOS
			//1 = Pago e Capturado | 2 = Pago e não Capturado (O CORRETO SERIA 2, POIS FAREMOS A CAPTURA POSTERIORMENTE)
			if(retornoTransacaoSuperpay.get().getStatusTransacao() == 2 ||
					retornoTransacaoSuperpay.get().getStatusTransacao() == 1 ||
                    //TIVE QUE ADICIONAR O STATUS 31 (Transação já Paga), ENQUANTO NAO FAZEMOS AS VALIDACOES DOS STATUS
					retornoTransacaoSuperpay.get().getStatusTransacao() == 31) {

				//SE FOR PAGO E CAPTURADO, HOUVE UM ERRO NAS DEFINICOES DA SUPERPAY, MAS FOI FEITO O PAGAMENTO
				if (retornoTransacaoSuperpay.get().getStatusTransacao() == 1) {
					log.warn("Pedido retornou como PAGO E CAPTURADO, mas o correto seria PAGO E 'NÃO' CAPTURADO.");
				}

                //SE TRANSACAO JA PAGA, ESTAMOS TENTANDO EFETUAR O PAGAMENTO DE UM PEDIDO JA PAGO ANTERIORMENTE
                if (retornoTransacaoSuperpay.get().getStatusTransacao() == 1) {
                    log.warn("Pedido retornou como TRANSACAO JA PAGA, possível tentativa de pagamento em duplicidade.");
                }

				//ENVIAMOS OS DADOS DO PAGAMENTO EFETUADO NA SUPERPAY PARA SALVAR O STATUS DO PAGAMENTO
				//OBS.: COMO ESSE METODO AINDA NAO FOI IMPLEMENTADO, ELE ESTA RETORNANDO BOOLEAN
				Boolean updateStatusPagamento = paymentService.updatePaymentStatus(retornoTransacaoSuperpay.get());

				if (updateStatusPagamento == false) {
					//TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO DER UM ERRO AO ATUALIZAR O STATUS DO PAGAMENTO
					log.error("Erro salvar o status do pagamento");
					throw new Exception("Erro salvar o status do pagamento");
				}

			//SE NAO VIER O STATUS DO PAGAMENTO 1 OU 2, VAMOS LANCAR UMA EXCECAO COM O STATUS VINDO DA SUPERPAY
			} else {
				//TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO O STATUS FOR OUTRO
				//TODO - SE FOR MESMO RETORNAR O CODIGO DO STATUS DO PAGAMENTO, PODERIA RETORNAR A MENSAGEM, NAO O CODIGO
				throw new Exception("Erro ao efetuar o pagamento: " + retornoTransacaoSuperpay.get().getStatusTransacao());
			}

		} else {
			//TODO - NAO SEI QUAL SERIA A MALHOR SOLUCAO QUANDO DER UM ERRO NO PAGAMENTO NA SUPERPAY
			log.error("Erro ao enviar a requisição de pagamento");
			throw new Exception("Erro ao enviar a requisição de pagamento");
		}


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
	 *
	 * @param order
	 *
	 * TODO: Ta escroto essas duas excecoes fazendo amesma coisa.. depois arrumo.. tem q ficar so a
	 * OrderValidationException mas tem q alterar  a classe la ainda
	 * @throws OrderValidationException
	 * @throws ValidationException
	 */
	public void validateCreate(Order order) throws OrderValidationException, ValidationException {//

		if (order.isScheduled()) {
			validateScheduleEndDate(order);
			
            // Aqui vc escolhe o que quer usar.
            //validateBusyScheduled2(order);
            validateBusyScheduled1(order);
		}
		else {
			Long idProfessionalCategory = order.getProfessionalCategory().getProfessionalCategoryId();

			ProfessionalCategory professionalCategory = professionalCategoryRepository.findOne(idProfessionalCategory);

			Professional professional = professionalCategory.getProfessional();

	        List<Order> orderList = orderRepository.findRunningOrdersByProfessional(
	                professional.getIdProfessional(), 0L);
	
			if (!orderList.isEmpty()) {
				// Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
				throw new OrderValidationException(OrderValidationException.Type.DUPLICATE_RUNNING_ORDER, "profissional ja esta com outra order em andamento.");
			}
		}
	}

	/**
	 *
	 * @param receivedOrder
	 *
	 * TODO: Ta escroto essas duas excecoes fazendo amesma coisa.. depois arrumo.. tem q ficar so a
	 * OrderValidationException mas tem q alterar  a classe la ainda
	 * @throws OrderValidationException
	 * @throws ValidationException
	 */
	public void validateUpdate(Order receivedOrder) throws OrderValidationException, ValidationException {//

		Long idOrder = receivedOrder.getIdOrder();

		MDC.put("idOrder", String.valueOf(idOrder));
		
		Order persistentOrder = orderRepository.findOne(idOrder);
		
		Professional professional;
		ProfessionalCategory professionalCategory = receivedOrder.getProfessionalCategory();
		
		if(professionalCategory == null)
		{
			professionalCategory = persistentOrder.getProfessionalCategory(); 
		}
        
		professional = professionalCategory.getProfessional();
		
		// So a Order gravada no banco eh que sabe dizer se a order eh agendada ou nao.
		if (persistentOrder.isScheduled()) {
			validateScheduleEndDate(receivedOrder);
            validateBusyScheduled1(receivedOrder);
		}


        List<Order> orderList = orderRepository.findRunningOrdersByProfessional(
                professional.getIdProfessional(), idOrder);

		if (!orderList.isEmpty()) {
			// Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
			throw new OrderValidationException(OrderValidationException.Type.DUPLICATE_RUNNING_ORDER, "profissional ja esta com outra order em andamento.");
		}

	}
	private void validateBusyScheduled1(Order order) throws ValidationException, OrderValidationException {

		Date newOrderScheduleStart = order.getScheduleId().getScheduleStart();
		if(newOrderScheduleStart == null){
			throw new OrderValidationException(OrderValidationException.Type.INVALID_SCHEDULE_START, "Data de inicio de agendamento vazia");
		}


		/*
		Nao coloco muitos filtros na query e retorno bastante orders e aplico a logica no if la em baixo.
		 */
		List<Order> orders = orderRepository.findScheduledOrdersByProfessional(order.getProfessionalCategory().getProfessional().getIdProfessional());

		for (int i = 0; i < orders.size(); i++) {
			Order o =  orders.get(i);

			if(o.getScheduleId() != null ) {
				Date existingOrderStart = o.getScheduleId().getScheduleStart();
				Date existingOrderEnd  = o.getScheduleId().getScheduleEnd();

				if (existingOrderStart.before(newOrderScheduleStart) &&
						existingOrderEnd.after(newOrderScheduleStart)) {
					// conflitou!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					throw new OrderValidationException(OrderValidationException.Type.CONFLICTING_SCHEDULES, "Ja existe agendamento marcado no horario de  " + newOrderScheduleStart.toString());
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
		if(!orders.isEmpty()){

			throw new ValidationException("Ja existe agendamento marcado no horario de  " + newOrderScheduleStart.toString());
		}else {
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

		if(receivedOrder.getScheduleId().getScheduleEnd() == null 
				&& Order.Status.SCHEDULED.equals(receivedOrder.getStatus())){
			throw new OrderValidationException(Type.INVALID_SCHEDULE_END, "Precisa de data final no agendamento");
		}

	}
}
