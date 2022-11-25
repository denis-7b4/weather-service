package ru.testtask.weatherstation;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public final class PropertiesReader {
    private final List<String[]> cities = new ArrayList<>();

    public List<String[]> getProperties() throws FileNotFoundException {
        String rootPath = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("")).getPath();
        String citiesListPath = rootPath + "weather.cities";
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(citiesListPath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            log.error("File with list of cities not found");
            throw new FileNotFoundException("Cities not found");
        }
        for (Map.Entry<Object, Object> item : properties.entrySet()) {
            String[] city = item.getValue().toString().split("\\.", 2);
            cities.add(city);
        }
        return cities;
    }
}
