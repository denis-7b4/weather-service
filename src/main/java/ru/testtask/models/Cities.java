package ru.testtask.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "CITIES")
public class Cities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "CITY")
    @NotBlank(message = "City cannot be empty")
    private String city;
    @Column(name = "COUNTRY")
    @NotBlank(message = "Country cannot be empty")
    private String country;
    @Column(name = "LATITUDE")
    @NotNull(message = "Latitude cannot be null")
    private Double lat;
    @Column(name = "LONGITUDE")
    @NotNull(message = "Longitude cannot be null")
    private Double lon;
    @Column(name = "DISPLAY_NAME")
    private String displayName;

    public Cities() {
    }
    public Cities(String city, String country, Double lat, Double lon, String displayName) {
        this.city = city;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
        this.displayName = displayName;
    }
}
