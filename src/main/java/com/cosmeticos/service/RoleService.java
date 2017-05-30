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

/**
 * Created by Lulu on 23/05/2017.
 */
@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public Role create(RoleRequestBody request)
    {
        return repository.save(request.getEntity());
    }

    public Role update(RoleRequestBody request)
    {
        Role roleFromRequest = request.getEntity();

        Role persistentRole  = repository.findOne(request.getEntity().getIdRole());
        persistentRole.setName(roleFromRequest.getName());
        persistentRole.setUserCollection(roleFromRequest.getUserCollection());

        return repository.save(persistentRole);
    }

    public Role find(Long id)
    {
        return repository.findOne(id);
    }

    public void delete()
    {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }
}
