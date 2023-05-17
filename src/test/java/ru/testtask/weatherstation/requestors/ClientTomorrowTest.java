package ru.testtask.weatherstation.requestors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

class ClientTomorrowTest {

    private static ClientAndServer mockServer;
    private final ClientTomorrow clientTomorrow = new ClientTomorrow("apiKeyTomorrow");
    private static final String urlTomorrowClient = "http://localhost:1080";
    private final WebClient tomorrowClient = WebClient.create(urlTomorrowClient);

    @Test
    void getTemperatureTest() {
        ConfigurationProperties.logLevel("WARN");
        Assertions.assertEquals(17.63
                , clientTomorrow.getTemperature(tomorrowClient, 55.1598408, 61.4025547));
        Assertions.assertNull(clientTomorrow.getTemperature(tomorrowClient, 90.0000000, 90.0000000));
    }

    @BeforeAll
    public static void setUp() {
        mockServer = startClientAndServer(1080);
        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withQueryStringParameters(
                                        Parameter.param("location", "55.1598408,61.4025547"),
                                        Parameter.param("fields", "temperature"),
                                        Parameter.param("timesteps", "current"),
                                        Parameter.param("units", "metric"),
                                        Parameter.param("format", "json"),
                                        Parameter.param("apikey", "apiKeyTomorrow")
                                )
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody("{\"data\":{\"timelines\":[{\"timestep\":\"current\"," +
                                        "\"endTime\":\"2023-05-17T09:26:00Z\",\"startTime\":\"2023-05-17T09:26:00Z\"," +
                                        "\"intervals\":[{\"startTime\":\"2023-05-17T09:26:00Z\"," +
                                        "\"values\":{\"temperature\":17.63}}]}]}}")
                );
    }

    @AfterAll
    public static void cleanUp() {
        ConfigurationProperties.logLevel("INFO");
        mockServer.stop();
    }
}
