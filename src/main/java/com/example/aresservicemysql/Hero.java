package com.example.aresservicemysql;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String codeName;
    private String email;
    private String team;

    public Hero() {}

    public Hero(String firstName, String lastName, String codeName, String email, String team) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.codeName = codeName;
        this.email = email;
        this.team = team;
    }

}
