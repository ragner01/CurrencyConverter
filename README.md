# Currency Converter MVP

A Java 17 Maven project demonstrating MVP (Model–View–Presenter) architecture with a JavaFX UI and a mockable exchange rate service.

Features (MVP):
- Convert an amount between many currencies (USD, EUR, GBP, NGN, JPY, CAD, AUD, CNY, ZAR, INR, GHS).
- Exchange rates come from an in-memory mock service for now, with a stub HTTP service ready for future use.
- Shows last-updated timestamp for rates.
- Validates inputs and displays human-readable errors in the View.
- Redesigned JavaFX page with a cleaner layout, a swap button, and improved styling.
- Optional Live Updates toggle that auto-refreshes the conversion every 5 seconds for a real-time feel.

## Architecture
```
model/: Currency enum, RateQuote, ExchangeRateService, InMemoryRateService (mock), HttpRateService (stub), WebSocketRateService (streaming-ready mock)
presenter/: ConverterPresenter, ConverterPresenterImpl
view/: ConverterView, ConverterViewFx (JavaFX)
app/: MainApp (JavaFX launcher, DI wiring)
```

Rules:
- The View only renders UI and forwards events; it contains no business logic.
- The Presenter talks to ExchangeRateService, validates, formats, and calls View methods.
- The Presenter is fully unit-testable (no JavaFX types inside Presenter).

## Requirements
- Java 17
- Maven 3.8+

## Run the app
- Using Maven (recommended):
```
mvn clean javafx:run
```
- From your IDE: Run the main class `com.example.currencyconverter.app.AppLauncher` (do not run `MainApp` directly).

If you encounter JavaFX runtime errors, ensure the correct JavaFX platform dependencies for your OS are available or use a JDK with JavaFX. This project uses the `javafx-maven-plugin` to simplify running.

Note (JDK 22+): JavaFX may print warnings about restricted native access. Maven runs are configured to pass the required JVM option automatically. If you run from your IDE using AppLauncher, add this VM option to your Run Configuration to suppress the warnings:

--enable-native-access=ALL-UNNAMED

## Run tests
```
mvn test
```

## Real-time via API or WebSocket
- A new `WebSocketRateService` class is provided as a streaming-ready mock. It keeps rates like the in-memory service but updates its timestamp on a background scheduler to simulate live ticks.
- To enable it, wire it in `MainApp.start(...)` instead of `InMemoryRateService`:
  ```java
  // ExchangeRateService service = new WebSocketRateService();
  // For now, we keep using the mock:
  ExchangeRateService service = new InMemoryRateService();
  ```
- The UI includes a "Live updates" toggle that re-runs the conversion every 5 seconds. This works with any `ExchangeRateService` (mock, HTTP, or WebSocket).

