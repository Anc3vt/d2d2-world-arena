/*
 *   D2D2 World Arena Client
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.client.net;

import com.ancevt.commons.concurrent.Lock;

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
        HttpUtfLoader.loadAsync("https://d2d2.world/thanksto/", r -> {
                    System.out.println(r.body());
                },
                (response, throwable) -> {
                    System.out.println(response.statusCode() + "\n" + response.body());
                    throwable.printStackTrace();
                });


        new Lock().lock(100, TimeUnit.SECONDS);
    }
}

