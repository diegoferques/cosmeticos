package com.cosmeticos.service;

import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Created by Lulu on 23/05/2017.
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository repository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private ServiceBeanService serviceBeanService;

    /**
     * @deprecated Criacao de agendamentos deve ser feita pelo endpoint de {@link Order}.
     * @param request
     * @return
     */
    public Schedule create(ScheduleRequestBody request)
    {

        // TODO: talvez esta insercao seja responsabilidade de SaleService.
        Optional<Customer> customerOptional = customerService.find(request.getIdCustomer());
        Optional<Professional> professionalOptional = professionalService.find(request.getIdProfessional());
        Optional<Service> serviceOptional = serviceBeanService.find(request.getIdService());

        ProfessionalServices professionalServices = new ProfessionalServices(
                request.getIdProfessional(),
                request.getIdService()
        );
        professionalServices.setProfessional(professionalOptional.get());
        professionalServices.setService(serviceOptional.get());

        Schedule s = new Schedule();
        s.setScheduleDate(request.getScheduleDate());
        s.setStatus(Schedule.Status.ACTIVE);

        Order newOrder = new Order(customerOptional.get(), professionalServices, s);
        newOrder.setStatus(Order.Status.SCHEDULED); // Scheduled

        s.getOrderCollection().add(newOrder);

        return repository.save(s);
    }

    public Schedule update(ScheduleRequestBody request)
    {
        Schedule schedule = repository.findOne(request.getIdSchedule());
        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setStatus(Schedule.Status.valueOf(request.getStatus()));
        return repository.save(schedule);
    }

    public Optional<Schedule> find(Long id)
    {
        return Optional.of(repository.findOne(id));
    }

    public void delete()
    {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Schedule> find10Lastest() {
        return repository.findTop10ByOrderByScheduleDateDesc();
    }
}
