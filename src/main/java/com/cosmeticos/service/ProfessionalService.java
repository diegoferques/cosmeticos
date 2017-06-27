package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalRequestBody;
import com.cosmeticos.model.*;
import com.cosmeticos.model.Professional;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        newProfessional.setAddress(request.getProfessional().getAddress());
        newProfessional.setUser(request.getProfessional().getUser());

        professionalRepository.save(newProfessional);

        configureHability(request.getProfessional(), newProfessional);
        configureProfessionalServices(request.getProfessional(), newProfessional);

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

    private void configureProfessionalServices(Professional receivedProfessional, Professional newProfessional) {
        Set<ProfessionalServices> receivedProfessionalServices =
                receivedProfessional.getProfessionalServicesCollection();

        receivedProfessionalServices.stream().forEach(ps -> {
            ps.setProfessional(newProfessional);

            ProfessionalServicesPK pk = new ProfessionalServicesPK(ps);
            ps.setProfessionalServicesPK(pk);

            newProfessional.getProfessionalServicesCollection().add(ps);
        });
    }

    private void configureHability(Professional receivedProfessional, Professional newProfessional) {
        Collection<Hability> habilityList = receivedProfessional.getHabilityCollection();

        if(habilityList != null)
        {
            for (Hability h : receivedProfessional.getHabilityCollection()) {

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
        }
    }
}
