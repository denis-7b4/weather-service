package ru.testtask.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.testtask.models.Temperatures;
import ru.testtask.repositories.WeatherRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    Double[] temperatures = new Double[] {17.6, 19.3, 18.4, 15.8, 11.1};
    List<Temperatures> temperaturesList = new ArrayList<>();

    @MockBean
    private WeatherRepository weatherRepository;

    @Autowired
    private MockMvc mockMvc;

    WeatherControllerTest() {
        for (Double temperature : temperatures) {
            temperaturesList.add(new Temperatures("Chelyabinsk", "Russia", temperature));
        }
    }

    @Test
    void ver() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().string("1.0.0"));
    }

    @Test
    void getCurrentTemperatureTest() throws Exception {
        Mockito.when(weatherRepository
                .findByCityAndCountryAndDateCreatedOrderByTimeCreated(
                        "Chelyabinsk", "Russia", LocalDate.now()))
                        .thenReturn(Optional.of(temperaturesList));
        MvcResult response = mockMvc.perform(get("/weather?city=Chelyabinsk&country=Russia"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        Assertions.assertNotNull(response);
        JSONArray responseJsonArray = new JSONArray(response.getResponse().getContentAsString());
        Assertions.assertNotNull(responseJsonArray);
        Assertions.assertEquals(1, responseJsonArray.length());
        Assertions.assertEquals(11.1, responseJsonArray.getJSONObject(0).getDouble("temperature"));
    }

    @Test
    void getTemperaturesByDateTest() throws Exception {
        Mockito.when(weatherRepository
                        .findByCityAndCountryAndDateCreatedOrderByTimeCreated(
                                "Chelyabinsk", "Russia", LocalDate.now()))
                .thenReturn(Optional.of(temperaturesList));
        MvcResult response = mockMvc.perform(get("/weather?city=Chelyabinsk&country=Russia&date="
                        + LocalDate.now()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        Assertions.assertNotNull(response);
        JSONArray responseJsonArray = new JSONArray(response.getResponse().getContentAsString());
        for (int i = 0; i < responseJsonArray.length(); i++)
        {
            JSONObject responseJsonObj = responseJsonArray.getJSONObject(i);
            Assertions.assertEquals(temperatures[i], responseJsonObj.getDouble("temperature"));
        }
    }

    @Test
    void getTemperaturesByDateTestEmpty() throws Exception {
        mockMvc.perform(get("/weather?city=Moscow&country=Russia"))
                .andExpect(status().isNoContent());
    }
}
