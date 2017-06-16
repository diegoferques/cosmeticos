package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ProfessionalRepository;
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
public class ProfessionalService {

    @Autowired
    private ProfessionalRepository repository;

    public Optional<Professional> find(Long idProfessional) {
        return Optional.ofNullable(repository.findOne(idProfessional));
    }

    public Professional create(ProfessionalRequestBody request) {

        Professional c = new Professional();

        c.setBirthDate(request.getProfessional().getBirthDate());
        c.setCellPhone(request.getProfessional().getCellPhone());
        c.setCnpj(request.getProfessional().getCnpj());
        c.setGenre(request.getProfessional().getGenre());
        c.setNameProfessional(request.getProfessional().getNameProfessional());
        c.setStatus(Professional.Status.ACTIVE);
        c.setDateRegister(Calendar.getInstance().getTime());
        c.setIdAddress(request.getAddress());
        c.setIdLogin(request.getUser());

        return repository.save(c);
    }

    public Professional update(ProfessionalRequestBody request) {
        Professional cr = request.getProfessional();
        Professional customer = repository.findOne(cr.getIdProfessional());

        if(!StringUtils.isEmpty(cr.getBirthDate())) {
            customer.setBirthDate(cr.getBirthDate());
        }

        if(!StringUtils.isEmpty(cr.getCellPhone())) {
            customer.setCellPhone(cr.getCellPhone());
        }

        if(!StringUtils.isEmpty(cr.getCnpj())) {
            customer.setCnpj(cr.getCnpj());
        }

        if(!StringUtils.isEmpty(cr.getGenre())) {
            customer.setGenre(cr.getGenre());
        }

        if(!StringUtils.isEmpty(cr.getNameProfessional())) {
            customer.setNameProfessional(cr.getNameProfessional());
        }

        if(!StringUtils.isEmpty(cr.getStatus())) {
            customer.setStatus(cr.getStatus());
        }

        return repository.save(customer);
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }


    public List<Professional> find10Lastest() {
        return repository.findTop10ByOrderByDateRegisterDesc();
    }
}
