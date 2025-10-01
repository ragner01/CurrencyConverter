package com.example.currencyconverter.model;

import java.time.Instant;
import java.util.Objects;

public final class RateQuote {
    private final Currency base;
    private final Currency quote;
    private final double rate;
    private final Instant timestamp;

    public RateQuote(Currency base, Currency quote, double rate, Instant timestamp) {
        if (base == null || quote == null || timestamp == null) {
            throw new IllegalArgumentException("base, quote, and timestamp must be non-null");
        }
        if (rate <= 0) {
            throw new IllegalArgumentException("rate must be positive");
        }
        this.base = base;
        this.quote = quote;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public Currency getBase() { return base; }
    public Currency getQuote() { return quote; }
    public double getRate() { return rate; }
    public Instant getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateQuote)) return false;
        RateQuote rateQuote = (RateQuote) o;
        return Double.compare(rateQuote.rate, rate) == 0 && base == rateQuote.base && quote == rateQuote.quote && Objects.equals(timestamp, rateQuote.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, quote, rate, timestamp);
    }

    @Override
    public String toString() {
        return "RateQuote{" +
                "base=" + base +
                ", quote=" + quote +
                ", rate=" + rate +
                ", timestamp=" + timestamp +
                '}';
    }
}
