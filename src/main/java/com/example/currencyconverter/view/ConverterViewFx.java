package com.example.currencyconverter.view;

import com.example.currencyconverter.model.Currency;
import com.example.currencyconverter.presenter.ConverterPresenter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * JavaFX implementation of ConverterView. Only contains rendering and event wiring.
 */
public class ConverterViewFx implements ConverterView {

    private ConverterPresenter presenter;

    private final ComboBox<Currency> fromCombo = new ComboBox<>();
    private final ComboBox<Currency> toCombo = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Button convertBtn = new Button("Convert");
    private final Button swapBtn = new Button("⇄ Swap");
    private final CheckBox liveUpdatesToggle = new CheckBox("Live updates");
    private final Label resultLabel = new Label();
    private final Label lastUpdatedLabel = new Label();
    private final Label errorLabel = new Label();

    private ScheduledExecutorService liveScheduler;

    public void start(Stage stage) {
        stage.setTitle("Currency Converter (MVP)");

        fromCombo.getItems().setAll(Currency.values());
        toCombo.getItems().setAll(Currency.values());
        fromCombo.getSelectionModel().select(Currency.USD);
        toCombo.getSelectionModel().select(Currency.EUR);

        errorLabel.getStyleClass().add("error-label");
        resultLabel.getStyleClass().add("result-label");

        amountField.setPromptText("Enter amount e.g. 100");
        convertBtn.setDefaultButton(true);

        convertBtn.setOnAction(e -> doConvert());
        amountField.setOnAction(e -> doConvert());

        swapBtn.getStyleClass().add("swap-button");
        swapBtn.setOnAction(e -> {
            Currency f = fromCombo.getValue();
            fromCombo.setValue(toCombo.getValue());
            toCombo.setValue(f);
        });

        liveUpdatesToggle.setSelected(false);
        liveUpdatesToggle.setOnAction(e -> toggleLiveUpdates(liveUpdatesToggle.isSelected()));

        GridPane grid = new GridPane();
        grid.getStyleClass().add("card");
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.add(new Label("From"), 0, 0);
        grid.add(fromCombo, 1, 0);
        grid.add(new Label("To"), 0, 1);
        HBox toRow = new HBox(8, toCombo, swapBtn);
        grid.add(toRow, 1, 1);
        grid.add(new Label("Amount"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new HBox(8, convertBtn, liveUpdatesToggle), 1, 3);

        Label title = new Label("Currency Converter");
        title.getStyleClass().add("title");
        VBox root = new VBox(16, title, grid, resultLabel, lastUpdatedLabel, errorLabel);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(24));
        Scene scene = new Scene(root, 520, 340);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> stopLiveUpdates());
        stage.show();
    }

    private void doConvert() {
        if (presenter != null) {
            presenter.onConvert(amountField.getText(), fromCombo.getValue(), toCombo.getValue());
        }
    }

    private void toggleLiveUpdates(boolean enable) {
        if (enable) {
            startLiveUpdates();
        } else {
            stopLiveUpdates();
        }
    }

    private void startLiveUpdates() {
        if (liveScheduler != null && !liveScheduler.isShutdown()) return;
        liveScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "live-convert");
            t.setDaemon(true);
            return t;
        });
        // Refresh every 5 seconds
        liveScheduler.scheduleAtFixedRate(() -> Platform.runLater(this::doConvert), 0, 5, TimeUnit.SECONDS);
    }

    private void stopLiveUpdates() {
        if (liveScheduler != null) {
            liveScheduler.shutdownNow();
            liveScheduler = null;
        }
    }

    @Override
    public void setPresenter(ConverterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearError() {
        errorLabel.setText("");
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message == null ? "" : message);
    }

    @Override
    public void showResult(String resultText) {
        resultLabel.setText(resultText == null ? "" : resultText);
    }

    @Override
    public void showLastUpdated(String lastUpdatedText) {
        lastUpdatedLabel.setText(lastUpdatedText == null ? "" : lastUpdatedText);
    }
}
