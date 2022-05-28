
package com.ancevt.d2d2world.client.scene.intro;

import com.ancevt.commons.Holder;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.client.net.HttpUtfLoader;
import com.ancevt.d2d2world.client.ui.Preloader;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ThanksToContainer extends DisplayObjectContainer {

    private final Preloader preloader;
    private final List<ThanksTo> thanksToList;

    public ThanksToContainer() {
        preloader = new Preloader();
        thanksToList = new ArrayList<>();
        addEventListener(this, Event.REMOVE_FROM_STAGE, this::this_removeFromStage);

        add(preloader, D2D2.stage().getWidth() / 2, 50);
    }

    private void this_removeFromStage(Event event) {
        removeEventListener(this, Event.REMOVE_FROM_STAGE);
        dispose();
    }

    public void start() {
        loadHtml();
    }

    private void loadHtml() {
        HttpUtfLoader.loadAsync("https://d2d2.world/thanksto/", this::loadHtmlResult, this::loadHtmlError);
    }

    private void loadHtmlResult(@NotNull HttpResponse<String> response) {
        dispatchEvent(Event.builder()
                .type(Event.COMPLETE)
                .build());

        preloader.removeFromParent();

        if (response.statusCode() >= 300) {
            fallback();
            return;
        }

        Map<String, ThanksToHtmlParser.Line> map;

        try {
            map = ThanksToHtmlParser.parse(response.body());
        } catch (Exception e) {
            // TODO: log
            e.printStackTrace();
            fallback();
            return;
        }

        float totalWidth = map.size() * ThanksTo.IMAGE_WIDTH + 70;
        setX((D2D2.stage().getWidth() - totalWidth) / 2);

        Holder<Integer> xHolder = new Holder<>(30);
        map.forEach((name, line) -> {
            ThanksTo thanksTo = new ThanksTo(
                    "https://d2d2.world/thanksto/" + line.pngFileName(),
                    name,
                    line.fileSize()
            );

            thanksTo.setX(xHolder.getValue());
            thanksTo.load();
            add(thanksTo);
            thanksToList.add(thanksTo);
            xHolder.setValue(
                    (int) (xHolder.getValue() + ThanksTo.IMAGE_WIDTH + (D2D2.stage().getWidth() / ThanksTo.IMAGE_WIDTH))
            );
        });
    }

    public void dispose() {
        thanksToList.forEach(ThanksTo::dispose);
    }

    private void loadHtmlError(HttpResponse<String> response, Throwable throwable) {
        fallback();
    }

    private void fallback() {
        dispatchEvent(Event.builder()
                .type(Event.COMPLETE)
                .build());

        preloader.removeFromParent();
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-Qryptojesus"), "Qryptojesus"), 100, 0);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-WhiteWorldBridger"), "WhiteWorldBridger"), 280, 0);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-meeekup"), "meeekup"), 460, 0);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-Me"), "Me"), 640, 0);
    }
}
