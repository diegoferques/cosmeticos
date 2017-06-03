package com.cosmeticos.service;

import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.model.Role;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.RoleRepository;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

        Optional<Role> optional = Optional.ofNullable(repository.findOne(requestedIdRole));

        if (optional.isPresent()) {
            Role persistentRole = optional.get();

            persistentRole.setName(roleFromRequest.getName());
            persistentRole.setUserCollection(roleFromRequest.getUserCollection());

            repository.save(persistentRole);
        }

        return optional;
    }

    public Optional<Role> find(Long id) {
        return Optional.ofNullable(repository.findOne(id));
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
