package ru.testtask.weatherstation;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CitiesListReaderTest {

    private static final List<String[]> expectedCities = new ArrayList<>();
    static String citiesListPath;

    static void createCities() {
        try(FileWriter writer = new FileWriter(citiesListPath, false)) {
            writer.write("#weather.point01=Zlatoust.Russia\n");
            for (int i = 0, j=2; i < expectedCities.size(); i++, j++) {
                String[] city = expectedCities.get(i);
                String lineToWrite = "weather.point0" + j + "=" + city[0] + "." + city[1] + '\n';
                writer.append(lineToWrite);
            }
            System.out.println("weather.cities created");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void deleteCities() {
        boolean deleted = new File(citiesListPath).delete();
        if (deleted) {
            System.out.println("weather.cities deleted");
        }
    }

    @BeforeAll
    static void init() {
        System.out.println("BeforeAll called");
        String rootPath = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("")).getPath();
        citiesListPath = rootPath + "weather.cities";
        expectedCities.add(new String[] {"Chelyabinsk", "Russia"});
        expectedCities.add(new String[] {"Austin", "US"});
        expectedCities.add(new String[] {"Nizhny Novgorod", "Russia"});
        expectedCities.add(new String[] {"Saint-Petersburg", "Russia"});
        createCities();
    }

    @Test
    void testGetCitiesList() {
        System.out.println("getCitiesList called");
        CitiesListReader citiesListReader = new CitiesListReader();
        try {
            List<String[]> actualCities = citiesListReader.getCitiesList();
            for (int i = 0; i < actualCities.size(); i++) {
                String[] actualCity = actualCities.get(i);
                String[] expectedCity = expectedCities.get(i);
                assertEquals(expectedCity[0], actualCity[0]);
                assertEquals(expectedCity[1], actualCity[1]);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testFileNotFoundException() {
        System.out.println("testFileNotFound called");
        deleteCities();
        Exception exceptionCitiesFileNotFound = Assertions.assertThrows(FileNotFoundException.class, () -> {
            CitiesListReader citiesListReader = new CitiesListReader();
            List<String[]> actualCities = citiesListReader.getCitiesList();
        }
        , "FileNotFoundException expected");
        Assertions.assertEquals("Cities not found",
                exceptionCitiesFileNotFound.getMessage());
    }
}
