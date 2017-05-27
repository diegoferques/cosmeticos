package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.Customer;
import com.cosmeticos.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created by matto on 27/05/2017.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Customer find(Long idCustomer) {
        return repository.findOne(idCustomer);
    }

    public Customer create(CustomerRequestBody request)
    {
        //LocalDate birthDate = LocalDate.of(request.getBirthYear(), request.getBirthMonth(), request.getBirthDay());

        Customer c = new Customer();

        //c.setBirthDate(LocalDate.of(1980, 01, 20).toString());
        //TODO - Necessário formatar o birthDate do Customer para inserir no banco
        c.setBirthDate(request.getBirthDate());
        c.setCellPhone(request.getCellPhone());
        c.setCpf(request.getCpf());
        //c.setDateRegister();
        c.setGenre(request.getGenre());
        //c.setIdAddress(null);
        //c.setIdCustomer(Long.valueOf(1));
        //c.setIdLogin(null);
        c.setNameCustomer(request.getNameCustomer());
        //c.setServiceRequestCollection(null);
        //TODO - Onde e quando vamos setar o status do Customer?
        c.setStatus((short) 1);

        //TODO - Temos que informar o ID do cliente antes de inserir no banco? Não seria gerado automaticamente no banco?
        //c.setIdCustomer(Long.valueOf(1));

        //TODO - Não poderia ser o TimeStamp do banco?
        c.setDateRegister(LocalDateTime.now().toString());

        //TODO - Não consigo passar os parâmetros dos métodos abaixo
        //c.setIdAddress();
        //c.setIdLogin();

        return repository.save(c);
    }
}
