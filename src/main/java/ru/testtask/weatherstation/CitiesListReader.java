package ru.testtask.weatherstation;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public final class CitiesListReader {
    private final List<String[]> cities = new ArrayList<>();
    private final NavigableMap<String, String> citiesSorted = new TreeMap<>();

    public List<String[]> getCitiesList() throws FileNotFoundException {
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
        properties.forEach((key, value) -> citiesSorted.put(key.toString(), value.toString()));
        for (Map.Entry<String, String> item : citiesSorted.entrySet()) {
            String[] city = item.getValue().split("\\.", 2);
            cities.add(city);
        }
        return cities;
    }
}
