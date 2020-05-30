package com.leonardolariu.deliveryassistant;

import com.leonardolariu.deliveryassistant.models.ERole;
import com.leonardolariu.deliveryassistant.models.Role;
import com.leonardolariu.deliveryassistant.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@EnableJpaRepositories("com.leonardolariu.deliveryassistant.repositories")
public class DeliveryAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryAssistantApplication.class, args);
	}

	@Bean
	public CommandLineRunner checkRoles(RoleRepository roleRepository) {
		return args -> {
			List<Role> roles = roleRepository.findAll();
			if (roles.isEmpty()) {
				Stream.of(new Role(ERole.ROLE_USER),
					new Role(ERole.ROLE_ADMIN))
					.forEach(roleRepository::save);
			}
		};
	}
}