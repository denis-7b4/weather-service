package ru.testtask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.testtask.models.Temperatures;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Temperatures, Long> {
    Optional<List<Temperatures>>
    findByCityAndCountryAndDateCreatedOrderByTimeCreated(String city, String country, LocalDate date);
}
