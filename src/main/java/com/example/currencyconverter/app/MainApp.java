package com.example.currencyconverter.app;

import com.example.currencyconverter.model.ExchangeRateService;
import com.example.currencyconverter.model.InMemoryRateService;
import com.example.currencyconverter.model.HttpRateService;
import com.example.currencyconverter.presenter.ConverterPresenter;
import com.example.currencyconverter.presenter.ConverterPresenterImpl;
import com.example.currencyconverter.view.ConverterViewFx;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application launcher. Performs simple DI wiring between View, Presenter, and Service.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Compose dependencies
        ExchangeRateService service = createServiceFromEnvOrMock();
        ConverterPresenter presenter = new ConverterPresenterImpl(service);
        ConverterViewFx view = new ConverterViewFx();

        // Wire
        presenter.attachView(view);
        view.setPresenter(presenter);

        // Start UI
        view.start(primaryStage);
    }

    private ExchangeRateService createServiceFromEnvOrMock() {
        // Allow both environment variables and system properties (for IDE run configs)
        String baseUrl = firstNonBlank(
                System.getenv("RATE_API_BASE_URL"),
                System.getProperty("rate.api.baseUrl"));
        String apiKey = firstNonBlank(
                System.getenv("RATE_API_KEY"),
                System.getProperty("rate.api.key"));
        String apiKeyName = firstNonBlank(
                System.getenv("RATE_API_KEY_NAME"),
                System.getProperty("rate.api.keyName"),
                "access_key");

        if (baseUrl != null && !baseUrl.isBlank()) {
            System.out.println("[INFO] Using HttpRateService with baseUrl=" + baseUrl);
            return new HttpRateService(baseUrl, apiKey, apiKeyName);
        }
        System.out.println("[INFO] Using InMemoryRateService (no RATE_API_BASE_URL configured)");
        return new InMemoryRateService();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
