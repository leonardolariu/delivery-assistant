package com.leonardolariu.deliveryassistant.repositories;

import com.leonardolariu.deliveryassistant.models.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findById(Long id);
}
