package com.cosmeticos.service;

import static com.cosmeticos.model.Order.Status.*;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.validation.OrderValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.cosmeticos.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.validation.OrderValidationException;

import lombok.extern.slf4j.Slf4j;

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
	private PenaltyService penaltyService;

	public Optional<Order> find(Long idOrder) {
		return Optional.of(orderRepository.findOne(idOrder));
	}

	public Order create(OrderRequestBody orderRequest) throws ValidationException {

		ProfessionalServices receivedProfessionalServices = orderRequest.getOrder().getProfessionalServices();

		/*
		 * Buscando o cliente que foi informado no request. Do que chega no request, so
		 * preciso confiar no ID dessas entidades. Os outros atributos as vezes podem
		 * nao vir preenchidos ou preenchidos de qualquer forma so pra nao ser barrado
		 * pelo @Valid, portanto devemos buscar o objeto real no banco.
		 */
		Customer customer = customerResponsitory.findOne(orderRequest.getOrder().getIdCustomer().getIdCustomer());

		CreditCard creditCard = (CreditCard) orderRequest.getOrder().getCreditCardCollection();

		Professional professional = professionalRepository
				.findOne(receivedProfessionalServices.getProfessional().getIdProfessional());

		// Conferindo se o ProfessionalServices recebido realmente esta associado ao
		// Profissional em nossa base.
		Optional<ProfessionalServices> persistentProfessionalServices = professional.getProfessionalServicesCollection()
				.stream().filter(ps -> ps.getCategory().getIdCategory()
						.equals(receivedProfessionalServices.getCategory().getIdCategory()))
				.findFirst();


			if (persistentProfessionalServices.isPresent()) {
				Order order = new Order();
				order.setScheduleId(orderRequest.getOrder().getScheduleId());
				order.setIdLocation(orderRequest.getOrder().getIdLocation());
				order.setIdCustomer(customer);
				order.setDate(Calendar.getInstance().getTime());
				order.setLastUpdate(order.getDate());
				order.getCreditCardCollection().add(creditCard);
				order.setExpireTime(new Date(order.getDate().getTime() +

						// 6 horas de validade
						21600000));

				// ProfessionalServices por ser uma tabela associativa necessita de um cuidado
				// estra
				order.setProfessionalServices(persistentProfessionalServices.get());

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
						"Service [id=" + receivedProfessionalServices.getCategory().getIdCategory()
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
				if (o.getProfessionalServices().getProfessional().getIdProfessional() == professional
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

	public Order update(OrderRequestBody request) {
		Order orderRequest = request.getOrder();
		Order order = orderRepository.findOne(orderRequest.getIdOrder());

		if (Order.Status.CLOSED == order.getStatus()) {
			throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
		}

		if (Order.Status.EXPIRED == order.getStatus()) {
			throw new IllegalStateException("PROIBIDO ATUALIZAR STATUS.");
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

		if (!StringUtils.isEmpty(orderRequest.getProfessionalServices())) {

			Professional p = orderRequest.getProfessionalServices().getProfessional();
			Category s = orderRequest.getProfessionalServices().getCategory();

			ProfessionalServices ps = new ProfessionalServices(p, s);

			order.setProfessionalServices(ps);

		}

		if (!StringUtils.isEmpty(orderRequest.getScheduleId())) {
			order.setScheduleId(orderRequest.getScheduleId());
		}

		return orderRepository.save(order);
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

        Order persistentOrder = null;
        Professional professional;

        // SE FOR POST/CREATE, O ID ORDER AINDA NAO EXISTE, MAS TEMOS O PROFISSIONAL
        // PARA VERIFICAR SE JA TEM ORDERS
        if (order.getIdOrder() == null) {

            professional = order.getProfessionalServices().getProfessional();

            // SE FOR PUT/UPDATE, O ID ORDER EXISTE, MAS PODEMOS NAO TER O PROFISSIONAL, BEM
            // COMO O UPDATE DE STATUS
        } else {
            order = orderRepository.findOne(order.getIdOrder());
            professional = order.getProfessionalServices().getProfessional();
        }

        if(order.getScheduleId() != null) {
            // Aqui vc escolhe o que quer usar.
            validateScheduled1(order);
            //validateScheduled2(order);
        }

        // List<Order> orderList =
        // orderRepository.findByStatusOrStatusAndProfessionalServices_Professional_idProfessional(
        // professional.getIdProfessional(), Order.Status.INPROGRESS,
        // Order.Status.ACCEPTED);
        List<Order> orderList = orderRepository.findByProfessionalServices_Professional_idProfessionalAndStatusOrStatus(
                professional.getIdProfessional());

		if (!orderList.isEmpty()) {
			throw new OrderValidationException();
		}

	}

	private void validateScheduled1(Order order) throws ValidationException {

		Date newOrderScheduleStart = order.getScheduleId().getScheduleStart();
		newOrderScheduleStart.getTime();


		/*
		Nao coloco muitos filtros na query e retorno bastante orders e aplico a logica no if la em baixo.
		 */
		List<Order> orders = orderRepository.findScheduledOrdersByProfessional(order.getProfessionalServices().getProfessional().getIdProfessional());
		orderRepository.save(orders);
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

		ProfessionalServices ps = order.getProfessionalServices();
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
}
