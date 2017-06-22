package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalservicesRequestBody;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.repository.ProfessionalServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Created by Vinicius on 21/06/2017.
 */
public class ProfessionalServicesBeanServices {

    @Autowired
    private ProfessionalServicesRepository repository;

    public ProfessionalServices create(ProfessionalservicesRequestBody request){
        return repository.save(request.getEntity());
    }

    public Optional<ProfessionalServices> update (ProfessionalservicesRequestBody request){
        ProfessionalServices professionalServicesFromRequest = request.getEntity();

       // Long requestedIdProfessionalServices = professionalServicesFromRequest.getClass();
        throw new UnsupportedOperationException("Este metodo ainda nao eh suportado");
    }

}
