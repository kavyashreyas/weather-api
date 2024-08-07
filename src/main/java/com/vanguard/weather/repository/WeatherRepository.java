package com.vanguard.weather.repository;

import com.vanguard.weather.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Weather findByCityAndCountry(String city, String country);
}

