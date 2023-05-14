package ru.testtask.weatherstation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.springframework.http.HttpStatus;
import ru.testtask.models.Cities;
import ru.testtask.repositories.CityRepository;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

class CityLocatorTest {
    private static ClientAndServer mockServer;
    private final String urlCityLocator = "http://localhost:1080";
    Cities cityRawFromTable1 = new Cities("Chelyabinsk", "Russia", 55.1598408, 61.4025547
            ,"Chelyabinsk, Chelyabinsk Oblast, Ural Federal District, Russia");
    private static List<String[]> weatherCitiesFileList = new ArrayList<>();
    private List<Cities> requestCitiesList;


    @Test
    void getCities() throws FileNotFoundException {
        CitiesListReader citiesListReaderMock = Mockito.mock(CitiesListReader.class);
        CityRepository cityRepositoryMock = Mockito.mock(CityRepository.class);
        when(citiesListReaderMock.getCitiesList()).thenReturn(weatherCitiesFileList);
        when(cityRepositoryMock.findByCityAndCountry("Chelyabinsk", "Russia")).thenReturn(cityRawFromTable1);
        when(cityRepositoryMock.save(Mockito.any(Cities.class))).thenReturn(null);
        mockServer.when(
                        request()
                                .withMethod("GET")
                                .withPath("/search")
                                .withQueryStringParameters(
                                        Parameter.param("country", "Russia"),
                                        Parameter.param("city", "Moscow")
                                )
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody("[{\"lat\":\"55.7504461\",\"lon\":\"37.6174943\"," +
                                        "\"display_name\":\"Moscow, Central Federal District, Russia\"," +
                                        "\"class\":\"place\",\"type\":\"city\"}]")
                );
        ConfigurationProperties.logLevel("WARN");
        try {
            CityLocator cityLocator = new CityLocator(cityRepositoryMock, urlCityLocator, citiesListReaderMock);
            requestCitiesList = cityLocator.getCities();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        ConfigurationProperties.logLevel("INFO");
        requestCitiesList.forEach(cities -> {
            System.out.println("City: " + cities.getCity());
            System.out.println("Country: " + cities.getCountry());
        });
        Assertions.assertEquals(2, requestCitiesList.size());
        Assertions.assertEquals("Chelyabinsk", requestCitiesList.get(0).getCity());
        Assertions.assertEquals("Russia", requestCitiesList.get(0).getCountry());
        Assertions.assertEquals("Moscow", requestCitiesList.get(1).getCity());
        Assertions.assertEquals("Russia", requestCitiesList.get(1).getCountry());

    }

    @BeforeAll
    public static void startServer() {
        System.out.println("BeforeAll called");
        mockServer = startClientAndServer(1080);

        createCities();
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }

    static void createCities() {
        // Test city from "repository"
        weatherCitiesFileList.add(new String[] {"Chelyabinsk", "Russia"});
        // Test city from "remote REST service"
        weatherCitiesFileList.add(new String[]{"Moscow", "Russia"});
        // Test non-existent city
        weatherCitiesFileList.add(new String[]{"Fakecity", "Fakecountry"});
    }

}
