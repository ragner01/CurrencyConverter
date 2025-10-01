package com.example.currencyconverter.presenter;

import com.example.currencyconverter.model.Currency;
import com.example.currencyconverter.view.ConverterView;

/**
 * Presenter contract for converting currency amounts.
 *
 * <p>Presenter contains all validation and formatting logic. It calls methods on the
 * {@link ConverterView} to render results and errors. No UI types are used here to
 * keep it unit-testable.</p>
 */
public interface ConverterPresenter {

    /** Attach the view to this presenter. */
    void attachView(ConverterView view);

    /**
     * Handle a conversion request.
     *
     * @param amountText user-entered amount (raw string)
     * @param from base currency
     * @param to quote currency
     */
    void onConvert(String amountText, Currency from, Currency to);
}
