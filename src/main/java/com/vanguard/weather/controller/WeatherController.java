package com.vanguard.weather.controller;

import com.vanguard.weather.annotations.WithRateLimitProtection;
import com.vanguard.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Operation(summary = "Get weather description", description = "Fetch weather description for a given city and country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of weather description"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input or missing API key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @GetMapping
    @WithRateLimitProtection
    public String getWeather(
            @RequestParam @Parameter(description = "City name") String city,
            @RequestParam @Parameter(description = "Country code") String country,
            @RequestHeader("API-Key") @Parameter(description = "API key for authentication") String apiKey) {
        return weatherService.getWeatherDescription(city, country, apiKey);
    }
}
