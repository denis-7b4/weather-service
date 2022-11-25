package ru.testtask.weatherstation.requestors;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;
import ru.testtask.weatherstation.interfaces.TemperatureRequest;

import java.time.LocalDate;

@Slf4j
public class ClientVisualcrossing implements TemperatureRequest {

    private final String apiKey;
    static final String TEMPERATURE_PATH = "$['currentConditions']['temp']";
    static final String CURRENT_SERVICE = "VisualCrossing";

    public ClientVisualcrossing(String apiKeyVisualcrossing) {
        this.apiKey = apiKeyVisualcrossing;
    }

    @Override
    public Double getTemperature(@NotNull WebClient webClient, Double latitude, Double longitude) {
        Double temperatureReturn = null;
        String requestString = new StringBuilder()
                .append("/")
                .append(latitude)
                .append(",")
                .append(longitude)
                .append("/")
                .append(LocalDate.now())
                .append("?key=")
                .append(apiKey)
                .append("&unitGroup=metric&include=current")
                .toString();
        log.debug("\"{}\" Request: {}", CURRENT_SERVICE, requestString);
        WebClient.ResponseSpec responseSpec = webClient.get()
                .uri(requestString)
                .retrieve();
        String responseBody = null;
        try {
            responseBody = responseSpec.bodyToMono(String.class).block();
        } catch (Exception e) {
            log.warn(WEATHER_DATA_NOT_FOUND, CURRENT_SERVICE);
        }
        if (responseBody != null) {
            log.debug("\"{}\" Response: {}", CURRENT_SERVICE, responseBody);
            try {
                DocumentContext temperatureContext = JsonPath.parse(responseBody);
                if (temperatureContext.read(TEMPERATURE_PATH) instanceof Integer) {
                    temperatureReturn = Double.valueOf((Integer) temperatureContext.read(TEMPERATURE_PATH));
                } else if (temperatureContext.read(TEMPERATURE_PATH) instanceof Double) {
                    temperatureReturn = temperatureContext.read(TEMPERATURE_PATH);
                } else {
                    log.warn(WEATHER_DATA_NOT_FOUND, CURRENT_SERVICE);
                }
                log.debug("Service: \"{}\" Temperature: \"{}\"", CURRENT_SERVICE, temperatureReturn);
            } catch (PathNotFoundException pathNotFoundException) {
                log.warn(WEATHER_DATA_NOT_FOUND, CURRENT_SERVICE);
            }
        }
        return temperatureReturn;
    }
}
