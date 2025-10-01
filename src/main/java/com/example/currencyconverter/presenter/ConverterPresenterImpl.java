package com.example.currencyconverter.presenter;

import com.example.currencyconverter.model.Currency;
import com.example.currencyconverter.model.ExchangeRateService;
import com.example.currencyconverter.model.RateQuote;
import com.example.currencyconverter.view.ConverterView;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Presenter implementation handling validation, formatting and calling the view.
 */
public class ConverterPresenterImpl implements ConverterPresenter {

    private final ExchangeRateService rateService;
    private ConverterView view;

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(Locale.US);
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

    public ConverterPresenterImpl(ExchangeRateService rateService) {
        this.rateService = rateService;
        moneyFormat.setMaximumFractionDigits(2);
        moneyFormat.setMinimumFractionDigits(2);
    }

    @Override
    public void attachView(ConverterView view) {
        this.view = view;
    }

    @Override
    public void onConvert(String amountText, Currency from, Currency to) {
        if (view == null) return; // no-op if not attached
        view.clearError();

        // Validate
        if (amountText == null || amountText.trim().isEmpty()) {
            view.showError("Please enter an amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException e) {
            view.showError("Amount must be a valid number.");
            return;
        }
        if (amount < 0) {
            view.showError("Amount cannot be negative.");
            return;
        }
        if (from == null || to == null) {
            view.showError("Please select both currencies.");
            return;
        }

        Optional<RateQuote> quoteOpt = rateService.getRate(from, to);
        if (quoteOpt.isEmpty()) {
            view.showError("This currency pair is not supported yet.");
            return;
        }
        RateQuote quote = quoteOpt.get();
        double converted = amount * quote.getRate();
        String result = numberFormat.format(amount) + " " + from + " = " + moneyFormat.format(converted) + " " + to;
        String lastUpdated = "Last updated: " + dtf.format(quote.getTimestamp());
        view.showResult(result);
        view.showLastUpdated(lastUpdated);
    }
}
