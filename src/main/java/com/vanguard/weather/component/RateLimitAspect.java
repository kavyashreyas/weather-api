package com.vanguard.weather.component;

import com.vanguard.weather.exception.InvalidApiKeyException;
import com.vanguard.weather.exception.RateLimitExceededException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    private final ConcurrentHashMap<String, List<Long>> requestCounts = new ConcurrentHashMap<>();
    Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Value("${api.key.rate.limit:5}")
    private int apiKeyRateLimit;

    @Value("${api.key.rate.limit.duration.in.ms:3600000l}")
    private Long apiKeyRateLimitDurationInMs;

    @Value("#{'${allowed.api.keys}'.split(',')}")
    private List<String> allowedApiKeys;

    @Before("@annotation(com.vanguard.weather.annotations.WithRateLimitProtection)")
    public void rateLimit() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final String apiKey = requestAttributes.getRequest().getHeader("API-Key");
        logger.info("Api Key Header: {}", apiKey);
        if (!allowedApiKeys.contains(apiKey)) {
            logger.error("Invalid API Key. apiKey: {},allowedApiKeys: {}, allowedApiKeys.contains(apiKey): {}", apiKey, allowedApiKeys, allowedApiKeys.contains(apiKey));
            throw new InvalidApiKeyException("Invalid API Key.");
        }
        final long currentTime = System.currentTimeMillis();
        requestCounts.putIfAbsent(apiKey, new ArrayList<>());
        removeOlderEntryThanRateLimitDuration(currentTime);
        logger.info("APIKey: {}, requestCounts for apiKey: {}, requestCounts.get(apiKey) list: {}", apiKey, requestCounts.get(apiKey), requestCounts.get(apiKey).size());
        if (requestCounts.get(apiKey).size() >= apiKeyRateLimit) {
            logger.error("Hourly limit has been exceeded for apiKey: {}, number of requests: {}", apiKey, requestCounts.get(apiKey).size());
            throw new RateLimitExceededException("Hourly limit has been exceeded.");
        } else {
            requestCounts.get(apiKey).add(currentTime);
        }
    }

    private void removeOlderEntryThanRateLimitDuration(final long currentTime) {
        requestCounts.values().forEach(l -> {
            l.removeIf(t -> compareIfEntryIsOlderThanRateLimitDuration(currentTime, t));
        });
    }

    private boolean compareIfEntryIsOlderThanRateLimitDuration(final long currentTime, final long timeToCheck) {
        return currentTime - timeToCheck > apiKeyRateLimitDurationInMs;
    }
}
