package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 27/05/2017.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Optional<Customer> find(Long idCustomer) {
        return Optional.of(repository.findOne(idCustomer));
    }

    public Customer create(CustomerRequestBody request) {
        //LocalDate birthDate = LocalDate.of(request.getBirthYear(), request.getBirthMonth(), request.getBirthDay());

        Customer c = new Customer();

        c.setBirthDate(request.getCustomer().getBirthDate());
        c.setCellPhone(request.getCustomer().getCellPhone());
        c.setCpf(request.getCustomer().getCpf());
        //c.setDateRegister();
        c.setGenre(request.getCustomer().getGenre());
        //c.setIdAddress(null);
        //c.setIdCustomer(Long.valueOf(1));
        //c.setIdLogin(null);
        c.setNameCustomer(request.getCustomer().getNameCustomer());
        //c.setServiceRequestCollection(null);
        c.setStatus(Customer.Status.ACTIVE.ordinal());

        //TODO - Não poderia ser o TimeStamp do banco?
        c.setDateRegister(Calendar.getInstance().getTime());

        //TODO - Não consigo passar os parâmetros dos métodos abaixo
        c.setIdAddress(createFakeAddress(c));
        c.setIdLogin(createFakeLogin(c));

        return repository.save(c);
    }

    public Customer update(CustomerRequestBody request)
    {
        Customer customer = repository.findOne(request.getCustomer().getIdCustomer());
        customer.setNameCustomer(request.getCustomer().getNameCustomer());
        customer.setCellPhone(request.getCustomer().getCellPhone());
        customer.setCpf(request.getCustomer().getCpf());
        customer.setGenre(request.getCustomer().getGenre());
        customer.setBirthDate(request.getCustomer().getBirthDate());
        //customer.setStatus(Customer.Status.valueOf(request.getCustomer().getStatus()));
        return repository.save(customer);
    }

    public void delete()
    {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Customer> find10Lastest() {
        return repository.findTop10ByOrderByDateRegisterDesc();
    }

    private User createFakeLogin(Customer c) {
        User u = new User();
        u.setEmail("diego@bol.com");
        u.setIdLogin(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername("diegoferques");
        u.getCustomerCollection().add(c);
        return u;
    }

    private Address createFakeAddress(Customer customer) {
        Address a = new Address();
        a.setAddress("Rua Perlita");
        a.setCep("0000000");
        a.setCity("RJO");
        a.setCountry("BRA");
        a.setNeighborhood("Austin");
        a.setState("RJ");
        a.getCustomerCollection().add(customer);
        return a;
    }
}
