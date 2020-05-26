package com.leonardolariu.deliveryassistant;

import com.leonardolariu.deliveryassistant.repositories.IHouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.leonardolariu.deliveryassistant.repositories")
public class DeliveryAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryAssistantApplication.class, args);
	}

	@Bean
	public CommandLineRunner houses(IHouseRepository houseRepository) {
		return args -> {
//			Stream.of(new House("Iasi"),
//					new House("Cluj"),
//					new House("Bucuresti"))
//					.forEach(houseRepository::save);

			houseRepository.findAll()
					.forEach(house -> System.out.println("House " + house.getId() + ": " + house.getAddress()));
		};
	}
}