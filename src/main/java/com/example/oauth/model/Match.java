package com.example.oauth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
@Entity
@Table(name="matches")
public class Match {

    @Id
    @GeneratedValue
    private long id;


    @NotNull
    private String player1;

    @NotNull
    private String player2;

    private Boolean player1Voted = false;

    private Boolean player2Voted = false;

    private String player1Vote;

    private String player2Vote;

    private Boolean accepted = false;

    private String winner ;


}
