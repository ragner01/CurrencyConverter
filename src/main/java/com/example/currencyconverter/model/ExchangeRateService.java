package com.example.currencyconverter.model;

import java.util.Optional;

public interface ExchangeRateService {

    Optional<RateQuote> getRate(Currency base, Currency quote);
}
