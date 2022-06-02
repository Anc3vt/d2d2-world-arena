/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

