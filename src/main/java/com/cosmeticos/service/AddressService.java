package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.google.LocationGoogle;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by matto on 10/06/2017.
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProfessionalRepository professionalRepository;

    public Address createFromCustomer(CustomerRequestBody request) {
        Address a = new Address();
        Customer customer = request.getCustomer();
        Address address = customer.getAddress();
        String complement = address.getComplement();

        a.setAddress(request.getCustomer().getAddress().getAddress());
        a.setCep(request.getCustomer().getAddress().getCep());
        a.setCity(request.getCustomer().getAddress().getCity());
        a.setState(request.getCustomer().getAddress().getState());
        a.setCountry(request.getCustomer().getAddress().getCountry());
        a.setNeighborhood(request.getCustomer().getAddress().getNeighborhood());
        a.setComplement(complement);

        LocationGoogle geocode = locationService.getGeoCode(a);
        if(geocode  != null) {
            a.setLatitude(geocode.getLat().toString());
            a.setLongitude(geocode.getLng().toString());
        } else {
            a.setLatitude("0");
            a.setLongitude("0");
        }

        return a;
    }

    public Address updateGeocodeFromProfessionalCreate(Professional professional) {

        Address address = professional.getAddress();

        if(address != null) {
            LocationGoogle geocode = locationService.getGeoCode(address);

            address.setLatitude(geocode.getLat().toString());
            address.setLongitude(geocode.getLng().toString());
            address.setProfessional(professional);


        }
return address;
    }

    public void updateGeocodeFromProfessionalUpdate(Professional professionalRequest) {

        Optional<Professional> professionalOptional = Optional.ofNullable(professionalRepository.findOne(professionalRequest.getIdProfessional()));
        Address address = professionalOptional.get().getAddress();
        //Address address = professionalRequest.getAddress();

        Address addressRequest = professionalRequest.getAddress();

        if(address != null) {

            address.setAddress(addressRequest.getAddress());
            address.setNeighborhood(addressRequest.getNeighborhood());
            address.setCep(addressRequest.getCep());
            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setCountry(addressRequest.getCountry());
            address.setComplement(addressRequest.getComplement());

            LocationGoogle geocode = locationService.getGeoCode(address);

            address.setLatitude(geocode.getLat().toString());
            address.setLongitude(geocode.getLng().toString());
            //address.setProfessional(professional);
        } else {
            address.setLatitude("");
            address.setLongitude("");
        }

        addressRepository.save(address);
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
