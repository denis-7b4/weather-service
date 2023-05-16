package ru.testtask.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.testtask.models.Cities;

//@Disabled("Disabled during work on other tests")
@DataJpaTest
class CityRepositoryTest {
    @Autowired
    private CityRepository cityRepository;
    Cities cityRowFromTable = new Cities();
    private static final Cities cityDataFromRemoteService1 = new Cities("Chelyabinsk", "Russia"
            , 55.1598408, 61.4025547,"Chelyabinsk, Chelyabinsk Oblast" +
            ", Ural Federal District, Russia");
    private static final Cities cityDataFromRemoteService2 = new Cities("Fakecity", "Fakecountry"
            , 90.0000000, 90.0000000,"Fakecity, Some Oblast" +
            ", Ural Federal District, Russia");

    @Test
    void findByCityAndCountryTest() {
        cityRowFromTable = cityRepository.findByCityAndCountry("Chelyabinsk", "Russia");
        Assertions.assertNull(cityRowFromTable);
        setUpCityData();
        cityRowFromTable = cityRepository.findByCityAndCountry("Chelyabinsk", "Russia");
        Assertions.assertNotNull(cityRowFromTable);
        Assertions.assertEquals("Chelyabinsk", cityRowFromTable.getCity());
        Assertions.assertEquals("Russia", cityRowFromTable.getCountry());
        Assertions.assertEquals(55.1598408, cityRowFromTable.getLat());
        Assertions.assertEquals(61.4025547, cityRowFromTable.getLon());
    }

    void setUpCityData() {
        cityRepository.save(cityDataFromRemoteService1);
        cityRepository.save(cityDataFromRemoteService2);
    }
}
