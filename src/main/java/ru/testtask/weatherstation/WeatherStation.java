package ru.testtask.weatherstation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.testtask.models.Cities;
import ru.testtask.models.Temperatures;
import ru.testtask.repositories.WeatherRepository;
import ru.testtask.weatherstation.requestors.ClientTomorrow;
import ru.testtask.weatherstation.requestors.ClientVisualcrossing;
import ru.testtask.weatherstation.requestors.ClientWeatherbit;

@Slf4j
@Component
public class WeatherStation {
    private final Long requestInterval;
    private final String urlTomorrow;
    private final String urlWeatherbit;
    private final String urlVisualcrossing;
    private final ClientTomorrow clientTomorrow;
    private final ClientVisualcrossing clientVisualcrossing;
    private final ClientWeatherbit clientWeatherbit;
    String weatherDataNotFound = "Weather data for \"{}\" city in the \"{}\" country not found";
    private final List<Double> temperaturesCollected = new ArrayList<>(3);
    @Autowired
    private final CityLocator cityLocator;
    @Autowired
    private final WeatherRepository weatherRepository;
    private List<Cities> requestCitiesList;

    @Autowired
    public WeatherStation(
            @Value("${weather.interval:60}") Long requestInterval
          , @Value("${weather.apikey.tomorrow}") String apiKeyTomorrow
          , @Value("${weather.url.tomorrow}") String urlTomorrow
          , @Value("${weather.apikey.weatherbit}") String apiKeyWeatherbit
          , @Value("${weather.url.weatherbit}") String urlWeatherbit
          , @Value("${weather.apikey.visualcrossing}") String apiKeyVisualcrossing
          , @Value("${weather.url.visualcrossing}") String urlVisualcrossing,
            CityLocator cityLocator, WeatherRepository weatherRepository) {
        this.requestInterval = requestInterval;
        this.urlTomorrow = urlTomorrow;
        this.urlWeatherbit = urlWeatherbit;
        this.urlVisualcrossing = urlVisualcrossing;
        this.cityLocator = cityLocator;
        this.weatherRepository = weatherRepository;
        this.clientTomorrow = new ClientTomorrow(apiKeyTomorrow);
        this.clientVisualcrossing = new ClientVisualcrossing(apiKeyVisualcrossing);
        this.clientWeatherbit = new ClientWeatherbit(apiKeyWeatherbit);
    }

    @Async
    @Bean
    public void weatherStationMainThread() {
        Thread.currentThread().setName("WeatherStation");
        log.info("{} started", Thread.currentThread().getName());
        try {
            requestCitiesList = cityLocator.getCities();
        } catch (FileNotFoundException fileNotFoundException) {
            Thread.currentThread().interrupt();
        }

        WebClient tomorrowClient = WebClient.create(urlTomorrow);
        WebClient weatherbitClient = WebClient.create(urlWeatherbit);
        WebClient visualcrossingClient = WebClient
                .create(urlVisualcrossing);

        while (!Thread.interrupted()) {
            log.info("Requesting weather data...");
            for (Cities curCity : requestCitiesList ) {

                Double temperature;
                temperature = clientTomorrow
                        .getTemperature(tomorrowClient, curCity.getLat(), curCity.getLon());
                if (temperature != null) {
                    temperaturesCollected.add(temperature);
                } else {
                    log.info(weatherDataNotFound, curCity.getCity(), curCity.getCountry());
                }
                temperature = clientWeatherbit
                        .getTemperature(weatherbitClient, curCity.getLat(), curCity.getLon());
                if (temperature != null) {
                    temperaturesCollected.add(temperature);
                } else {
                    log.info(weatherDataNotFound, curCity.getCity(), curCity.getCountry());
                }
                temperature = clientVisualcrossing
                        .getTemperature(visualcrossingClient, curCity.getLat(), curCity.getLon());
                if (temperature != null) {
                    temperaturesCollected.add(temperature);
                } else {
                    log.info(weatherDataNotFound, curCity.getCity(), curCity.getCountry());
                }
                double sumTemperature = 0.0;
                log.debug("Temperatures: {}", temperaturesCollected);
                for (double temp : temperaturesCollected) {
                    sumTemperature = sumTemperature + temp;
                }
                double averageTemperature = sumTemperature / temperaturesCollected.size();
                averageTemperature = Math.round(averageTemperature*10)/10D;
                temperaturesCollected.clear();
                Temperatures temperatureRow = new Temperatures();
                temperatureRow.setCity(curCity.getCity());
                temperatureRow.setCountry(curCity.getCountry());
                temperatureRow.setTemperature(averageTemperature);
                weatherRepository.save(temperatureRow);
            }
            log.info("Weather data requested for all cities");
            try {
                Thread.sleep(requestInterval*60*1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("Weather station stopped");
    }
}
