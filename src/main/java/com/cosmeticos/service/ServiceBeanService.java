package com.cosmeticos.service;

import com.cosmeticos.commons.ServiceRequestBody;
import com.cosmeticos.model.Service;
import com.cosmeticos.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 31/05/2017.
 */
@org.springframework.stereotype.Service
public class ServiceBeanService {


   @Autowired
   private ServiceRepository repository;

    public Service create(ServiceRequestBody request) {
        Service s = new Service();
        s.setCategory(request.getEntity().getCategory());

        return repository.save(s);
    }

    public Optional<Service> update(ServiceRequestBody request) {
        Service serviceFromRequest = request.getEntity();

        // TODO ver possibilidade de usar VO pq para update, o ID deve ser obrigatorio.
        Long requestedIdService = serviceFromRequest.getIdService();

        Optional<Service> optional = Optional.ofNullable(repository.findOne(requestedIdService));

        if (optional.isPresent()) {
            Service persistentService = optional.get();

            persistentService.setCategory(serviceFromRequest.getCategory());
            persistentService.setProfessionalServicesCollection(serviceFromRequest.getProfessionalServicesCollection());

            repository.save(persistentService);
        }

        return optional;
    }

    public Optional<Service> find(Long id){
        return Optional.ofNullable(repository.findOne(id));
    }

    public void deletar(){
        throw new UnsupportedOperationException("Excluir de acordo com o Status. ");
    }

    public List<Service> findAll() {

        Iterable<Service> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }
}