### Plugging a real HTTP API later
- A basic `HttpRateService` is implemented and can call endpoints shaped like `/latest?base=USD&symbols=EUR` (e.g., https://api.exchangerate.host). It also supports adding an API key as a query parameter with a configurable name.
- To enable it, provide environment variables or system properties and the app will switch from the in-memory mock automatically:
  - RATE_API_BASE_URL (or -Drate.api.baseUrl) — e.g., https://api.exchangerate.host
  - RATE_API_KEY (or -Drate.api.key) — your API key (optional for public endpoints)
  - RATE_API_KEY_NAME (or -Drate.api.keyName) — query param name for the key (defaults to apikey). Some providers use access_key or apiKey.

Examples:
- Maven run with public endpoint (no key required):
  mvn -DRATE_API_BASE_URL=https://api.exchangerate.host clean javafx:run

- Maven run with a provider that requires a key named access_key:
  mvn -DRATE_API_BASE_URL=https://api.yourprovider.tld -DRATE_API_KEY=YOUR_KEY -DRATE_API_KEY_NAME=access_key clean javafx:run

Security note: never commit your API key to source control. Prefer environment variables or IDE run configuration variables.
- Periodic updates can be achieved by leaving the "Live updates" toggle on, or by extending `HttpRateService` to cache and push updates.

### Plugging a real WebSocket later
- Replace the scheduler in `WebSocketRateService` with a real WebSocket client that updates the internal USD-anchored rate map and the `lastRefresh` timestamp from incoming messages.
- Keep the `ExchangeRateService` API unchanged so the Presenter stays the same.

## Supported mock pairs
- All pairs among the supported currencies are available. The mock services use USD as an anchor to compute cross-rates. Same-currency conversions return 1.0.

## Notes
- No business logic in JavaFX classes.
- Presenter unit tests use JUnit 5 and Mockito (a self-contained plain Java test is also provided to work without external test libs).
- Enhanced CSS for a clean look is in `src/main/resources/styles.css`.

# Currency Converter MVP

A Java 17 Maven project demonstrating MVP (Model–View–Presenter) architecture with a JavaFX UI and a mockable exchange rate service.

Features (MVP):
- Convert an amount between many currencies (USD, EUR, GBP, NGN, JPY, CAD, AUD, CNY, ZAR, INR, GHS).
- Exchange rates come from an in-memory mock service for now, with a stub HTTP service ready for future use.
- Shows last-updated timestamp for rates.
- Validates inputs and displays human-readable errors in the View.
- Redesigned JavaFX page with a cleaner layout, a swap button, and improved styling.
- Optional Live Updates toggle that auto-refreshes the conversion every 5 seconds for a real-time feel.

## Architecture
```
model/: Currency enum, RateQuote, ExchangeRateService, InMemoryRateService (mock), HttpRateService (stub), WebSocketRateService (streaming-ready mock)
presenter/: ConverterPresenter, ConverterPresenterImpl
view/: ConverterView, ConverterViewFx (JavaFX)
app/: MainApp (JavaFX launcher, DI wiring)
```

Rules:
- The View only renders UI and forwards events; it contains no business logic.
- The Presenter talks to ExchangeRateService, validates, formats, and calls View methods.
- The Presenter is fully unit-testable (no JavaFX types inside Presenter).

## Requirements
- Java 17
- Maven 3.8+

## Run the app
- Using Maven (recommended):
```
mvn clean javafx:run
```
- From your IDE: Run the main class `com.example.currencyconverter.app.AppLauncher` (do not run `MainApp` directly).

If you encounter JavaFX runtime errors, ensure the correct JavaFX platform dependencies for your OS are available or use a JDK with JavaFX. This project uses the `javafx-maven-plugin` to simplify running.

Note (JDK 22+): JavaFX may print warnings about restricted native access. Maven runs are configured to pass the required JVM option automatically. If you run from your IDE using AppLauncher, add this VM option to your Run Configuration to suppress the warnings:

--enable-native-access=ALL-UNNAMED

## Run tests
```
mvn test
```

## Real-time via API or WebSocket
- A new `WebSocketRateService` class is provided as a streaming-ready mock. It keeps rates like the in-memory service but updates its timestamp on a background scheduler to simulate live ticks.
- To enable it, wire it in `MainApp.start(...)` instead of `InMemoryRateService`:
  ```java
  // ExchangeRateService service = new WebSocketRateService();
  // For now, we keep using the mock:
  ExchangeRateService service = new InMemoryRateService();
  ```
- The UI includes a "Live updates" toggle that re-runs the conversion every 5 seconds. This works with any `ExchangeRateService` (mock, HTTP, or WebSocket).

### Plugging a real HTTP API later
- A basic `HttpRateService` is implemented and can call endpoints shaped like `/latest?base=USD&symbols=EUR` (e.g., https://api.exchangerate.host). It also supports adding an API key as a query parameter with a configurable name.
- To enable it, provide environment variables or system properties and the app will switch from the in-memory mock automatically:
  - RATE_API_BASE_URL (or -Drate.api.baseUrl) — e.g., https://api.exchangerate.host
  - RATE_API_KEY (or -Drate.api.key) — your API key (optional for public endpoints)
  - RATE_API_KEY_NAME (or -Drate.api.keyName) — query param name for the key (defaults to apikey). Some providers use access_key or apiKey.

Examples:
- Maven run with public endpoint (no key required):
  mvn -DRATE_API_BASE_URL=https://api.exchangerate.host clean javafx:run

- Maven run with a provider that requires a key named access_key:
  mvn -DRATE_API_BASE_URL=https://api.yourprovider.tld -DRATE_API_KEY=YOUR_KEY -DRATE_API_KEY_NAME=access_key clean javafx:run

Security note: never commit your API key to source control. Prefer environment variables or IDE run configuration variables.
- Periodic updates can be achieved by leaving the "Live updates" toggle on, or by extending `HttpRateService` to cache and push updates.

### Plugging a real WebSocket later
- Replace the scheduler in `WebSocketRateService` with a real WebSocket client that updates the internal USD-anchored rate map and the `lastRefresh` timestamp from incoming messages.
- Keep the `ExchangeRateService` API unchanged so the Presenter stays the same.

## Supported mock pairs
- All pairs among the supported currencies are available. The mock services use USD as an anchor to compute cross-rates. Same-currency conversions return 1.0.

## Notes
- No business logic in JavaFX classes.
- Presenter unit tests use JUnit 5 and Mockito (a self-contained plain Java test is also provided to work without external test libs).
- Enhanced CSS for a clean look is in `src/main/resources/styles.css`.

---

## Using exchangeratesapi.io
If your API key is from https://exchangeratesapi.io/ (base URL `https://api.exchangeratesapi.io/v1`):

- On free tier, the API may not allow setting `base=...`. Our HttpRateService automatically falls back by requesting both currencies as symbols relative to the provider default base (usually EUR) and computing the cross-rate internally.
- Default API key parameter name for this provider is `access_key`. The app now defaults to this name if you don’t set one.

Example runs:

- From Maven:
```
mvn -DRATE_API_BASE_URL=https://api.exchangeratesapi.io/v1 \
    -DRATE_API_KEY=YOUR_ACCESS_KEY \
    clean javafx:run
```

- From IDE (Run Configuration VM options and Environment variables):
  - Environment variables:
    - RATE_API_BASE_URL=https://api.exchangeratesapi.io/v1
    - RATE_API_KEY=YOUR_ACCESS_KEY
    - (Optional) RATE_API_KEY_NAME=access_key

Security reminder: Do not commit your key. Use environment variables or IDE run configuration variables.
