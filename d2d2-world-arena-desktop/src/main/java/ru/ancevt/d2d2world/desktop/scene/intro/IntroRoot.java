/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.desktop.scene.intro;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancevt.commons.Holder;
import ru.ancevt.commons.concurrent.Lock;
import ru.ancevt.commons.regex.PatternMatcher;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2.panels.Button;
import ru.ancevt.d2d2world.desktop.DesktopConfig;
import ru.ancevt.d2d2world.desktop.scene.GameRoot;
import ru.ancevt.d2d2world.desktop.ui.Font;
import ru.ancevt.d2d2world.desktop.ui.UiText;
import ru.ancevt.d2d2world.desktop.ui.UiTextInput;
import ru.ancevt.d2d2world.desktop.ui.UiTextInputEvent;
import ru.ancevt.d2d2world.desktop.ui.UiTextInputProcessor;
import ru.ancevt.d2d2world.desktop.ui.dialog.DialogWarning;
import ru.ancevt.d2d2world.net.client.ServerInfo;
import ru.ancevt.d2d2world.net.client.ServerInfoRetriever;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;
import static ru.ancevt.d2d2world.desktop.ModuleContainer.modules;

@Slf4j
public class IntroRoot extends Root {

    private static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final DisplayObjectContainer panel;
    private final PlainRect panelRect;
    private final UiTextInput uiTextInputServer;
    private final UiTextInput uiTextInputPlayerName;
    private UiText labelVersion;
    private EventListener addToStageEventListener;

    public IntroRoot(@NotNull String version) {
        textureManager().loadTextureDataInfo("thanksto/thanksto-texturedata.inf");

        setBackgroundColor(Color.BLACK);

        UiText labelServer = new UiText();
        labelServer.setText("Server:");

        UiText labelPlayerName = new UiText();
        labelPlayerName.setText("Player name:");

        uiTextInputServer = new UiTextInput();
        DesktopConfig desktopConfig = modules.get(DesktopConfig.class);
        uiTextInputServer.setText(desktopConfig.getString(DesktopConfig.SERVER));


        uiTextInputPlayerName = new UiTextInput();
        uiTextInputPlayerName.requestFocus();
        uiTextInputPlayerName.addEventListener(UiTextInputEvent.TEXT_ENTER, this::keyEnter);
        uiTextInputPlayerName.addEventListener(UiTextInputEvent.TEXT_CHANGE, event -> {
            var e = (UiTextInputEvent) event;
            boolean valid = PatternMatcher.check(e.getText(), NAME_PATTERN);
            uiTextInputPlayerName.setColor(valid ? Color.WHITE : Color.RED);
        });

        uiTextInputServer.addEventListener(UiTextInputEvent.TEXT_ENTER, event -> uiTextInputPlayerName.requestFocus());

        try {
            if (Files.exists(Paths.get("playername.txt"))) {
                String playerName = Files.readString(Paths.get("playername.txt"));
                uiTextInputPlayerName.setText(playerName);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        panel = new DisplayObjectContainer();

        panelRect = new PlainRect(330, 200, Color.WHITE);
        panelRect.setVisible(false);

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

        addEventListener(Event.ADD_TO_STAGE, addToStageEventListener = event -> {
            removeEventListener(Event.ADD_TO_STAGE, addToStageEventListener);

            PlainRect plainRect = new PlainRect(getStage().getStageWidth(), getStage().getStageHeight() - 300, Color.DARK_BLUE);
            add(plainRect);

            add(new CityBgSprite(), 0, 150);

            UiText labelThanksTo = new UiText();
            labelThanksTo.setVisible(false);
            labelThanksTo.setText("Special thanks to");
            add(labelThanksTo, 380, 330 - 80);

            ThanksToContainer thanksToContainer = new ThanksToContainer();
            add(thanksToContainer, 0, 300);
            thanksToContainer.addEventListener(Event.COMPLETE, e -> labelThanksTo.setVisible(true));
            thanksToContainer.start();

            labelVersion = new UiText();
            labelVersion.setText(version);
            labelVersion.setWidth(1000);

            this.addEventListener(InputEvent.KEY_DOWN, evnt -> {
                var e = (InputEvent) evnt;
                switch (e.getKeyCode()) {
                    case KeyCode.F1 -> {
                        String host = uiTextInputServer.getText().split(":")[0];
                        int port = parseInt(uiTextInputServer.getText().split(":")[1]);

                        ServerInfoRetriever.retrieve(host, port, System.out::println, System.out::println);
                    }
                }
            });

            add(panel, (getStage().getStageWidth() - panelRect.getWidth()) / 2, (getStage().getStageHeight() - panelRect.getHeight()) / 4);
            UiTextInputProcessor.enableRoot(this);

            int labelVersionWidth = labelVersion.getText().length() * Font.getBitmapFont().getCharInfo('0').width();

            add(labelVersion, (getStage().getStageWidth() - labelVersionWidth) / 2, 20);
        });
    }

    private void keyEnter(Event event) {
        var e = (UiTextInputEvent) event;
        if (PatternMatcher.check(e.getText(), NAME_PATTERN))
            enter(uiTextInputServer.getText(), uiTextInputPlayerName.getText());
    }

    public void enter(String server, String localPlayerName) {
        log.info("Enter try, server: {}, player name: {}", server, localPlayerName);
        try {
            Files.writeString(Paths.get("playername.txt"), localPlayerName);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        if (!server.contains(":")) {
            server = server.concat(":2245");
            uiTextInputServer.setText(server);
        }

        ServerInfo result = retrieveServerInfo(server);

        if (result != null) {
            result.getPlayers()
                    .stream()
                    .filter(p -> p.getName().equals(localPlayerName))
                    .findAny()
                    .ifPresentOrElse(p -> {
                        warningDialog("The name \"" + localPlayerName + "\" is already taken");
                    }, () -> {
                        GameRoot gameRoot = new GameRoot();
                        gameRoot.setServerName(result.getName());
                        D2D2.getStage().setRoot(gameRoot);
                        gameRoot.start(uiTextInputServer.getText(), localPlayerName);
                    });
        } else {
            warningDialog("Server \"" + server + "\"\n is unavailable");
        }
    }

    private void warningDialog(String text) {
        UiTextInputProcessor.INSTANCE.unfocus();
        DialogWarning dialogWarning = new DialogWarning("Error", text);
        dialogWarning.addEventListener(DialogWarning.DialogWarningEvent.DIALOG_OK, event -> {
            uiTextInputPlayerName.requestFocus();
        });
        add(dialogWarning);
    }

    private @Nullable ServerInfo retrieveServerInfo(@NotNull String server) {
        String host = server.split(":")[0];
        int port = parseInt(server.split(":")[1]);

        Lock lock = new Lock();
        Holder<ServerInfo> resultHolder = new Holder<>();
        ServerInfoRetriever.retrieve(host, port, result -> {
            resultHolder.setValue(result);
            lock.unlockIfLocked();
        }, closeStatus -> {
            log.error(closeStatus.getErrorMessage());
        });

        lock.lock(5, TimeUnit.SECONDS);

        return resultHolder.getValue();
    }

}
