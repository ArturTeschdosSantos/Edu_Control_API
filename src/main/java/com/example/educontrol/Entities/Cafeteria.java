package com.example.educontrol.cafeteria;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "cafeteria")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cafeteria {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String location;

    @Column(length = 255)
    private String openingTime;

    @Column(length = 255)
    private String closingTime;

    private boolean active;
}
