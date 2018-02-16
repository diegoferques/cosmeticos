package com.cosmeticos.service;

import com.cosmeticos.commons.LocationRequestBody;
import com.cosmeticos.commons.google.GoogleMapsResponseBody;
import com.cosmeticos.commons.google.LocationGoogle;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Location;
import com.cosmeticos.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 10/07/2017.
 */
@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public List<Location> find10Lastest() {
        return locationRepository.findTop10ByOrderById();
    }

    public Optional<Location> find(Long idLocation) {
        return Optional.of(locationRepository.findOne(idLocation));
    }

    public Location create(LocationRequestBody request) {
        Location location = new Location();
        location.setLatitude(request.getLocation().getLatitude());
        location.setLongitude(request.getLocation().getLongitude());
        //location.setOrderCollection(request.getLocation().getOrderCollection());

        return locationRepository.save(location);
    }

    public Location update(LocationRequestBody request) {

        Location lr = request.getLocation();
        Location location = locationRepository.findOne(lr.getId());

        if(!StringUtils.isEmpty(lr.getLatitude())) {
            location.setLatitude(lr.getLatitude());
        }

        if(!StringUtils.isEmpty(lr.getLongitude())) {
            location.setLongitude(lr.getLongitude());
        }

        //TODO - VERIFICAR SE ESTA CORRETA A FORMA DE VALIDAR PARA O OBJETO ABAIXO
        if(!StringUtils.isEmpty(lr.getOrderCollection())) {
            location.setOrderCollection(lr.getOrderCollection());
        }

        return locationRepository.save(location);
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros.");
    }

    public LocationGoogle getGeoCode(Address a) {


        String googleKey = "AIzaSyDdNCXGRO5OxTcBkpzgURHceKgbZyj1g9w";
        String googleUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
        String urlSource = googleUrl +
                "address=" + a.getAddress()+ a.getNeighborhood() + a.getCity() + a.getState() +
                "&key=" + googleKey;

        RestTemplate restTemplate = restTemplateBuilder.build();

        LocationGoogle sourceLocation;

        try {
            ResponseEntity<GoogleMapsResponseBody> sourceResponse =
                    restTemplate.getForEntity(urlSource, GoogleMapsResponseBody.class);

            sourceLocation = sourceResponse.getBody().getResults()
                    .stream().findFirst().get()
                    .getGeometry().getLocation();

        } catch (Exception e) {
            sourceLocation = new LocationGoogle();
            sourceLocation.setLat(0.0);
            sourceLocation.setLng(0.0);
        }

        return sourceLocation;
    }
}
