package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by matto on 10/06/2017.
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address createFromCustomer(CustomerRequestBody request) {
        Address a = new Address();
        a.setAddress(request.getCustomer().getIdAddress().getAddress());
        a.setCep(request.getCustomer().getIdAddress().getCep());
        a.setCity(request.getCustomer().getIdAddress().getCity());
        a.setState(request.getCustomer().getIdAddress().getState());
        a.setCountry(request.getCustomer().getIdAddress().getCountry());
        a.setNeighborhood(request.getCustomer().getIdAddress().getNeighborhood());

        return addressRepository.save(a);
    }

    public Address createFakeAddress() {
        Address a = new Address();
        a.setAddress("Avenida Presidente Vargas, 309");
        //a.setNumero("309");
        a.setCep("20940-000");
        a.setCity("Rio de Janeiro");
        a.setState("RJ");
        a.setCountry("BRA");
        a.setNeighborhood("Centro");
        //a.getCustomerCollection().add(customer);
        addressRepository.save(a);
        return a;
    }

}
