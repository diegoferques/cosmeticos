package com.cosmeticos.service;

import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.model.Role;
import com.cosmeticos.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Lulu on 23/05/2017.
 */
@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public Role create(RoleRequestBody request) {
        return repository.save(request.getEntity());
    }

    public Optional<Role> update(RoleRequestBody request) {
        Role roleFromRequest = request.getEntity();

        // TODO ver possibilidade de usar VO pq para update, o ID deve ser obrigatorio.
        Long requestedIdRole = roleFromRequest.getIdRole();

        Optional<Role> optional = (repository.findById(requestedIdRole));

        if (optional.isPresent()) {
            Role persistentRole = optional.get();

            persistentRole.setName(roleFromRequest.getName());
            persistentRole.setUserCollection(roleFromRequest.getUserCollection());

            repository.save(persistentRole);
        }

        return optional;
    }

    public Optional<Role> find(Long id) {
        return (repository.findById(id));
    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Role> findAll() {

        Iterable<Role> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }
}
