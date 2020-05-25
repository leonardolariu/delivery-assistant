package com.leonardolariu.deliveryassistant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.stream.Stream;

@SpringBootApplication
public class DeliveryAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryAssistantApplication.class, args);
	}

	@Bean
	public CommandLineRunner houses(HouseRepository houseRepository) {
		return args -> {
			Stream.of(new House("Iasi"),
					new House("Cluj"),
					new House("Bucuresti"))
					.forEach(houseRepository::save);

			houseRepository.findAll()
					.forEach(house -> System.out.println("House " + house.getId() + ": " + house.getAddress()));
		};
	}
}

interface HouseRepository extends CrudRepository<House, Long> {}

@Entity
class House {

	@Id
	@GeneratedValue
	private long id;

	private String address;

	public House(String address) {
		this.address = address;
	}

	public House() { }

	public long getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}
}