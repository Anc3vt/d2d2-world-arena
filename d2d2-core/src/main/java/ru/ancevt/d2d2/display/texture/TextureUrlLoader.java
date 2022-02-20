package ru.ancevt.d2d2.display.texture;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.event.EventDispatcher;
import ru.ancevt.d2d2.event.TextureUrlLoaderEvent;

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
        dispatchEvent(new TextureUrlLoaderEvent(TextureUrlLoaderEvent.TEXTURE_LOAD_START, this, null));
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
        dispatchEvent(
                new TextureUrlLoaderEvent(TextureUrlLoaderEvent.TEXTURE_LOAD_COMPLETE, this, lastLoadedTextureAtlas));
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
