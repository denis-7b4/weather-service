package ru.testtask.weatherstation.requestors;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;
import ru.testtask.weatherstation.interfaces.TemperatureRequest;

@Slf4j
public class ClientTomorrow implements TemperatureRequest {

    private final String apiKey;
    static final String CURRENT_SERVICE = "Tomorrow.io";

    public ClientTomorrow(String apiKeyTomorrow) {
        this.apiKey = apiKeyTomorrow;
    }

    @Override
    public Double getTemperature(@NotNull WebClient webClient, Double latitude, Double longitude) {
        Double temperatureReturn = null;
        String requestString = new StringBuilder()
                .append("?location=")
                .append(latitude)
                .append(",")
                .append(longitude)
                .append("&fields=temperature&timesteps=current&units=metric&format=json")
                .append("&apikey=")
                .append(apiKey)
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
                JSONArray temperature = JsonPath.parse(responseBody)
                        .read("$['data']['timelines'][*]['intervals'][*]['values']['temperature']");
                if (!temperature.isEmpty() && temperature.get(0) instanceof Integer) {
                    temperatureReturn = Double.valueOf((Integer) temperature.get(0));
                } else if (!temperature.isEmpty() && temperature.get(0) instanceof Double) {
                    temperatureReturn = (Double) temperature.get(0);
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
