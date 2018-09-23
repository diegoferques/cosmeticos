package com.cosmeticos.service;

import com.cosmeticos.commons.HabilityRequestBody;
import com.cosmeticos.model.Hability;
import com.cosmeticos.repository.HabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by matto on 27/05/2017.
 */
@Service
public class HabilityService {

    @Autowired
    private HabilityRepository repository;

    public Optional<Hability> find(Long id) {
        return repository.findById(id);
    }

    public Hability create(Hability newHAbility) {
        return repository.save(newHAbility);
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }


    public Hability findByName(String name) {
        return repository.findByName(name);
    }


    public Optional<Hability> update(HabilityRequestBody request) {
        Hability incomingHability = request.getHability();

        Optional<Hability> optional = repository.findById(incomingHability.getId());

        if(optional.isPresent()) {

            Hability persistentHability = optional.get();

            if (!StringUtils.isEmpty(incomingHability.getName())) {
                persistentHability.setName(incomingHability.getName());
            }

            repository.save(persistentHability);

            return Optional.of(persistentHability);
        }
        else{
            return optional;
        }
    }

    public List<Hability> listAll() {

        Iterable<Hability> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }
}
