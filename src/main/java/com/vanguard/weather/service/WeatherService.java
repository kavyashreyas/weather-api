package com.vanguard.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanguard.weather.exception.InvalidInputException;
import com.vanguard.weather.model.Weather;
import com.vanguard.weather.repository.WeatherRepository;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    Logger logger = LoggerFactory.getLogger(WeatherService.class);


    @Autowired
    private WeatherRepository weatherRepository;

    @Value("${open-weather-map.api.url}")
    private String weatherApiUrl;

    public String getWeatherDescription(String city, String country, String apiKey) {
        return getWeather(city, country, apiKey).getDescription();
    }

    private Weather getWeather(String city, String country, String apiKey) {
        try {
            logger.info("Retrieving weather from DB");
            Weather weather = weatherRepository.findByCityAndCountry(city, country);
            logger.info("Weather from DB: " + weather);
            if (null == weather) {
                weather = getWeatherFromAPI(city, country, apiKey);
            }
            return weather;
        } catch (HttpClientErrorException ex) {
            logger.error("HttpClientErrorException occurred: {} {}", ex.getStatusCode(), ex.getStatusText());
            throw new HttpClientErrorException(ex.getStatusCode(), ex.getStatusText());
        } catch (HttpServerErrorException ex) {
            logger.error("httpServerErrorException: {} ", ex.getMessage());
            throw new HttpClientErrorException(ex.getStatusCode(), ex.getStatusText());
        } catch (UnknownHttpStatusCodeException ex) {
            logger.error("unknownHttpStatusCodeException: {} ", ex.getMessage());
            throw ex;
        } catch (Exception exception) {
            logger.error("exception: {} ", exception.getMessage());
            throw exception;
        }
    }

    private Weather getWeatherFromAPI(String city, String country, String apiKey) {
        logger.info("Fetching Weather from API");
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(weatherApiUrl + "?q=%s,%s&appid=%s", city, country, apiKey);
        String response = restTemplate.getForObject(url, String.class);
        logger.info("Response from API: {}", response);
        return extractWeatherAndSaveInTheDB(city, country, response);
    }

    private Weather extractWeatherAndSaveInTheDB(String city, String country, String response) {
        Weather weather = new Weather();
        weather.setCity(city);
        weather.setCountry(country);
        weather.setDescription(parseDescription(response));
        weatherRepository.save(weather);
        return weather;
    }

    private String parseDescription(String response) {
        String description = "";
        if (StringUtils.isNotEmpty(response)
                && response.contains("weather")
                && response.contains("description")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> map = mapper.readValue(response, new TypeReference<>() {
                });
                ArrayList<Object> weatherArray = (ArrayList) map.get("weather");
                if (!weatherArray.isEmpty()) {
                    Object weatherObject = weatherArray.get(0);
                    HashMap<String, String> weatherMap = mapper.convertValue(weatherObject, HashMap.class);
                    description = weatherMap.get("description");
                    logger.info("description: {}", description);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new InvalidInputException("Invalid Input");
        }
        return description;
    }
}
