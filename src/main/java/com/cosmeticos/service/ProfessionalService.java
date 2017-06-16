package com.cosmeticos.service;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by matto on 27/05/2017.
 */
@Service
public class ProfessionalService {

    @Autowired
    private ProfessionalRepository repository;

    public Professional find(Long idCustomer) {
        return repository.findOne(idCustomer);
    }

    public Professional create(ProfessionalRequestBody request) {

        Professional c = new Professional();

        c.setBirthDate(request.getCustomer().getBirthDate());
        c.setCellPhone(request.getCustomer().getCellPhone());
        c.setCnpj(request.getCustomer().getCnpj());
        c.setGenre(request.getCustomer().getGenre());
        c.setNameProfessional(request.getCustomer().getNameProfessional());
        c.setStatus(Professional.Status.ACTIVE);
        c.setDateRegister(Calendar.getInstance().getTime());
        c.setIdAddress(request.getAddress());
        c.setIdLogin(request.getUser());

        return repository.save(c);
    }

}
