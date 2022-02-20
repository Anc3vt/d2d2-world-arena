package ru.ancevt.d2d2world.desktop.net;

import ru.ancevt.commons.concurrent.Lock;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class HttpUtfLoader {

    public static void loadAsync(String url, ResulFunction resultFn, ErrorFunction errorFn) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .whenCompleteAsync((r, t) -> {
                    if (t != null)
                        errorFn.error(r, t);
                    else
                        resultFn.result(r);
                });


    }

    @FunctionalInterface
    public interface ResulFunction {
        void result(HttpResponse<String> r);

    }
    @FunctionalInterface
    public interface ErrorFunction {
        void error(HttpResponse<String> response, Throwable throwable);
    }

    public static void main(String[] args) {
        HttpUtfLoader.loadAsync("https://d2d2.ancevt.ru/thanksto", r -> {
                    System.out.println(r.body());
                },
                (response, throwable) -> {
                    System.out.println(response.statusCode() + "\n" + response.body());
                    throwable.printStackTrace();
                });


        new Lock().lock(100, TimeUnit.SECONDS);
    }
}

