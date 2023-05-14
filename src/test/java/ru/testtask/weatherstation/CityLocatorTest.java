package ru.testtask.weatherstation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import ru.testtask.models.Cities;
import ru.testtask.repositories.CityRepository;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

class CityLocatorTest {
    private static ClientAndServer mockServer;
    Cities cityRawFromTable1 = new Cities("Chelyabinsk", "Russia", 55.1598408, 61.4025547
            ,"Chelyabinsk, Chelyabinsk Oblast, Ural Federal District, Russia");
    Cities cityRawFromTable2 = new Cities("Austin", "US",30.2711286, -97.7436995
            ,"Austin, Travis County, Texas, United States");
    private static List<String[]> weatherCitiesFileList = new ArrayList<>();
    private List<Cities> requestCitiesList;


    @Test
    void getCities() throws FileNotFoundException {
        CitiesListReader citiesListReaderMock = Mockito.mock(CitiesListReader.class);
        CityRepository cityRepositoryMock = Mockito.mock(CityRepository.class);
        when(citiesListReaderMock.getCitiesList()).thenReturn(weatherCitiesFileList);
        when(cityRepositoryMock.findByCityAndCountry("Chelyabinsk", "Russia")).thenReturn(cityRawFromTable1);
        when(cityRepositoryMock.findByCityAndCountry("Austin", "US")).thenReturn(cityRawFromTable2);
        when(cityRepositoryMock.save(Mockito.any(Cities.class))).thenReturn(null);
        try {
            CityLocator cityLocator = new CityLocator(cityRepositoryMock, citiesListReaderMock);
            requestCitiesList = cityLocator.getCities();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
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
        weatherCitiesFileList.add(new String[] {"Chelyabinsk", "Russia"});
        weatherCitiesFileList.add(new String[]{"Austin", "US"});
        weatherCitiesFileList.add(new String[]{"Moscow", "Russia"});
        weatherCitiesFileList.add(new String[]{"Hrenov", "Hrenia"});
    }

}
