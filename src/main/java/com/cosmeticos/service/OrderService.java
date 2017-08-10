package com.cosmeticos.service;

import static com.cosmeticos.model.Order.Status.AUTO_CLOSED;
import static com.cosmeticos.model.Order.Status.CANCELLED;
import static com.cosmeticos.model.Order.Status.CLOSED;
import static com.cosmeticos.model.Order.Status.EXPIRED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.model.Wallet;
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

		Professional professional = professionalRepository
				.findOne(receivedProfessionalServices.getProfessional().getIdProfessional());

		// Conferindo se o ProfessionalServices recebido realmente esta associado ao
		// Profissional em nossa base.
		Optional<ProfessionalServices> persistentProfessionalServices = professional.getProfessionalServicesCollection()
				.stream().filter(ps -> ps.getService().getIdService()
						.equals(receivedProfessionalServices.getService().getIdService()))
				.findFirst();

		if (persistentProfessionalServices.isPresent()) {
			Order order = new Order();
			order.setScheduleId(orderRequest.getOrder().getScheduleId());
			order.setIdLocation(orderRequest.getOrder().getIdLocation());
			order.setIdCustomer(customer);
			order.setDate(Calendar.getInstance().getTime());
			order.setLastUpdate(order.getDate());
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

			// Pra ter dado erro la em baixo significa que essa parte aki funcionu ne? isso
			List<Order> savedOrders = orderRepository.findByIdCustomer_idCustomer(customer.getIdCustomer());

			int totalOrders = 0;

			for (int i = 0; i < savedOrders.size(); i++) {
				Order o = savedOrders.get(i);
				if (o.getProfessionalServices().getProfessional().getIdProfessional() == professional
						.getIdProfessional()) {
					totalOrders++;
				}
			}

			if (totalOrders >= 2)// tirei os breaks daki pq ja sei q aki ta inserindo o wallet com id=2 certinho.
									// pode debugar.
			{
				if (professional.getWallet() == null) {
					professional.setWallet(new Wallet());
					professional.getWallet().setProfessional(professional);
				}
				professional.getWallet().getCustomers().add(customer);
				professionalRepository.save(professional);
			}

			return newOrder;
		} else {
			throw new OrderService.ValidationException(
					"Service [id=" + receivedProfessionalServices.getService().getIdService()
							+ "] informado no requst nao esta associado ao profissional " + "id=["
							+ professional.getIdProfessional() + "] em nosso banco de dados.");
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
			order.setProfessionalServices(orderRequest.getProfessionalServices());
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

	public void validate(Order order) throws OrderValidationException {

		Professional professional;

		// SE FOR POST/CREATE, O ID ORDER AINDA NAO EXISTE, MAS TEMOS O PROFISSIONAL
		// PARA VERIFICAR SE JA TEM ORDERS
		if (order.getIdOrder() == null) {
			professional = order.getProfessionalServices().getProfessional();

			// SE FOR PUT/UPDATE, O ID ORDER EXISTE, MAS PODEMOS NAO TER O PROFISSIONAL, BEM
			// COMO O UPDATE DE STATUS
		} else {
			Order requestedOrder = orderRepository.findOne(order.getIdOrder());
			professional = requestedOrder.getProfessionalServices().getProfessional();
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
