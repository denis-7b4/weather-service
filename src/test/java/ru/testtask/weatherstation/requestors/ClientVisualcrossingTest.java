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

import java.time.LocalDate;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

class ClientVisualcrossingTest {

    private static ClientAndServer mockServer;
    private final ClientVisualcrossing clientVisualcrossing =
            new ClientVisualcrossing("apiKeyVisualcrossing");
    private static final String urlVisualcrossingClient = "http://localhost:1080";
    private final WebClient visualcrossingClient = WebClient.create(urlVisualcrossingClient);
    private static final String currentDate = String.valueOf(LocalDate.now());

    @Test
    void getTemperatureTest() {
        ConfigurationProperties.logLevel("WARN");
        Assertions.assertEquals(19.00
                , clientVisualcrossing.getTemperature(visualcrossingClient, 55.1598408, 61.4025547));
        Assertions.assertNull(clientVisualcrossing
                .getTemperature(visualcrossingClient, 90.0000000, 90.0000000));
    }

    @BeforeAll
    public static void setUp() {
        mockServer = startClientAndServer(1080);
        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/55.1598408,61.4025547/" + currentDate)
                                .withQueryStringParameters(
                                        Parameter.param("unitGroup", "metric"),
                                        Parameter.param("key", "apiKeyVisualcrossing"),
                                        Parameter.param("include", "current")
                                )
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody("{\"queryCost\":1,\"latitude\":55.1598408,\"longitude\":61.4025547," +
                                        "\"resolvedAddress\":\"55.1598408,61.4025547\"," +
                                        "\"address\":\"55.1598408,61.4025547\",\"timezone\":\"Asia/Yekaterinburg\"," +
                                        "\"tzoffset\":5.0,\"days\":[{\"datetime\":\"2023-05-17\"," +
                                        "\"datetimeEpoch\":1684263600,\"tempmax\":19.0,\"tempmin\":9.0," +
                                        "\"temp\":14.1,\"feelslikemax\":19.0,\"feelslikemin\":8.0," +
                                        "\"feelslike\":13.9,\"dew\":-0.1,\"humidity\":40.3,\"precip\":0.0," +
                                        "\"precipprob\":0.0,\"precipcover\":0.0,\"preciptype\":null," +
                                        "\"snow\":0.0,\"snowdepth\":0.0,\"windgust\":39.6,\"windspeed\":23.0," +
                                        "\"winddir\":61.9,\"pressure\":1024.4,\"cloudcover\":8.9," +
                                        "\"visibility\":14.9,\"solarradiation\":328.5,\"solarenergy\":28.3," +
                                        "\"uvindex\":9.0,\"severerisk\":10.0,\"sunrise\":\"04:43:24\"," +
                                        "\"sunriseEpoch\":1684280604,\"sunset\":\"20:59:25\"," +
                                        "\"sunsetEpoch\":1684339165,\"moonphase\":0.91,\"conditions\":\"Clear\"," +
                                        "\"description\":\"Clear conditions throughout the day.\"," +
                                        "\"icon\":\"clear-day\",\"stations\":[\"USCC\"],\"source\":\"comb\"}]," +
                                        "\"stations\":{\"USCC\":{\"distance\":17575.0,\"latitude\":55.3," +
                                        "\"longitude\":61.53,\"useCount\":0,\"id\":\"USCC\",\"name\":\"USCC\"," +
                                        "\"quality\":50,\"contribution\":0.0}},\"currentConditions\":{\"datetime\":" +
                                        "\"15:00:00\",\"datetimeEpoch\":1684317600,\"temp\":19.0," +
                                        "\"feelslike\":19.0,\"humidity\":20.7,\"dew\":-4.0,\"precip\":0.0," +
                                        "\"precipprob\":0.0,\"snow\":0.0,\"snowdepth\":0.0,\"preciptype\":null," +
                                        "\"windgust\":null,\"windspeed\":21.6,\"winddir\":60.0,\"pressure\":1024.0," +
                                        "\"visibility\":10.0,\"cloudcover\":4.0,\"solarradiation\":798.0," +
                                        "\"solarenergy\":2.9,\"uvindex\":8.0,\"conditions\":\"Clear\"," +
                                        "\"icon\":\"clear-day\",\"stations\":[\"USCC\"],\"source\":\"obs\"," +
                                        "\"sunrise\":\"04:43:24\",\"sunriseEpoch\":1684280604,\"sunset\":\"20:59:25\"," +
                                        "\"sunsetEpoch\":1684339165,\"moonphase\":0.91}}")
                );
    }

    @AfterAll
    public static void cleanUp() {
        ConfigurationProperties.logLevel("INFO");
        mockServer.stop();
    }
}
