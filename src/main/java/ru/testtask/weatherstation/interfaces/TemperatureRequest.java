package ru.testtask.weatherstation.interfaces;

import org.springframework.web.reactive.function.client.WebClient;

public interface TemperatureRequest {

    String WEATHER_DATA_NOT_FOUND = "Weather data cannot be retrieved from \"{}\" service";

    Double getTemperature(WebClient webClient, Double latitude, Double longitude);
}
