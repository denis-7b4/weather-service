package ru.testtask.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.testtask.models.Temperatures;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class WeatherRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @Test
    void weatherRepositoryTestEmpty() {
        Optional<List<Temperatures>> temperaturesFromTable = weatherRepository
                .findByCityAndCountryAndDateCreatedOrderByTimeCreated(
                        "Chelyabinsk"
                        , "Russia"
                        , LocalDate.now()
                );
        Assertions.assertTrue(temperaturesFromTable.isPresent());
        Assertions.assertTrue(temperaturesFromTable.get().isEmpty());
    }

    @Test
    void weatherRepositoryTest() {
        List<Temperatures> temperaturesList;
        fillTemperaturesBase();
        Optional<List<Temperatures>> temperaturesFromTable = weatherRepository
                .findByCityAndCountryAndDateCreatedOrderByTimeCreated(
                        "Chelyabinsk"
                        , "Russia"
                        , LocalDate.now()
                );
        Assertions.assertTrue(temperaturesFromTable.isPresent());
        temperaturesList = temperaturesFromTable.get();
        Assertions.assertFalse(temperaturesList.isEmpty());
        Assertions.assertEquals(3, temperaturesList.size());
        Assertions.assertEquals(10.11, temperaturesList.get(temperaturesList.size() - 1).getTemperature());
    }

    void fillTemperaturesBase() {
        System.out.println("Fill Temperatures started");
        weatherRepository.save(new Temperatures("Chelyabinsk", "Russia", 5.06));
        weatherRepository.save(new Temperatures("Chelyabinsk", "Russia", 13.00));
        weatherRepository.save(new Temperatures("Chelyabinsk", "Russia", 10.11));
        weatherRepository.save(new Temperatures("Fakecity", "Fakecountry", 20.00));
        System.out.println("Fill Temperatures ended");
    }
}
