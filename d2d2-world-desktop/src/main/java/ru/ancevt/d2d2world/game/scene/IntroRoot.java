/*
 *   D2D2 World Desktop
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
package ru.ancevt.d2d2world.game.scene;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.panels.Button;
import ru.ancevt.d2d2world.game.ui.Font;
import ru.ancevt.d2d2world.game.ui.TextInputEvent;
import ru.ancevt.d2d2world.game.ui.TextInputProcessor;
import ru.ancevt.d2d2world.game.ui.UiText;
import ru.ancevt.d2d2world.game.ui.UiTextInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IntroRoot extends Root {

    private final String version;
    private final DisplayObjectContainer panel;
    private final PlainRect panelRect;
    private final UiTextInput uiTextInputServer;
    private final UiTextInput uiTextInputPlayerName;
    private UiText labelVersion;

    public IntroRoot(@NotNull String server, @NotNull String version) {
        this.version = version;

        D2D2.getTextureManager().loadTextureDataInfo("thanksto-texturedata.inf");

        setBackgroundColor(Color.BLACK);

        UiText labelServer = new UiText();
        labelServer.setText("Server:");

        UiText labelPlayerName = new UiText();
        labelPlayerName.setText("Player name:");

        uiTextInputServer = new UiTextInput();
        uiTextInputServer.setText(server);

        uiTextInputPlayerName = new UiTextInput();
        uiTextInputPlayerName.focus();
        uiTextInputPlayerName.addEventListener(TextInputEvent.TEXT_ENTER, e -> {
            TextInputEvent event = (TextInputEvent) e;
            enter(uiTextInputServer.getText(), uiTextInputPlayerName.getText());
        });

        try {
            if (Files.exists(Paths.get("playername.txt"))) {
                String playerName = Files.readString(Paths.get("playername.txt"));
                uiTextInputPlayerName.setText(playerName);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        panel = new DisplayObjectContainer();

        panelRect = new PlainRect(330, 200, Color.DARK_BLUE);

        panel.add(panelRect);
        panel.add(labelServer, 20, 20);
        panel.add(labelPlayerName, 20, 60);

        panel.add(uiTextInputServer, 130, 20 - 10);
        panel.add(uiTextInputPlayerName, 130, 60 - 10);

        Button button = new Button("Enter") {
            @Override
            public void onButtonPressed() {
                enter(uiTextInputServer.getText(), uiTextInputPlayerName.getText());
            }
        };
        button.setWidth(panelRect.getWidth());
        panel.add(button, 10, 100);

        addEventListener(Event.ADD_TO_STAGE, this::addToStage);

        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-Qryptojesus"), "Qryptojesus"), 100, 380);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-WhiteWorldBridger"), "WhiteWorldBridger"), 280, 380);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-meeekup"), "meeekup"), 460, 380);
        add(new ThanksTo(D2D2.getTextureManager().getTexture("thanksto-Me"), "Me"), 640, 380);

        UiText labelThanksTo = new UiText();
        labelThanksTo.setText("Special thanks to");

        add(labelThanksTo, 380, 330);

        labelVersion = new UiText();
        labelVersion.setText(version);
        labelVersion.setWidth(1000);
    }

    public void enter(String server, String localPlayerName) {
        try {
            Files.writeString(Paths.get("playername.txt"), localPlayerName);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        GameRoot gameRoot = new GameRoot();
        D2D2.getStage().setRoot(gameRoot);
        gameRoot.start(uiTextInputServer.getText(), localPlayerName);
    }

    private void addToStage(Event event) {
        PlainRect plainRect = new PlainRect(getStage().getStageWidth(), getStage().getStageHeight() - 300, Color.DARK_BLUE);
        add(plainRect);

        add(panel, (getStage().getStageWidth() - panelRect.getWidth()) / 2, (getStage().getStageHeight() - panelRect.getHeight()) / 4);
        TextInputProcessor.enableRoot(this);

        int labelVersionWidth = labelVersion.getText().length() * Font.getBitmapFont().getCharInfo('0').width();

        add(labelVersion, (getStage().getStageWidth() - labelVersionWidth) / 2, 20);
    }

}
