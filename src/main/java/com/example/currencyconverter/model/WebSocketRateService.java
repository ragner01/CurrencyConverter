package com.example.currencyconverter.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketRateService implements ExchangeRateService {

    private final Map<Currency, Double> usdTo = new HashMap<>();
    private volatile Instant lastRefresh;
    private final ScheduledExecutorService scheduler;

    public WebSocketRateService() {
        initMockRates();
        lastRefresh = Instant.now();
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ws-rate-refresh");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> lastRefresh = Instant.now(), 2, 2, TimeUnit.SECONDS);
    }

    private void initMockRates() {
        usdTo.clear();
        usdTo.put(Currency.USD, 1.0);
        usdTo.put(Currency.EUR, 0.92);
        usdTo.put(Currency.GBP, 0.80);
        usdTo.put(Currency.NGN, 1500.0);
        usdTo.put(Currency.JPY, 150.0);
        usdTo.put(Currency.CAD, 1.36);
        usdTo.put(Currency.AUD, 1.53);
        usdTo.put(Currency.CNY, 7.20);
        usdTo.put(Currency.ZAR, 18.50);
        usdTo.put(Currency.INR, 83.20);
        usdTo.put(Currency.GHS, 15.30);
    }

    @Override
    public Optional<RateQuote> getRate(Currency base, Currency quote) {
        if (base == null || quote == null) return Optional.empty();
        if (base == quote) {
            return Optional.of(new RateQuote(base, quote, 1.0, Instant.now()));
        }
        Double usdToBase = usdTo.get(base);
        Double usdToQuote = usdTo.get(quote);
        if (usdToBase == null || usdToQuote == null) return Optional.empty();
        double rate = usdToQuote / usdToBase;
        return Optional.of(new RateQuote(base, quote, rate, lastRefresh));
    }


    public void shutdown() {
        scheduler.shutdownNow();
    }
}
