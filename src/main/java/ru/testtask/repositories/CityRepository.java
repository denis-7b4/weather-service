package ru.testtask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.models.Cities;

@Repository
public interface CityRepository extends JpaRepository<Cities, Long> {
    Cities findByCityAndCountry(String city, String country);
}
