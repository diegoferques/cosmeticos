package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalservicesRequestBody;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ProfessionalServicesRepository;
import com.cosmeticos.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 21/06/2017.
 */
@org.springframework.stereotype.Service
public class ProfessionalServicesBeanServices {

    @Autowired
    private ProfessionalServicesRepository repository;

    @Autowired
    private ServiceRepository repositoryService;

    @Autowired
    private ProfessionalRepository repositoryProfessional;

    public ProfessionalServices create(ProfessionalservicesRequestBody request){

        long idProfessional = request.getEntity().getProfessional().getIdProfessional();
        long idService = request.getEntity().getService().getIdService();

        ProfessionalServices ps = new ProfessionalServices(idProfessional, idService);
        ps.setProfessional(request.getEntity().getProfessional());
        ps.setService(request.getEntity().getService());

        return repository.save(ps);
    }

    /*public Optional<Professional> update(ProfessionalservicesRequestBody request) {
        ProfessionalServices professionalServicesFromRequest = request.getEntity();

        // TODO ver possibilidade de usar VO pq para update, o ID deve ser obrigatorio.
        Long requestedIdService = professionalServicesFromRequest.getService().getIdService();
        Long requestedIdProfessional = professionalServicesFromRequest.getProfessional().getIdProfessional();

        Optional<Service> optional = Optional.ofNullable(repositoryService.findOne(requestedIdService));
        Optional<Professional> optional2 = Optional.ofNullable(repositoryProfessional.findOne(requestedIdProfessional));


        if (optional.isPresent()) {
            ProfessionalServices persistentService = optional.get();

            persistentService.setService(professionalServicesFromRequest.getService());
            repository.save(persistentService);
            return optional;
        }




        if (optional2.isPresent()){
            ProfessionalServices persistentService2 = optional2.get();

            persistentService2.setProfessional(professionalServicesFromRequest.getProfessional());
            repository.save(persistentService2);
        }
        return optional2;
    }*/

    public Optional<ProfessionalServices> find(Long id){
        return Optional.ofNullable(repository.findOne(id));
    }

    public void deletar(){
        throw new UnsupportedOperationException("Excluir de acordo com o Status. ");
    }

    public List<ProfessionalServices> findAll() {

        Iterable<ProfessionalServices> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }


    /**
     * Usa a api Example do spring-data.
     * @param professionalServicesProbe
     * @return
     */
    public List<ProfessionalServices> findAllBy(ProfessionalServices professionalServicesProbe) {
        return this.repository.findAll(Example.of(professionalServicesProbe));
    }
}
