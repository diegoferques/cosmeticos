package com.cosmeticos.service;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.controller.PaymentController;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.cosmeticos.model.Order.Status.*;
import static com.cosmeticos.validation.OrderValidationException.Type.*;

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

	public Order create(OrderRequestBody orderRequest) throws ValidationException {

		ProfessionalCategory receivedProfessionalCategory = orderRequest.getOrder().getProfessionalCategory();

		/*
		 * Buscando o cliente que foi informado no request. Do que chega no request, so
		 * preciso confiar no ID dessas entidades. Os outros atributos as vezes podem
		 * nao vir preenchidos ou preenchidos de qualquer forma so pra nao ser barrado
		 * pelo @Valid, portanto devemos buscar o objeto real no banco.
		 */
		Customer customer = customerResponsitory.findOne(orderRequest.getOrder().getIdCustomer().getIdCustomer());

		// Checaremos Order.PAymentTypeCreditCard creditCard = orderRequest.getOrder().getCreditCardCollection().iterator().next();

		Professional professional = professionalRepository
				.findOne(receivedProfessionalCategory.getProfessional().getIdProfessional());

		// Conferindo se o ProfessionalServices recebido realmente esta associado ao
		// Profissional em nossa base.
		Optional<ProfessionalCategory> persistentProfessionalServices = professional.getProfessionalCategoryCollection()
				.stream().filter(ps -> ps.getCategory().getIdCategory()
						.equals(receivedProfessionalCategory.getCategory().getIdCategory()))
				.findFirst();


			if (persistentProfessionalServices.isPresent()) {
				Order order = new Order();
				order.setScheduleId(orderRequest.getOrder().getScheduleId());
				order.setIdLocation(orderRequest.getOrder().getIdLocation());
				order.setIdCustomer(customer);
				order.setDate(Calendar.getInstance().getTime());
				order.setLastUpdate(order.getDate());
				order.setPaymentType(orderRequest.getOrder().getPaymentType());
//				order.getCreditCardCollection().add(creditCard);
				order.setExpireTime(new Date(order.getDate().getTime() +

						// 6 horas de validade
						21600000));

				// ProfessionalServices por ser uma tabela associativa necessita de um cuidado
				// estra
				order.setProfessionalCategory(persistentProfessionalServices.get());
				order.getProfessionalCategory().setPriceRule(receivedProfessionalCategory.getPriceRule());

				//possibilidade de colocar um if a partir daki.
				/*
				MDC.put("price", receivedProfessionalCategory.getPriceRule()
						.stream()
						.findFirst()
						.get()
						.getPrice().toString());
				*/

				// O ID ORDER SERA DEFINIDO AUTOMATICAMENTE
				// order.setIdOrder(orderRequest.getOrder().getIdOrder());

				// Schedule schedule = new Schedule();
				// orderRequest.getOrder().getScheduleId()
				// sale.setScheduleId();

				// O STATUS INICIAL SERA DEFINIDO COMO CRIADO
				order.setStatus(Order.Status.OPEN);

				Order newOrder = orderRepository.save(order);
				// Buscando se o customer que chegou no request esta na wallet

				addInWallet(professional, customer);

				return newOrder;
			} else {
				throw new OrderService.ValidationException(
						"Service [id=" + receivedProfessionalCategory.getCategory().getIdCategory()
								+ "] informado no requst nao esta associado ao profissional " + "id=["
								+ professional.getIdProfessional() + "] em nosso banco de dados.");
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
<<<<<<< HEAD
			throw new OrderValidationException(HttpStatus.CONFLICT, "PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
=======
			throw new OrderValidationException(INVALID_ORDER_STATUS, "PROIBIDO ATUALIZAR PARA O MESMO STATUS.");
>>>>>>> dev
		}

		if (Order.Status.CLOSED == order.getStatus()) {
			throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
		}

		if (Order.Status.EXPIRED == order.getStatus()) {
			throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
		}

		//TODO - VERIFICAR POIS SER FOR ENVIADO CLOSED PODE BATER AQUI E GERAR PROBLEMA
		if(Order.Status.READY2CHARGE == order.getStatus()){
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

		//AQUI TRATAMOS O STATUS SCHEDULED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
		if (orderRequest.getStatus() == Order.Status.SCHEDULED) {
			this.validateScheduledAndsendPaymentRequest(orderRequest);
		}

		//AQUI TRATAMOS O STATUS ACCEPTED QUE VAMOS NA SUPERPAY EFETUAR A RESERVA DO VALOR PARA PAGAMENTO
		if (orderRequest.getStatus() == Order.Status.READY2CHARGE) {
			if(orderRequest.getPaymentType() == Order.PayType.CREDITCARD) {
				this.sendPaymentCapture(order);
			}
		}

		//TODO - CRIAR METODO DE VALIDAR PAYMENT RESPONSE LANCANDO ORDER VALIDATION EXCEPTION COM...
		//HTTPSTATUS DEFINIDO PARA CADA STATUS DE PAGAMENTO DA SUPERPAY
		//CARD: https://trello.com/c/fyPMjNJI/113-adequar-status-do-pagamento-do-superpay-aos-nossos-status-da-order
		//BRANCH: RNF101

		return orderRepository.save(order);
	}

	//CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
	//BRANCH: RNF101
	//TODO - ACHO QUE PRECISA DE MAIS VALIDACOES, BEM COMO QUANDO DER ERRO DE CONSULTA OU CAPTURA POR 404, 500 E ETC.
	private void sendPaymentCapture(Order order) throws JsonProcessingException, URISyntaxException, OrderValidationException {

		//ACHEI MELHOR COMENTAR ABAIXO E UTILIZAR O PROXIMO
		/*
		Optional<RetornoTransacao> retornoConsultaTransacao = paymentService.consultaTransacao(idOrder);

		if(retornoConsultaTransacao.isPresent()) {

			//SE O STATUS ESTIVER NA SUPERPAY COMO PAGO E NAO CAPTURADO, ENTAO ENVIAMOS O PEDIDO DE CAPTURA
			if (retornoConsultaTransacao.get().getStatusTransacao() == 2) {
				ResponseEntity<RetornoTransacao> exchange = paymentService.capturaTransacaoSuperpay(idOrder);
			}
		}
		*/

		//ACHEI MELHOR COLOCAR ESSA RESPONSABILIDADE DENTRO DE PAYMENTSERVICE, (OPS, CONTROLLER)
		Boolean paymentCapture = paymentController.validatePaymentStatusAndSendCapture(order);

		//DEIXEI RESERVADO ABAIXO PARA FAZER ALGUMA COISA, CASO NAO TENHA SUCESSO NA CAPTURA QUANDO FOR READY2CHARGE
		//POIS O CRON PEGA TODOS OS READY2CHARGE E ENVIA PARA ESTE METODO, SE DER ALGUM ERRO, TEMOS QUE FAZER ALGO
		//CASO CONTRARIO, VAI FICAR TENTANDO CAPTURAR E NUNCA VAI CONSEGUIR ATE CADUCAR!
		/*
		if(!paymentCapture) {

		}
		*/

	}

	private void validateScheduledAndsendPaymentRequest(Order orderRequest) throws Exception {

	    Order order = orderRepository.findOne(orderRequest.getIdOrder());

		int daysToStart = Integer.parseInt(daysToStartPayment);
		int daysBeforeStart = Integer.parseInt(daysBeforeStartToNotification);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		//INSTANCIAMOS O CALENDARIO
		Calendar c = Calendar.getInstance();

		//DATA ATUAL
		//EX.: 20/08/2017
		Date now = c.getTime();

		//PEGAMOS A DATA DE INICIO DO AGENDAMENTO DO PEDIDO
		Date scheduleDateStart = order.getScheduleId().getScheduleStart();


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
			if(!sendPaymentRequest(orderRequest)) {
				//AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
				log.error("Erro ao efetuar a reserva do pagamento, sugerimos que troque o pagamento para dinheiro");
			}

		//TODO - VERIFICAR SE E O MESMO DIA, SE DER ERRO, NOTIFICAR PARA MUDAR PARA DINHEIRO
		} else if(sdf.format(now).equals(sdf.format(scheduleDateStart))) {
			//TODO - URGENTE: VERIFICAR MELHORIA, POIS O ERRO AQUI PODE SER DE REDE E ETC, NAO SO DE LIMITE DO CARTAO
			if(!sendPaymentRequest(orderRequest)) {
				//AQUI O GARRY DISSE QUE TROCARIAMOS, POSTERIORMENTE, PARA ALGO QUE IRA GERAR O POPUP NA TELA DO CLIENTE
				log.error("Erro ao efetuar a reserva do pagamento, seu agendamento não poderá prosseguir até que a" +
						" forma de pagamento seja alterado para dinheiro");
			}

		//TODO - URGENTE: DEVERIAMOS COBRAR SOMENTE SE FOR ATE A DATA DE AGENDAMENTO? POIS CORREMOS O RISCO DE COBRAR ALGO BEM ANTIGO
		//SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO, ENVIAMOS PARA PAGAMENTO
		} else if (now.after(dateToStartPayment)) {
			//AQUI NAO FAZEMOS NENHUMA VERIFICACAO, POIS SE DER ERRO, AINDA TEREMOS OUTROS DIAS PARA TENTAR NOVAMENTE.
			sendPaymentRequest(orderRequest);

		//TODO - VAMOS FAZER ALGO CASO NAO ESTEJA EM NEMHUMA DAS CONDICOES ACIMA?
		} else {
			log.error("Fora do período defenido para iniciar a reserva do valor para pagamento. ORDER ID: " + orderRequest.getIdOrder());
		}

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
			if (order.getPayment() == null) {
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
			if(Payment.Status.PAGO_E_NAO_CAPTURADO == order.getPayment().getStatus()) {

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
	 *
	 * @param order
	 *
	 * TODO: Ta escroto essas duas excecoes fazendo amesma coisa.. depois arrumo.. tem q ficar so a
	 * OrderValidationException mas tem q alterar  a classe la ainda
	 * @throws OrderValidationException
	 * @throws ValidationException
	 */
	public void validate(Order order) throws OrderValidationException, ValidationException {//

		Long idOrder = 0L;
        Professional professional;

		if(order.getScheduleId() != null)
			validateScheduledBadRequest(order);

        // SE FOR POST/CREATE, O ID ORDER AINDA NAO EXISTE, MAS TEMOS O PROFISSIONAL
        // PARA VERIFICAR SE JA TEM ORDERS
        if (order.getIdOrder() == null) {

            professional = order.getProfessionalCategory().getProfessional();

            // SE FOR PUT/UPDATE, O ID ORDER EXISTE, MAS PODEMOS NAO TER O PROFISSIONAL, BEM
            // COMO O UPDATE DE STATUS
        } else {
            order = orderRepository.findOne(order.getIdOrder());
            professional = order.getProfessionalCategory().getProfessional();
            idOrder = order.getIdOrder();
			MDC.put("idOrder", idOrder.toString());
        }

		if(order.getScheduleId() != null) {
			// Aqui vc escolhe o que quer usar.
			validateScheduled1(order);
			//validateScheduled2(order);
		}


		//List<Order> orderListId =
		//orderRepository.findByStatusOrStatusAndProfessionalServices_Professional_idProfessional(
		//Order.Status.ACCEPTED,
		//Order.Status.INPROGRESS, professional.getIdProfessional());

        List<Order> orderList = orderRepository.findByProfessionalCategory_Professional_idProfessionalAndStatusOrStatus(
                professional.getIdProfessional(), idOrder);

		if (!orderList.isEmpty()) {
			// Lanca excecao quando detectamos que o profissional ja esta com outra order em andamento.
			throw new OrderValidationException(OrderValidationException.Type.DUPLICATE_RUNNING_ORDER, "profissional ja esta com outra order em andamento.");
		}

	}

	private void validateScheduled1(Order order) throws ValidationException, OrderValidationException {

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
					throw new ValidationException("Ja existe agendamento marcado no horario de  " + newOrderScheduleStart.toString());
				}
			}
		}
	}
	private void validateScheduled2(Order order) throws ValidationException {

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

	public void validateScheduledBadRequest(Order orderRequest) throws OrderValidationException {

		if(orderRequest.getScheduleId().getScheduleEnd() == null && orderRequest.getStatus() == Order.Status.SCHEDULED){
			throw new OrderValidationException(OrderValidationException.Type.INVALID_SCHEDULE_END, "Precisa de data final no agendamento");
		}

	}
}
