package com.leonardolariu.deliveryassistant.repositories;

import com.leonardolariu.deliveryassistant.models.House;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHouseRepository extends CrudRepository<House, Long> {}
