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

class ClientWeatherbitTest {

    private static ClientAndServer mockServer;
    private final ClientWeatherbit clientWeatherbit = new ClientWeatherbit("apiKeyWeatherbit");
    private static final String urlWeatherbitClient = "http://localhost:1080";
    private final WebClient weatherbitClient = WebClient.create(urlWeatherbitClient);

    @Test
    void getTemperatureTest() {
        ConfigurationProperties.logLevel("WARN");
        Assertions.assertEquals(19.00
                , clientWeatherbit.getTemperature(weatherbitClient, 55.1598408, 61.4025547));
        Assertions.assertNull(clientWeatherbit
                .getTemperature(weatherbitClient, 90.0000000, 90.0000000));
    }

    @BeforeAll
    public static void setUp() {
        mockServer = startClientAndServer(1080);
        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withQueryStringParameters(
                                        Parameter.param("lat", "55.1598408"),
                                        Parameter.param("lon", "61.4025547"),
                                        Parameter.param("key", "apiKeyWeatherbit")
                                )
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody("{\"count\":1,\"data\":[{\"app_temp\":17.5,\"aqi\":21," +
                                        "\"city_name\":\"Chelyabinsk\",\"clouds\":0,\"country_code\":\"RU\"," +
                                        "\"datetime\":\"2023-05-17:09\",\"dewpt\":-4.5,\"dhi\":113.52,\"dni\":885.64," +
                                        "\"elev_angle\":52,\"ghi\":804.22,\"gust\":null,\"h_angle\":10," +
                                        "\"lat\":55.1598,\"lon\":61.4026,\"ob_time\":\"2023-05-17 09:25\"," +
                                        "\"pod\":\"d\",\"precip\":0,\"pres\":998.2,\"rh\":20,\"slp\":1025," +
                                        "\"snow\":0,\"solar_rad\":804.2,\"sources\":[\"USCC\"]," +
                                        "\"state_code\":\"13\",\"station\":\"USCC\",\"sunrise\":\"23:39\"," +
                                        "\"sunset\":\"16:00\",\"temp\":19,\"timezone\":\"Asia/Yekaterinburg\"," +
                                        "\"ts\":1684315558,\"uv\":6.974117,\"vis\":16," +
                                        "\"weather\":{\"description\":\"Clear sky\",\"code\":800,\"icon\":\"c01d\"}," +
                                        "\"wind_cdir\":\"NE\",\"wind_cdir_full\":\"northeast\"," +
                                        "\"wind_dir\":40,\"wind_spd\":6}]}\n")
                );
    }

    @AfterAll
    public static void cleanUp() {
        ConfigurationProperties.logLevel("INFO");
        mockServer.stop();
    }
}
