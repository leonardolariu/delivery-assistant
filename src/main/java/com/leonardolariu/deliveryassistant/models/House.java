package com.leonardolariu.deliveryassistant.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class House {

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
