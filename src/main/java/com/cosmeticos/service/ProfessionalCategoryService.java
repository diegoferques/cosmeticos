package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalCategoryRequestBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalCategory;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Slf4j
@Service
public class ProfessionalCategoryService {

    @Autowired
    private ProfessionalCategoryRepository repository;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private VoteService voteService;

    public ProfessionalCategory create(ProfessionalCategoryRequestBody request){

        ProfessionalCategory ps = new ProfessionalCategory();
        ps.setProfessional(request.getEntity().getProfessional());
        ps.setCategory(request.getEntity().getCategory());
        ps.addPriceRule(request.getEntity().getPriceRuleList()
                .stream()
                .findFirst()
                .get()
        );

        return repository.save(ps);
    }

    public Optional<ProfessionalCategory> find(Long id){
        return Optional.ofNullable(repository.findOne(id));
    }

    public void delete(Long id){

        // TODO: DELETE fazer endpoint professionals/id/professionalCategory/id ... gambi abaixo
       ProfessionalCategory professionalCategory = repository.findOne(id);

        Optional<Professional> optional = professionalService.find(professionalCategory.getProfessional().getIdProfessional());

        Professional p = optional.get();

        p.getProfessionalCategoryCollection().remove(professionalCategory);

        professionalService.update(p);

        //repository.delete(id);

    }

    public List<ProfessionalCategory> findAll() {

        Iterable<ProfessionalCategory> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<ProfessionalCategory> search(String query) {
        return repository.search(query);

       /* return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());*/
    }


    /**
     * Usa a api Example do spring-data.
     * @param professionalCategoryProbe
     * @return
     */
    public List<ProfessionalCategory> findAllBy(ProfessionalCategory professionalCategoryProbe) {
        return this.repository.findAll(Example.of(professionalCategoryProbe));
    }

    public List<ProfessionalCategory> getNearby(ProfessionalCategory service, Double receivedLatitude, Double receivedLongitude, String radius, Boolean homeCare) {

        String serviceName = service.getCategory().getName();

        List<ProfessionalCategory> professionalCategoryList = null;

        if(homeCare) {
            professionalCategoryList = repository.findByPriceRuleNotNullAndServiceAndHomecare(serviceName);
        }
        else{
            professionalCategoryList = repository.findByPriceRuleNotNullAndService(serviceName);
        }

        List<ProfessionalCategory> professionalServices = new ArrayList<>();

        //ACHEI MELHOR PARSEAR SOMENTE UMA VEZ, POR ISSO CRIEI ESSA VARIAVEL
        Double distanciaLimite = Double.parseDouble(radius);

        for (ProfessionalCategory psl : professionalCategoryList) {

            Professional professional = psl.getProfessional();

            Address professionalAddress = professional.getAddress();

            if (professionalAddress != null) {

                Optional<Double> distanceFromOpt = professionalAddress.getDistanceFrom(receivedLatitude, receivedLongitude);

                if(distanceFromOpt.isPresent())
                {
                    Double distance = distanceFromOpt.get();

                    if (distance <= distanciaLimite) {
                        professional.setDistance(distance.longValue());
                        professionalServices.add(psl);
                    }
                }
            }

            User persistentUser = professional.getUser();

            persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));
        }


        return professionalServices;
    }

}
