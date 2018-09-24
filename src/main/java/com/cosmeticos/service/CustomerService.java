package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Autowired
    private RoleService roleService;

    public Optional<Customer> find(Long idCustomer) {
        return (repository.findById(idCustomer));
    }

    public List<Customer> findBy(Customer probe) {
        if(probe.getUser() != null)
        {
            // Bacalhau dos campos q atrapalham a busca por example.
            User u = probe.getUser();
            u.setEvaluation(null);
            u.setLostPassword(null);
            u.setUserType(null);
            u.setStatus(User.Status.ACTIVE);
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
        c.setGenre(request.getCustomer().getGenre());
        c.setNameCustomer(request.getCustomer().getNameCustomer());
        c.setStatus(Customer.Status.ACTIVE.ordinal());

        c.setAddress(addressService.createFromCustomer(request));
        c.setUser(request.getCustomer().getUser());

        c.getUser().setCustomer(c);
        c.getUser().setPersonType(c.getPersonType());
        c.getUser().setStatus(User.Status.ACTIVE);

        return repository.save(c);
    }

    /**
     * @deprecated  Prefira {@link CustomerService#update(Customer)}
     * @param request
     * @return
     */
    public Customer update(CustomerRequestBody request) {
        return update(request.getCustomer());
    }


    public Customer update(Customer receivedCustomer) {
        Customer persistentCustomer = repository.findById(receivedCustomer.getIdCustomer()).get();

        if(!StringUtils.isEmpty(receivedCustomer.getBirthDate())) {
            persistentCustomer.setBirthDate(receivedCustomer.getBirthDate());
        }

        if(!StringUtils.isEmpty(receivedCustomer.getCellPhone())) {
            persistentCustomer.setCellPhone(receivedCustomer.getCellPhone());
        }

        if(!StringUtils.isEmpty(receivedCustomer.getCpf())) {
            persistentCustomer.setCpf(receivedCustomer.getCpf());
        }

        if(!StringUtils.isEmpty(receivedCustomer.getGenre())) {
            persistentCustomer.setGenre(receivedCustomer.getGenre());
        }

        if(!StringUtils.isEmpty(receivedCustomer.getNameCustomer())) {
            persistentCustomer.setNameCustomer(receivedCustomer.getNameCustomer());
        }

        if(!StringUtils.isEmpty(receivedCustomer.getStatus())) {
            persistentCustomer.setStatus(receivedCustomer.getStatus());
        }

        if(receivedCustomer.getAddress() != null) {
            Address persistentAddress = persistentCustomer.getAddress();
            if(persistentAddress == null) {
                persistentCustomer.setAddress(receivedCustomer.getAddress());
            }
            else
            {
                Address receivedAddress = receivedCustomer.getAddress();

                persistentAddress.setCep(receivedAddress.getCep());
                persistentAddress.setAddress(receivedAddress.getAddress());
                persistentAddress.setNumber(receivedAddress.getNumber());
                persistentAddress.setComplement(receivedAddress.getComplement());
                persistentAddress.setNeighborhood(receivedAddress.getNeighborhood());
                persistentAddress.setCity(receivedAddress.getCity());
                persistentAddress.setState(receivedAddress.getState());
                persistentAddress.setCountry(receivedAddress.getCountry());
                persistentAddress.setLatitude(receivedAddress.getLatitude());
                persistentAddress.setLongitude(receivedAddress.getLongitude());
            }

            persistentCustomer.getAddress().setCustomer(persistentCustomer);
        }

        if(!receivedCustomer.getUser().getCreditCardCollection().isEmpty()) {

            Collection<CreditCard> requestCCs = receivedCustomer.getUser().getCreditCardCollection();
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
