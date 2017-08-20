package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.controller.CustomerControllerTests;
import com.cosmeticos.controller.ProfessionalControllerTests;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vinicius on 15/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceTest {

    @Value("${order.payment.secheduled.startDay}")
    private String daysToStartPayment;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalServicesRepository professionalServicesRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private OrderService orderService;

    Order o3;
    Order o4;
    Order o5;

    @Before
    public void setUp() throws Exception {

        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testUpdateStatus-cliente");
        c1.getUser().setEmail("testUpdateStatus-cliente@bol");

        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername("testUpdateStatus-professional");
        professional.getUser().setEmail("testUpdateStatus-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category serviceProgramador = new Category();
        serviceProgramador.setName("PROGRAMADOR");
        serviceRepository.save(serviceProgramador);

        ProfessionalServices ps1 = new ProfessionalServices(professional, serviceProgramador);
        professionalServicesRepository.save(ps1);
        
        professional.getProfessionalServicesCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);


        o3 = new Order();
        o3.setStatus(Order.Status.OPEN);
        o3.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setLastUpdate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 30, 0)));
        o3.setIdCustomer(c1);
        //o3.setIdLocation();
        o3.setProfessionalServices(ps1);
        o3.setPaymentType(Order.PayType.CASH);
        //o3.setScheduleId(s1);
        orderRepository.save(o3);

        LocalDateTime ldt1 = LocalDateTime.now();
        ldt1.minusDays(3);
        o4 = new Order();
        o4.setDate(Timestamp.valueOf(ldt1));
        o4.setLastUpdate(Timestamp.valueOf(ldt1.minusDays(3)));
        o4.setIdCustomer(c1);
        //o4.setIdLocation();
        o4.setProfessionalServices(ps1);
        //o4.setScheduleId(s1);
        o4.setStatus(Order.Status.SEMI_CLOSED);
        o4.setPaymentType(Order.PayType.CASH);

        orderRepository.save(o4);

        LocalDateTime ldt2 = LocalDateTime.now();
        ldt2.minusDays(8);
        o5 = new Order();
        o5.setDate(Timestamp.valueOf(ldt2));
        o5.setIdCustomer(c1);
        o5.setLastUpdate(Timestamp.valueOf(ldt2.minusDays(8)));
        //o5.setIdLocation();
        o5.setProfessionalServices(ps1);
        //o5.setScheduleId(s2);
        o5.setStatus(Order.Status.SEMI_CLOSED);
        o5.setPaymentType(Order.PayType.CASH);
        orderRepository.save(o5);
                    }


    @Test
    public void testUpdateStatus(){


        orderService.updateStatus();
            o3 = orderRepository.findOne(o3.getIdOrder());
            o4 = orderRepository.findOne(o4.getIdOrder());
            o5 = orderRepository.findOne(o5.getIdOrder());

            Assert.assertEquals(Order.Status.SEMI_CLOSED, o4.getStatus());

            Assert.assertEquals(Order.Status.AUTO_CLOSED, o5.getStatus());
            Assert.assertEquals(Order.Status.OPEN, o3.getStatus());
    }

    //TESTE SOMENTE PARA VERIFICAR O TRATAMENTO DAS DATAS
    @Ignore
    @Test
    public void testCompareDates() {

        int daysToStart = ((int) Integer.parseInt(daysToStartPayment));
        System.out.println("VALUE: " + daysToStart);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //INSTANCIAMOS O CALENDARIO
        Calendar c = Calendar.getInstance();

        //DATA ATUAL
        Date now = new Date();
        System.out.println("DATA ATUAL: " + dateFormat.format(now));

        //ATRIBUIMOS A DATA ATUAL AO CALENDARIO
        c.setTime(now);

        Date dataIgual = c.getTime();
        System.out.println("DATA IGUAL: " + dateFormat.format(now));

        //VOLTAMOS 2 DIAS NO CALENDARIO BASEADO NA DATA ATUAL
        c.add(Calendar.DATE, -daysToStart);

        //DATA ATUAL MENOS 2 DIAS NO FORMADO DATE. OU SEJA, A DATA QUE DEVE INICIAR AS TENTAVIDAS DE PAGAMENTO
        Date dateToStartPayment = c.getTime();
        System.out.println("DATA ATUAL MENOS 2 DIAS: " + dateFormat.format(dateToStartPayment));

        //AVANCAMOS 4 DIAS NO CALENDARIO BASEADO NA DATA ATUAL
        c.add(Calendar.DATE, 4);

        //DATA ATUAL MENOS 2 DIAS NO FORMADO DATE. OU SEJA, A DATA QUE DEVE INICIAR AS TENTAVIDAS DE PAGAMENTO
        Date dateMaisDias = c.getTime();
        System.out.println("DATA ATUAL MAIS 2 DIAS: " + dateFormat.format(dateMaisDias));

        //SE A DATA ATUAL FOR POSTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO
        if (now.after(dateToStartPayment)) {
            System.out.println("DATA ATUAL É POSTERIOR A DATA PARA INICIAR O PAGAMENTO");
        } else {
            System.out.println("DATA ATUAL !NÃO! É POSTERIOR A DATA PARA INICIAR O PAGAMENTO");
        }

        if (now.after(dataIgual)) {
            System.out.println("DATA ATUAL É POSTERIOR A DATA IGUAL");
        } else {
            System.out.println("DATA ATUAL !NÃO! É POSTERIOR A DATA IGUAL");
        }

        //SE A DATA ATUAL FOR ANTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO
        if (now.before(dateToStartPayment)) {
            System.out.println("DATA ATUAL É ANTERIOR A DATA PARA INICIAR O PAGAMENTO");
        } else {
            System.out.println("DATA ATUAL !NÃO! É ANTERIOR A DATA PARA INICIAR O PAGAMENTO");
        }

        //SE A DATA ATUAL FOR ANTERIOR A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO
        if (now.before(dateMaisDias)) {
            System.out.println("DATA ATUAL É ANTERIOR A DATA MAIS DIAS");
        } else {
            System.out.println("DATA ATUAL !NÃO! É ANTERIOR A DATA PARA MAIS DIAS");
        }

        //SE A DATA ATUAL FOR IGUAL A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO
        if (now.equals(dataIgual)) {
            System.out.println("DATA ATUAL É IGUAL A DATA INFORMADA");
        } else {
            System.out.println("DATA ATUAL !NÃO! É IGUAL A DATA INFORMADA");
        }

        //SE A DATA ATUAL FOR IGUAL A DATA QUE DEVE INICIAR AS TENTATIVAS DE RESERVA DO PAGAMENTO
        if (now.equals(dateMaisDias)) {
            System.out.println("DATA ATUAL É IGUAL A DATA MAIS DIAS");
        } else {
            System.out.println("DATA ATUAL !NÃO! É IGUAL A DATA MAIS DIAS");
        }
    }
}
