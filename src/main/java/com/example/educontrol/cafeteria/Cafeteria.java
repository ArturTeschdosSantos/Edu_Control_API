package com.example.educontrol.cafeteria;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "cafeteria")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class Cafeteria {


    @Id 
    @GeneratedValue
    private UUID id;
    private String name;
    private String location;
    private String openingTime;
    private String closingTime;
    private boolean active;

    
}
