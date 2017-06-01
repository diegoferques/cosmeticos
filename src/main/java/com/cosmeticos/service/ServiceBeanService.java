package com.cosmeticos.service;

import com.cosmeticos.commons.ServiceRequestBody;
import com.cosmeticos.commons.UserRequestBody;
import com.cosmeticos.model.Role;
import com.cosmeticos.model.Service;
import com.cosmeticos.model.User;
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

    public Service create(ServiceRequestBody request){
        Service service = new Service();
        service.setCategory(request.toString());

        return repository.save(service);
    }

    public Service update(ServiceRequestBody request){
        Service service = repository.findOne(request.getIdService());
        service.setCategory(request.getCategory());

        return repository.save(service);
    }

    public Optional<Service> find(Long id){
        return Optional.of(repository.findOne(id));
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
