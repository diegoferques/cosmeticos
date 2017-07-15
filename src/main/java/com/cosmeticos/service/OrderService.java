package com.cosmeticos.service;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ServiceRepository;
import org.hibernate.Criteria;
import org.hibernate.SharedSessionContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 17/06/2017.
 */
@org.springframework.stereotype.Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerResponsitory;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Optional<Order> find(Long idOrder) {
        return Optional.of(orderRepository.findOne(idOrder));
    }

    public Order create(OrderRequestBody orderRequest) throws ValidationException {

        ProfessionalServices receivedProfessionalServices = orderRequest.getOrder().getProfessionalServices();

        /*
         Buscando o cliente que foi informado no request.
         Do que chega no request, so preciso confiar no ID dessas entidades. Os outros atributos as vezes podem
         nao vir preenchidos ou preenchidos de qualquer forma so pra nao ser barrado pelo @Valid, portanto
         devemos buscar o objeto real no banco.
          */
        Customer customer = customerResponsitory.findOne(
                orderRequest.getOrder().getIdCustomer().getIdCustomer()
        );

        Professional professional = professionalRepository.findOne(
                receivedProfessionalServices.getProfessional().getIdProfessional()
        );

        // Conferindo se o ProfessionalServices recebido realmente esta associado ao Profissional em nossa base.
        Optional<ProfessionalServices> persistentProfessionalServices =
                professional.getProfessionalServicesCollection()
                        .stream()
                        .filter(ps -> ps.getService().getIdService().equals(receivedProfessionalServices.getService().getIdService()))
                        .findFirst();

        if (persistentProfessionalServices.isPresent()) {
            Order order = new Order();
            order.setScheduleId(orderRequest.getOrder().getScheduleId());
            order.setIdLocation(orderRequest.getOrder().getIdLocation());
            order.setIdCustomer(customer);
            order.setDate(Calendar.getInstance().getTime());

            // ProfessionalServices por ser uma tabela associativa necessita de um cuidado estra
            order.setProfessionalServices(persistentProfessionalServices.get());

            //O ID ORDER SERA DEFINIDO AUTOMATICAMENTE
            //order.setIdOrder(orderRequest.getOrder().getIdOrder());

            //Schedule schedule = new Schedule();
            //orderRequest.getOrder().getScheduleId()
            //sale.setScheduleId();

            //O STATUS INICIAL SERA DEFINIDO COMO CRIADO
            order.setStatus(Order.Status.CREATED.ordinal());

            Order newOrder = orderRepository.save(order);

            // Pra ter dado erro la em baixo significa que essa parte aki funcionu ne? isso
            List<Order> savedOrders = orderRepository.findByIdCustomer_idCustomer(customer.getIdCustomer());

            int totalOrders = 0;

            for (int i = 0; i < savedOrders.size(); i++) {
                Order o = savedOrders.get(i);
                if (o.getProfessionalServices().getProfessional().getIdProfessional() == professional.getIdProfessional()) {
                    totalOrders++;
                }
            }

            if (totalOrders >= 2)// tirei os breaks daki pq ja sei q aki ta inserindo o wallet com id=2 certinho. pode debugar.
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
            throw new OrderService.ValidationException("Service [id=" + receivedProfessionalServices.getService().getIdService() + "] informado no requst nao esta associado ao profissional " +
                    "id=[" + professional.getIdProfessional() + "] em nosso banco de dados.");
        }
    }

    public Order update(OrderRequestBody request) {
        Order orderRequest = request.getOrder();
        Order order = orderRepository.findOne(orderRequest.getIdOrder());

        if(Order.Status.FINISHED_BY_CUSTOMER.ordinal() == order.getStatus()){
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

    public List<Order> find10Lastest() {
        return orderRepository.findTop10ByOrderByDateDesc();
        //return orderRepository.findAll();
    }


    public class ValidationException extends Exception {
        public ValidationException(String s) {
            super(s);
        }
    }

    @Scheduled(cron = "${order.unfinished.cron}")
    public void updateStatus() {

        List<Order> onlyOrsersFinishedByProfessionals = orderRepository.findByStatus(Order.Status.FINISHED_BY_PROFESSIONAL.ordinal());

        int count = onlyOrsersFinishedByProfessionals.size();

        for (Order o : onlyOrsersFinishedByProfessionals) {

            o.setStatus(Order.Status.FINISHED_BY_CUSTOMER_AUTO.ordinal());

            orderRepository.save(o);
        }
        log.info("{} orders foram atualizada para {}.", count, Order.Status.FINISHED_BY_CUSTOMER_AUTO.toString());
    }

}
