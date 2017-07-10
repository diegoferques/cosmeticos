package com.cosmeticos.service;

import com.cosmeticos.commons.LocationRequestBody;
import com.cosmeticos.model.Location;
import com.cosmeticos.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 10/07/2017.
 */
@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public List<Location> find10Lastest() {
        return locationRepository.findTop10();
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
}
