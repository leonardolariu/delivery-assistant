package com.leonardolariu.deliveryassistant.repositories;

import com.leonardolariu.deliveryassistant.models.ERole;
import com.leonardolariu.deliveryassistant.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}