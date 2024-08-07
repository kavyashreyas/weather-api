package com.vanguard.weather.service;

import com.vanguard.weather.model.Weather;
import com.vanguard.weather.repository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;

@SpringBootTest
public class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    public WeatherServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetWeatherDescription() {
        Weather weather = new Weather();
        weather.setCity("London");
        weather.setCountry("uk");
        weather.setDescription("Clear sky");

        when(weatherRepository.findByCityAndCountry("London", "uk")).thenReturn(weather);

        String description = weatherService.getWeatherDescription("London", "uk", "key1");
        assert (description.equals("Clear sky"));
    }
}

