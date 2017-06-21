package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.model.Professional;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 27/05/2017.
 */
@Service
public class ProfessionalService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private HabilityService habilityService;

    public Optional<Professional> find(Long idProfessional) {
        return Optional.ofNullable(professionalRepository.findOne(idProfessional));
    }

    public Professional create(ProfessionalRequestBody request) {

        Professional newProfessional = new Professional();

        newProfessional.setBirthDate(request.getProfessional().getBirthDate());
        newProfessional.setCellPhone(request.getProfessional().getCellPhone());
        newProfessional.setCnpj(request.getProfessional().getCnpj());
        newProfessional.setGenre(request.getProfessional().getGenre());
        newProfessional.setNameProfessional(request.getProfessional().getNameProfessional());
        newProfessional.setStatus(Professional.Status.ACTIVE);
        newProfessional.setDateRegister(Calendar.getInstance().getTime());
        newProfessional.setIdAddress(request.getAddress());
        newProfessional.setIdLogin(request.getUser());
        newProfessional.setProfessionalServicesCollection(request.getProfessional().getProfessionalServicesCollection());
        newProfessional.setHabilityCollection(new ArrayList<>());

        for (Hability h : request.getProfessional().getHabilityCollection()) {

            Optional<Hability> optional = Optional.ofNullable(habilityService.findByName(h.getName()));

            if(optional.isPresent()) {
                newProfessional.getHabilityCollection().add(optional.get());
            }
            else
            {
                Hability newHability = new Hability();
                newHability.setName(h.getName());
                Hability persistentHability = habilityService.create(newHability);

                newProfessional.getHabilityCollection().add(persistentHability);
            }

        }

        return professionalRepository.save(newProfessional);
    }

    public Optional<Professional> update(ProfessionalRequestBody request) {
        Professional cr = request.getProfessional();

        Optional<Professional> optional = Optional.ofNullable(professionalRepository.findOne(cr.getIdProfessional()));

        if(optional.isPresent()) {

            Professional customer = optional.get();

            if (!StringUtils.isEmpty(cr.getBirthDate())) {
                customer.setBirthDate(cr.getBirthDate());
            }

            if (!StringUtils.isEmpty(cr.getCellPhone())) {
                customer.setCellPhone(cr.getCellPhone());
            }

            if (!StringUtils.isEmpty(cr.getCnpj())) {
                customer.setCnpj(cr.getCnpj());
            }

            if (!StringUtils.isEmpty(cr.getGenre())) {
                customer.setGenre(cr.getGenre());
            }

            if (!StringUtils.isEmpty(cr.getNameProfessional())) {
                customer.setNameProfessional(cr.getNameProfessional());
            }

            if (!StringUtils.isEmpty(cr.getStatus())) {
                customer.setStatus(cr.getStatus());
            }

            professionalRepository.save(customer);

            return Optional.of(customer);
        }
        else{
            return optional;
        }
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }


    public List<Professional> find10Lastest() {
        return professionalRepository.findTop10ByOrderByDateRegisterDesc();
    }
}
