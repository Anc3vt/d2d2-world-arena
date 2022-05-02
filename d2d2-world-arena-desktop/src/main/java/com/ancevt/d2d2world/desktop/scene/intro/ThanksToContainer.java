/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.desktop.scene.intro;

import lombok.extern.slf4j.Slf4j;
import com.ancevt.commons.Holder;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.desktop.net.HttpUtfLoader;
import com.ancevt.d2d2world.desktop.ui.component.Preloader;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ThanksToContainer extends DisplayObjectContainer {

    private final Preloader preloader;
    private Stage stage;
    private final List<ThanksTo> thanksToList;

    public ThanksToContainer() {
        preloader = new Preloader();
        thanksToList = new ArrayList<>();
        addEventListener(1, Event.ADD_TO_STAGE, this::this_addToStage);
        addEventListener(2, Event.REMOVE_FROM_STAGE, this::this_removeFromStage);
    }

    private void this_addToStage(Event event) {
        stage = getStage();
        add(preloader, stage.getStageWidth() / 2, 50);
    }

    private void this_removeFromStage(Event event) {
        removeEventListener(2);
        dispose();
    }

    public void start() {
        loadHtml();
    }

    private void loadHtml() {
        HttpUtfLoader.loadAsync("https://d2d2.ancevt.com/thanksto/", this::loadHtmlResult, this::loadHtmlError);
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
        setX((stage.getStageWidth() - totalWidth) / 2);

        Holder<Integer> xHolder = new Holder<>(30);
        map.forEach((name, line) -> {
            ThanksTo thanksTo = new ThanksTo(
                    "https://d2d2.ancevt.com/thanksto/" + line.pngFileName(),
                    name,
                    line.fileSize()
            );

            thanksTo.setX(xHolder.getValue());
            thanksTo.load();
            add(thanksTo);
            thanksToList.add(thanksTo);
            xHolder.setValue(
                    (int) (xHolder.getValue() + ThanksTo.IMAGE_WIDTH + (stage.getStageWidth() / ThanksTo.IMAGE_WIDTH))
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
