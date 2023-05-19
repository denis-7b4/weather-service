package ru.testtask.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.models.Temperatures;
import ru.testtask.repositories.WeatherRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Tag(name = "Weather service",
        description = "Weather service API that returns either temperatures"
                + " for requested City in Country on specific date (if specified)"
                + " or last known temperature (if date not specified)")
@RestController
@RequestMapping("/")
public final class WeatherController {
    @Autowired
    private WeatherRepository weatherRepository;

    @Contract(pure = true)
    @GetMapping("/version")
    public @NotNull String ver() {
        return "1.0.0";
    }

    /* http://localhost:8081/weather?city=Chelyabinsk&country=Russia&date=YYYY-MM-DD */
    @Contract("_, _, _ -> new")
    @GetMapping("/weather")
    public @NotNull ResponseEntity<List<Temperatures>> getTemperaturesByDate(
            @RequestParam("city") String cityName
            , @RequestParam("country") String countryName
            , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        boolean dateProvided = false;

        List<Temperatures> temperaturesList;
        List<Temperatures> currentTemperature = new ArrayList<>();
        if (date == null) {
            date = LocalDate.now();
        } else {
            dateProvided = true;
        }
        Optional<List<Temperatures>> temperaturesFromTable =
                weatherRepository
                        .findByCityAndCountryAndDateCreatedOrderByTimeCreated(
                                cityName
                                , countryName
                                , date);
        if (temperaturesFromTable.isPresent()) {
            temperaturesList = temperaturesFromTable.get();
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        if (temperaturesList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (!dateProvided) {
            currentTemperature
                    .add(temperaturesList.get(temperaturesList.size() - 1));
            return new ResponseEntity<>(currentTemperature, HttpStatus.OK);
        }
        return new ResponseEntity<>(temperaturesList, HttpStatus.OK);
    }
}
