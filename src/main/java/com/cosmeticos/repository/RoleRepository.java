package com.cosmeticos.repository;

import com.cosmeticos.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Lulu on 30/05/2017.
 */
@Transactional
public interface RoleRepository extends CrudRepository<Role, Long> {
}
