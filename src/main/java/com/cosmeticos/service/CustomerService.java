package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.Customer;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public Optional<Customer> find(Long idCustomer) {
        return Optional.of(repository.findOne(idCustomer));
    }

    public Customer create(CustomerRequestBody request) {
        //LocalDate birthDate = LocalDate.of(request.getBirthYear(), request.getBirthMonth(), request.getBirthDay());

        Customer c = new Customer();

        c.setBirthDate(request.getCustomer().getBirthDate());
        c.setCellPhone(request.getCustomer().getCellPhone());
        c.setCpf(request.getCustomer().getCpf());
        //TODO - Não poderia ser o TimeStamp do banco?
        c.setDateRegister(Calendar.getInstance().getTime());
        //c.setDateRegister(Timestamp.valueOf(LocalDateTime.now()));
        c.setGenre(request.getCustomer().getGenre());
        c.setNameCustomer(request.getCustomer().getNameCustomer());
        //c.setOrderCollection(null);
        c.setStatus(Customer.Status.ACTIVE.ordinal());

        c.setIdAddress(addressService.createFromCustomer(request));
        //c.setIdLogin(userService.createFromCustomer(request));

        return repository.save(c);
    }

    public Customer update(CustomerRequestBody request) {
        Customer cr = request.getCustomer();
        Customer customer = repository.findOne(cr.getIdCustomer());

        if(!StringUtils.isEmpty(cr.getBirthDate())) {
            customer.setBirthDate(cr.getBirthDate());
        }

        if(!StringUtils.isEmpty(cr.getCellPhone())) {
            customer.setCellPhone(cr.getCellPhone());
        }

        if(!StringUtils.isEmpty(cr.getCpf())) {
            customer.setCpf(cr.getCpf());
        }

        if(!StringUtils.isEmpty(cr.getGenre())) {
            customer.setGenre(cr.getGenre());
        }

        if(!StringUtils.isEmpty(cr.getNameCustomer())) {
            customer.setNameCustomer(cr.getNameCustomer());
        }

        if(!StringUtils.isEmpty(cr.getStatus())) {
            customer.setStatus(cr.getStatus());
        }

        return repository.save(customer);
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Customer> find10Lastest() {
        return repository.findTop10ByOrderByDateRegisterDesc();
    }
}
