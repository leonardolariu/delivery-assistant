package com.leonardolariu.deliveryassistant.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        })
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    private Set<Driver> drivers = new HashSet<>();

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    private Set<Delivery> deliveries = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }



    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public void removeDriver(Driver driver) {
        drivers.remove(driver);
    }

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    public Optional<Delivery> getDailyDelivery() {
        return deliveries.stream()
                .filter(this::checkDate)
                .findFirst();
    }

    private boolean checkDate(Delivery delivery) {
        Calendar today = new GregorianCalendar();
        String todayString = (new SimpleDateFormat("dd/MM/yyyy")).format(today.getTime());
        String deliveryDateString = delivery.getDeliveryDateString();

        return todayString.equals(deliveryDateString);
    }

    public boolean hasDriver(String email) {
        for (Driver driver: drivers) {
            if (driver.getEmail().equals(email))
                return true;
        }

        return false;
    }
}
