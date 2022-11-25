package ru.testtask.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Entity
@Table(name = "TEMPERATURE")
public class Temperatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CITY")
    @NotBlank(message = "City cannot be empty")
    private String city;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "TEMPERATURE")
    private Double temperature;
    @Column(name = "CREATED_DATE")
    @NotNull(message = "Creation date cannot be null")
    private LocalDate dateCreated;
    @Column(name = "CREATED_TIME")
    private LocalTime timeCreated;

    public Temperatures() {
        this.dateCreated = LocalDate.now();
        this.timeCreated = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public Temperatures(String city, String country, Double temperature) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.dateCreated = LocalDate.now();
        this.timeCreated = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
