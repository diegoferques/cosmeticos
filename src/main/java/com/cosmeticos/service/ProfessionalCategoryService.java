package com.cosmeticos.service;

import com.cosmeticos.commons.ProfessionalCategoryRequestBody;
import com.cosmeticos.model.PriceRule;
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
import java.util.Set;
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
        repository.delete(id);
    }

    public List<ProfessionalCategory> findAll() {

        Iterable<ProfessionalCategory> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }


    /**
     * Usa a api Example do spring-data.
     * @param professionalCategoryProbe
     * @return
     */
    public List<ProfessionalCategory> findAllBy(ProfessionalCategory professionalCategoryProbe) {
        return this.repository.findAll(Example.of(professionalCategoryProbe));
    }

    //TODO - VERIFICAR SE TEM UMA FORMA MELHOR DE FAZER, ACHEI MUITO TRABALHOSO COMO ESTA ATUALMENTE
    //TODO - FALTA IMPLEMENTAR O METODO DO REPOSITORIO PARA TRAZER SOMENTE OS QUE CONTEMPLAM O SERVICE NO REQUEST
    public List<ProfessionalCategory> getNearby(ProfessionalCategory Service, String latitude, String longitude, String radius) {


        List<ProfessionalCategory> professionalCategoryList = repository.findByPriceRuleNotNull();
        List<ProfessionalCategory> professionalServices = new ArrayList<>();

        //ACHEI MELHOR PARSEAR SOMENTE UMA VEZ, POR ISSO CRIEI ESSA VARIAVEL
        Double distanciaLimite = Double.parseDouble(radius);

            for (ProfessionalCategory psl : professionalCategoryList) {

                Professional professional = psl.getProfessional();

                if (professional.getAddress() != null) {

                    if (!professional.getAddress().getLatitude().isEmpty() &&
                            !professional.getAddress().getLongitude().isEmpty()) {

                        Double distancia = getDistancia(
                                Double.parseDouble(latitude),
                                Double.parseDouble(longitude),
                                Double.parseDouble(professional.getAddress().getLatitude()),
                                Double.parseDouble(professional.getAddress().getLongitude())
                        );

                        if (distancia <= distanciaLimite) {
                            professional.setDistance(distancia.longValue());
                            professionalServices.add(psl);
                        }

                    }
                }

                User persistentUser = professional.getUser();

                persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));
            }


        return professionalServices;
    }

    private double getDistancia(double latitude, double longitude, double latitudePto, double longitudePto){

        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);
        latitudePto = Math.toRadians(latitudePto);
        longitudePto = Math.toRadians(longitudePto);

        double dlon, dlat, a, distancia;
        dlon = longitudePto - longitude;
        dlat = latitudePto - latitude;
        a = Math.pow(Math.sin(dlat/2),2) + Math.cos(latitude) * Math.cos(latitudePto) * Math.pow(Math.sin(dlon/2),2);
        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6378140 * distancia; /* 6378140 is the radius of the Earth in meters*/
    }
}
