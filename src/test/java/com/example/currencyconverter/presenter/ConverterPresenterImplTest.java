package com.example.currencyconverter.presenter;

import com.example.currencyconverter.model.Currency;
import com.example.currencyconverter.model.ExchangeRateService;
import com.example.currencyconverter.model.RateQuote;
import com.example.currencyconverter.view.ConverterView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Plain-Java self-contained tests for ConverterPresenterImpl.
 * This avoids external dependencies (JUnit/Mockito) to compile in restricted environments.
 * Run by executing the main method; throws AssertionError on failures.
 */
public class ConverterPresenterImplTest {

    public static void main(String[] args) {
        ConverterPresenterImplTest t = new ConverterPresenterImplTest();
        t.nullAmountShowsError();
        t.emptyAmountShowsError();
        t.invalidNumberShowsError();
        t.negativeAmountShowsError();
        t.missingCurrenciesShowError();
        t.unsupportedPairShowsError();
        t.happyPathShowsResultAndLastUpdated();
        System.out.println("[DEBUG_LOG] All presenter tests passed.");
    }

    private static class FakeView implements ConverterView {
        final List<String> calls = new ArrayList<>();
        @Override public void setPresenter(ConverterPresenter presenter) { /* no-op for tests */ }
        @Override public void showResult(String resultText) { calls.add("showResult:" + resultText); }
        @Override public void showLastUpdated(String timestampText) { calls.add("showLastUpdated:" + timestampText); }
        @Override public void showError(String message) { calls.add("showError:" + message); }
        @Override public void clearError() { calls.add("clearError"); }
    }

    private static class FakeRateService implements ExchangeRateService {
        Optional<RateQuote> next;
        @Override public Optional<RateQuote> getRate(Currency base, Currency quote) { return next; }
    }

    private FakeRateService rateService;
    private FakeView view;
    private ConverterPresenterImpl presenter;

    private void setUp() {
        rateService = new FakeRateService();
        view = new FakeView();
        presenter = new ConverterPresenterImpl(rateService);
        presenter.attachView(view);
    }

    private void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public void nullAmountShowsError() {
        setUp();
        presenter.onConvert(null, Currency.USD, Currency.EUR);
        assertTrue(view.calls.size() >= 2, "Expected at least two view calls");
        assertTrue("clearError".equals(view.calls.get(0)), "First call should clearError");
        assertTrue(view.calls.stream().anyMatch(s -> s.equals("showError:Please enter an amount.")), "Should show missing amount error");
    }

    public void emptyAmountShowsError() {
        setUp();
        presenter.onConvert("   ", Currency.USD, Currency.EUR);
        assertTrue(view.calls.get(0).equals("clearError"), "Should clear error first");
        assertTrue(view.calls.stream().anyMatch(s -> s.equals("showError:Please enter an amount.")), "Should show missing amount error");
    }

    public void invalidNumberShowsError() {
        setUp();
        presenter.onConvert("abc", Currency.USD, Currency.EUR);
        assertTrue(view.calls.get(0).equals("clearError"), "Should clear error first");
        assertTrue(view.calls.stream().anyMatch(s -> s.equals("showError:Amount must be a valid number.")), "Should show invalid number error");
    }

    public void negativeAmountShowsError() {
        setUp();
        presenter.onConvert("-5", Currency.USD, Currency.EUR);
        assertTrue(view.calls.get(0).equals("clearError"), "Should clear error first");
        assertTrue(view.calls.stream().anyMatch(s -> s.equals("showError:Amount cannot be negative.")), "Should show negative amount error");
    }

    public void missingCurrenciesShowError() {
        setUp();
        presenter.onConvert("10", null, Currency.EUR);
        assertTrue(view.calls.get(0).equals("clearError"), "Should clear error first");
        assertTrue(view.calls.stream().anyMatch(s -> s.equals("showError:Please select both currencies.")), "Should show missing currencies error");
    }

    public void unsupportedPairShowsError() {
        setUp();
        rateService.next = Optional.empty();
        presenter.onConvert("10", Currency.USD, Currency.EUR);
        assertTrue(view.calls.get(0).equals("clearError"), "Should clear error first");
        assertTrue(view.calls.stream().anyMatch(s -> s.equals("showError:This currency pair is not supported yet.")), "Should show unsupported pair error");
    }

    public void happyPathShowsResultAndLastUpdated() {
        setUp();
        Instant now = Instant.parse("2024-01-01T10:15:30Z");
        rateService.next = Optional.of(new RateQuote(Currency.USD, Currency.EUR, 0.90, now));
        presenter.onConvert("100", Currency.USD, Currency.EUR);
        assertTrue(view.calls.get(0).equals("clearError"), "Should clear error first");
        assertTrue(view.calls.stream().anyMatch(s -> s.startsWith("showResult:")), "Should show result");
        assertTrue(view.calls.stream().anyMatch(s -> s.toLowerCase().startsWith("showlastupdated:Last updated:".toLowerCase())), "Should show last updated");
    }
}
