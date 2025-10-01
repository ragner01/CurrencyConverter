package com.example.currencyconverter.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;


public class HttpRateService implements ExchangeRateService {

    private final String baseUrl;
    private final String apiKey;
    private final String apiKeyQueryParamName;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * @param baseUrl HTTP API base URL (e.g., https://api.exchangerate.host)
     * @param apiKey  API key or token if needed (can be null/blank for public APIs)
     */
    public HttpRateService(String baseUrl, String apiKey) {
        this(baseUrl, apiKey, "apikey");
    }

    /**
     * @param baseUrl HTTP API base URL
     * @param apiKey API key value (nullable)
     * @param apiKeyQueryParamName Query parameter name to send the key (default differs across providers, e.g., apikey/access_key/apiKey)
     */
    public HttpRateService(String baseUrl, String apiKey, String apiKeyQueryParamName) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.apiKey = apiKey;
        this.apiKeyQueryParamName = (apiKeyQueryParamName == null || apiKeyQueryParamName.isBlank()) ? "apikey" : apiKeyQueryParamName;
    }

    @Override
    public Optional<RateQuote> getRate(Currency base, Currency quote) {
        try {
            if (base == null || quote == null) return Optional.empty();

            // Attempt 1: Provider supports base parameter directly
            String endpoint1 = this.baseUrl + "/latest?base=" + encode(base.name()) + "&symbols=" + encode(quote.name());
            if (apiKey != null && !apiKey.isBlank()) {
                endpoint1 += "&" + apiKeyQueryParamName + "=" + encode(apiKey);
            }
            HttpRequest req1 = HttpRequest.newBuilder(URI.create(endpoint1))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> resp1 = httpClient.send(req1, HttpResponse.BodyHandlers.ofString());
            if (resp1.statusCode() / 100 == 2) {
                String body1 = resp1.body();
                Double directRate = extractRate(body1, quote);
                if (directRate != null && directRate > 0) {
                    return Optional.of(new RateQuote(base, quote, directRate, Instant.now()));
                }

            }

            String endpoint2 = this.baseUrl + "/latest?symbols=" + encode(base.name()) + "," + encode(quote.name());
            if (apiKey != null && !apiKey.isBlank()) {
                endpoint2 += "&" + apiKeyQueryParamName + "=" + encode(apiKey);
            }
            HttpRequest req2 = HttpRequest.newBuilder(URI.create(endpoint2))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> resp2 = httpClient.send(req2, HttpResponse.BodyHandlers.ofString());
            if (resp2.statusCode() / 100 != 2) {
                return Optional.empty();
            }
            String body2 = resp2.body();
            Double quotePerDefaultBase = extractRate(body2, quote);
            Double basePerDefaultBase = extractRate(body2, base);
            if (quotePerDefaultBase == null || basePerDefaultBase == null || quotePerDefaultBase <= 0 || basePerDefaultBase <= 0) {
                return Optional.empty();
            }
            double crossRate = quotePerDefaultBase / basePerDefaultBase;
            return Optional.of(new RateQuote(base, quote, crossRate, Instant.now()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }


    private static Double extractRate(String json, Currency quote) {
        if (json == null) return null;
        String q = quote.name().toUpperCase(Locale.ROOT);
        int ratesIdx = json.indexOf("\"rates\"");
        if (ratesIdx < 0) return null;
        int qIdx = json.indexOf("\"" + q + "\"", ratesIdx);
        if (qIdx < 0) return null;
        int colon = json.indexOf(':', qIdx);
        if (colon < 0) return null;

        int i = colon + 1;
        int n = json.length();

        while (i < n && Character.isWhitespace(json.charAt(i))) i++;
        int start = i;
        boolean dotSeen = false;
        while (i < n) {
            char c = json.charAt(i);
            if ((c >= '0' && c <= '9') || c == '-' || c == '+') { i++; continue; }
            if (c == '.' && !dotSeen) { dotSeen = true; i++; continue; }
            break;
        }
        if (start >= n || start == i) return null;
        try {
            return Double.parseDouble(json.substring(start, i));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String getBaseUrl() { return baseUrl; }
    public String getApiKey() { return apiKey; }
    public String getApiKeyQueryParamName() { return apiKeyQueryParamName; }
}
