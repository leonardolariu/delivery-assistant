package com.leonardolariu.deliveryassistant.repositories;

import com.leonardolariu.deliveryassistant.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findById(Long id);

    Optional<Driver> findByEmail(String email);
}