package com.example.currencyconverter.view;

import com.example.currencyconverter.presenter.ConverterPresenter;

/**
 * View contract in MVP. Implementations render the UI with no business logic.
 * The Presenter calls these methods to update the UI.
 */
public interface ConverterView {
    /** Attach a presenter so the view can forward user actions. */
    void setPresenter(ConverterPresenter presenter);

    /** Clear any previous error display. */
    void clearError();

    /** Show a human-readable error message. */
    void showError(String message);

    /** Show formatted conversion result. */
    void showResult(String resultText);

    /** Show formatted last-updated string for rates. */
    void showLastUpdated(String lastUpdatedText);
}
