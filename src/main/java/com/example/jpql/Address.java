package com.example.jpql;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Getter
@Setter
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
