/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package com.ancevt.d2d2.display.texture;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.event.EventDispatcher;
import com.ancevt.d2d2.event.TextureUrlLoaderEvent;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TextureUrlLoader extends EventDispatcher {

    private String url;
    private TextureAtlas lastLoadedTextureAtlas;

    public TextureUrlLoader(String url) {
        this.url = url;
    }

    public TextureUrlLoader() {
    }

    public void load() {
        if (url == null) throw new NullPointerException();
        dispatchEvent(TextureUrlLoaderEvent.builder()
                .type(TextureUrlLoaderEvent.TEXTURE_LOAD_START)
                .build());
        loadBytes(url);
    }

    public void load(String url) {
        setUrl(url);
        load();
    }

    private void loadBytes(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUrl()))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body)
                .thenApply(this::createTexture);
    }

    private byte[] createTexture(byte[] bytes) {
        this.lastLoadedTextureAtlas = D2D2.getTextureManager().loadTextureAtlas(new ByteArrayInputStream(bytes));
        dispatchEvent(TextureUrlLoaderEvent.builder()
                .type(TextureUrlLoaderEvent.TEXTURE_LOAD_COMPLETE)
                .bytes(bytes)
                .textureAtlas(lastLoadedTextureAtlas)
                .build());
        return bytes;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public TextureAtlas getLastLoadedTextureAtlas() {
        return lastLoadedTextureAtlas;
    }


}
