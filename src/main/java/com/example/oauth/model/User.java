package com.example.oauth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="users")

public class User {
    @Id
   @GeneratedValue
    private int id;

    private String name;

    private int score = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
