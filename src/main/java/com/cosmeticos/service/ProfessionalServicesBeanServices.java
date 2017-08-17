package com.cosmeticos.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import com.cosmeticos.commons.ProfessionalservicesRequestBody;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.repository.ProfessionalServicesRepository;

/**
 * Created by Vinicius on 21/06/2017.
 */
@org.springframework.stereotype.Service
public class ProfessionalServicesBeanServices {

    @Autowired
    private ProfessionalServicesRepository repository;

    public ProfessionalServices create(ProfessionalservicesRequestBody request){

        ProfessionalServices ps = new ProfessionalServices();
        ps.setProfessional(request.getEntity().getProfessional());
        ps.setCategory(request.getEntity().getCategory());

        return repository.save(ps);
    }

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

    //TODO - VERIFICAR SE TEM UMA FORMA MELHOR DE FAZER, ACHEI MUITO TRABALHOSO COMO ESTA ATUALMENTE
    //TODO - FALTA IMPLEMENTAR O METODO DO REPOSITORIO PARA TRAZER SOMENTE OS QUE CONTEMPLAM O SERVICE NO REQUEST
    public List<ProfessionalServices> getNearby(ProfessionalServices Service, String latitude, String longitude, String radius) {

        List<ProfessionalServices> professionalServicesList = repository.findAll(Example.of(Service));
        List<ProfessionalServices> professionalServices = new ArrayList<>();

        //ACHEI MELHOR PARSEAR SOMENTE UMA VEZ, POR ISSO CRIEI ESSA VARIAVEL
        Double distanciaLimite = Double.parseDouble(radius);

        for (ProfessionalServices psl: professionalServicesList) {

            if (psl.getProfessional().getAddress() != null) {

                if (!psl.getProfessional().getAddress().getLatitude().isEmpty() &&
                        !psl.getProfessional().getAddress().getLongitude().isEmpty()) {

                    Double distancia = getDistancia(
                            Double.parseDouble(latitude),
                            Double.parseDouble(longitude),
                            Double.parseDouble(psl.getProfessional().getAddress().getLatitude()),
                            Double.parseDouble(psl.getProfessional().getAddress().getLongitude())
                    );

                    if (distancia <= distanciaLimite) {
                        psl.getProfessional().setDistance(distancia.longValue());
                        professionalServices.add(psl);
                    }

                }
            }
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
