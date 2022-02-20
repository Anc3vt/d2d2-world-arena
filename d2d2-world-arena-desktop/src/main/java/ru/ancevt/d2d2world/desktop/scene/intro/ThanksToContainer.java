package ru.ancevt.d2d2world.desktop.scene.intro;

import ru.ancevt.commons.Holder;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Stage;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2world.desktop.ThanksToHtmlParser;
import ru.ancevt.d2d2world.desktop.net.HttpUtfLoader;
import ru.ancevt.d2d2world.ui.Preloader;

import java.net.http.HttpResponse;
import java.util.Map;

public class ThanksToContainer extends DisplayObjectContainer {

    private final Preloader preloader;
    private Stage stage;

    public ThanksToContainer() {
        preloader = new Preloader();
        addEventListener(Event.ADD_TO_STAGE, this::this_addToStage);
    }

    private void this_addToStage(Event event) {
        stage = getStage();
        add(preloader, stage.getStageWidth() / 2, 50);
    }

    public void start() {
        loadHtml();
    }

    private void loadHtml() {
        HttpUtfLoader.loadAsync("https://d2d2.ancevt.ru/thanksto/", this::loadHtmlResult, this::loadHtmlError);
    }

    private void loadHtmlResult(HttpResponse<String> response) {
        dispatchEvent(new Event(Event.COMPLETE, this));
        preloader.removeFromParent();

        if (response.statusCode() >= 300) {
            fallback();
            return;
        }

        Map<String, String> map;

        try {
            map = ThanksToHtmlParser.parse(response.body());
        } catch (Exception e) {
            // TODO: log
            e.printStackTrace();
            fallback();
            return;
        }

        float totalWidth = map.size() * ThanksTo.IMAGE_WIDTH + 70;
        setX((stage.getStageWidth() - totalWidth) / 2);

        Holder<Integer> xHolder = new Holder<>(30);
        map.forEach((name, pngFileName) -> {
            ThanksTo thanksTo = new ThanksTo("https://d2d2.ancevt.ru/thanksto/" + pngFileName, name);
            thanksTo.setX(xHolder.getValue());
            thanksTo.load();
            add(thanksTo);
            xHolder.setValue((int) (xHolder.getValue() + ThanksTo.IMAGE_WIDTH + (stage.getStageWidth() / ThanksTo.IMAGE_WIDTH)));
        });


    }

    private void loadHtmlError(HttpResponse<String> response, Throwable throwable) {
        fallback();
    }

    private void fallback() {
        dispatchEvent(new Event(Event.COMPLETE, this));
        preloader.removeFromParent();
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-Qryptojesus"), "Qryptojesus"), 100, 0);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-WhiteWorldBridger"), "WhiteWorldBridger"), 280, 0);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-meeekup"), "meeekup"), 460, 0);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-Me"), "Me"), 640, 0);
    }
}


























