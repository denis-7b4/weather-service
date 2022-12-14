package ru.testtask.weatherstation;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.testtask.models.Cities;
import ru.testtask.repositories.CityRepository;

import java.io.FileNotFoundException;
import java.util.*;

@Slf4j
@Service
public final class CityLocator {

    @Autowired
    private final CityRepository cityRepository;
    private final List<Cities> requestCitiesList = new ArrayList<>();

    public CityLocator(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<Cities> getCities() throws FileNotFoundException {
        log.info("City locator started");

        WebClient client = WebClient.create("https://nominatim.openstreetmap.org/");
        PropertiesReader propertiesReader = new PropertiesReader();
        List<String[]> cities = propertiesReader.getProperties();
        for (String[] city : cities) {
            String cityName = city[0];
            String countryName = city[1];
            Cities cityRawFromTable = cityRepository.findByCityAndCountry(cityName, countryName);
            if (cityRawFromTable != null) {
                requestCitiesList.add(cityRawFromTable);
            } else {
                String searchString = new StringBuilder()
                        .append("/search?limit=1&format=json&accept-language=en-US")
                        .append("&city=")
                        .append(cityName)
                        .append("&country=")
                        .append(countryName)
                        .toString();
                WebClient.ResponseSpec responseSpec = client.get()
                        .uri(searchString)
                        .retrieve();
                String responseBody = null;
                try {
                    responseBody = responseSpec.bodyToMono(String.class).block();
                } catch (Exception e) {
                    log.warn("City: \"{}\" not found in Country: \"{}\".", cityName, countryName);
                    continue;
                }
                if (responseBody != null) {
                    JSONObject responseObject;
                    try {
                        responseObject = new JSONObject(responseBody.substring(1, responseBody.length() - 1));
                    } catch (JSONException jsonException) {
                        log.warn("City: \"{}\" not found in Country: \"{}\".", cityName, countryName);
                        continue;
                    }
                    Cities cityRawFromJson = new Cities();
                    cityRawFromJson.setCity(cityName);
                    cityRawFromJson.setCountry(countryName);
                    cityRawFromJson.setLat(responseObject.getDouble("lat"));
                    cityRawFromJson.setLon(responseObject.getDouble("lon"));
                    cityRawFromJson.setDisplayName(responseObject.getString("display_name"));
                    requestCitiesList.add(cityRawFromJson);
                    cityRepository.save(cityRawFromJson);
                }
            }
        }
        log.info("City locator completed");
        return requestCitiesList;
    }
}
