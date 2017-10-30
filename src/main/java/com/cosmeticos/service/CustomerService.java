package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Collection;
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
    private AddressService addressService;

    public Optional<Customer> find(Long idCustomer) {
        return Optional.of(repository.findOne(idCustomer));
    }

    public List<Customer> findBy(Customer probe) {
        if(probe.getUser() != null)
        {
            // Bacalhau dos campos q atrapalham a busca por example.
            User u = probe.getUser();
            u.setEvaluation(null);
            u.setLostPassword(null);
            u.setCreditCardCount(null);
        }
        return repository.findAll(Example.of(probe));
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

        c.setAddress(addressService.createFromCustomer(request));
        c.setUser(request.getCustomer().getUser());

        c.getUser().setCustomer(c);
        c.getUser().setPersonType(c.getPersonType());

        return repository.save(c);
    }

    public Customer update(CustomerRequestBody request) {
        Customer cr = request.getCustomer();
        Customer persistentCustomer = repository.findOne(cr.getIdCustomer());

        if(!StringUtils.isEmpty(cr.getBirthDate())) {
            persistentCustomer.setBirthDate(cr.getBirthDate());
        }

        if(!StringUtils.isEmpty(cr.getCellPhone())) {
            persistentCustomer.setCellPhone(cr.getCellPhone());
        }

        if(!StringUtils.isEmpty(cr.getCpf())) {
            persistentCustomer.setCpf(cr.getCpf());
        }

        if(!StringUtils.isEmpty(cr.getGenre())) {
            persistentCustomer.setGenre(cr.getGenre());
        }

        if(!StringUtils.isEmpty(cr.getNameCustomer())) {
            persistentCustomer.setNameCustomer(cr.getNameCustomer());
        }

        if(!StringUtils.isEmpty(cr.getStatus())) {
            persistentCustomer.setStatus(cr.getStatus());
        }

        if(!cr.getUser().getCreditCardCollection().isEmpty()) {

            Collection<CreditCard> requestCCs = cr.getUser().getCreditCardCollection();
            Collection<CreditCard> persistentCCs = persistentCustomer.getUser().getCreditCardCollection();

            if(!requestCCs.isEmpty())
            {
                // So pode haver um cartao
                CreditCard requestCc = requestCCs.stream().findFirst().get();

                if(persistentCCs.isEmpty())
                {
                    persistentCustomer.getUser().addCreditCard(requestCc);
                }
                else{

                    CreditCard persistentCc = persistentCCs.stream().findFirst().get();

                    persistentCc.setToken(requestCc.getToken() == null ? persistentCc.getToken() : requestCc.getToken());
                    persistentCc.setStatus(requestCc.getStatus() == null ? persistentCc.getStatus() : requestCc.getStatus());
                    persistentCc.setVendor(requestCc.getVendor() == null ? persistentCc.getVendor() : requestCc.getVendor());
                    persistentCc.setExpirationDate(requestCc.getExpirationDate() == null ? persistentCc.getExpirationDate() : requestCc.getExpirationDate());
                    persistentCc.setSuffix(requestCc.getSuffix() == null ? persistentCc.getSuffix() : requestCc.getSuffix());
                    persistentCc.setOwnerName(requestCc.getOwnerName() == null ? persistentCc.getOwnerName() : requestCc.getOwnerName());
                    persistentCc.setSecurityCode(requestCc.getSecurityCode() == null ? persistentCc.getSecurityCode() : requestCc.getSecurityCode());
                    persistentCc.setNumber(requestCc.getNumber() == null ? persistentCc.getNumber() : requestCc.getNumber());
                }
            }

            for (CreditCard c : requestCCs) {
                for (CreditCard persistentCc : persistentCCs) {
                    if(c.getNumber().equalsIgnoreCase(persistentCc.getNumber()))
                    {

                    }
                }
                persistentCustomer.getUser().addCreditCard(c);
            }
        }

        return repository.save(persistentCustomer);
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }
    

    public List<Customer> findAll() {
        return repository.findAll();
    }
}
