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

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.regex.PatternMatcher;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.panels.Button;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.desktop.scene.GameRoot;
import com.ancevt.d2d2world.desktop.settings.MonitorDevice;
import com.ancevt.d2d2world.desktop.ui.Chooser;
import com.ancevt.d2d2world.desktop.ui.Font;
import com.ancevt.d2d2world.desktop.ui.ResolutionChooser;
import com.ancevt.d2d2world.desktop.ui.UiText;
import com.ancevt.d2d2world.desktop.ui.UiTextInput;
import com.ancevt.d2d2world.desktop.ui.UiTextInputEvent;
import com.ancevt.d2d2world.desktop.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.desktop.ui.dialog.DialogWarning;
import com.ancevt.d2d2world.net.client.ServerInfoRetriever;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.AUTO_ENTER;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.CONFIG;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.DISPLAY_FULLSCREEN;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.DISPLAY_RESOLUTION;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.PLAYERNAME;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.SERVER;
import static java.lang.Integer.parseInt;

@Slf4j
public class IntroRoot extends Root {

    private static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final DisplayObjectContainer panel;
    private final PlainRect panelRect;
    private final UiTextInput uiTextInputServer;
    private final UiTextInput uiTextInputPlayerName;
    private UiText labelVersion;
    private EventListener addToStageEventListener;

    public IntroRoot(@NotNull String version, String defaultGameServer) {
        textureManager().loadTextureDataInfo("thanksto/thanksto-texturedata.inf");

        setBackgroundColor(Color.BLACK);

        UiText labelServer = new UiText();
        labelServer.setText("Server:");

        UiText labelPlayerName = new UiText();
        labelPlayerName.setText("Player name:");

        uiTextInputServer = new UiTextInput();

        if (CONFIG.getString(SERVER).isEmpty()) {
            uiTextInputServer.setText(defaultGameServer);
        } else {
            uiTextInputServer.setText(CONFIG.getString(SERVER));
        }

        uiTextInputPlayerName = new UiTextInput();
        uiTextInputPlayerName.requestFocus();
        uiTextInputPlayerName.addEventListener(UiTextInputEvent.TEXT_ENTER, this::keyEnter);
        uiTextInputPlayerName.addEventListener(UiTextInputEvent.TEXT_CHANGE, event -> {
            var e = (UiTextInputEvent) event;
            boolean valid = PatternMatcher.check(e.getText(), NAME_PATTERN);
            uiTextInputPlayerName.setColor(valid ? Color.WHITE : Color.RED);
        });

        uiTextInputServer.addEventListener(UiTextInputEvent.TEXT_ENTER, event -> uiTextInputPlayerName.requestFocus());

        String playername = CONFIG.getString(PLAYERNAME);
        if (!playername.equals("")) {
            uiTextInputPlayerName.setText(playername);
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

            PlainRect plainRect = new PlainRect(
                    getStage().getWidth() * 2,
                    getStage().getHeight() - 300,
                    Color.of(0x4d0072)
            );
            add(plainRect);

            add(new CityBgSprite(), -1920, 200);

            UiText labelThanksTo = new UiText();
            labelThanksTo.setVisible(false);
            labelThanksTo.setText("Special thanks to");
            add(labelThanksTo, D2D2.getStage().getStageWidth() / 2 - labelThanksTo.getTextWidth() / 2, 330 - 55);

            ThanksToContainer thanksToContainer = new ThanksToContainer();
            add(thanksToContainer, 0, 300);
            thanksToContainer.addEventListener(Event.COMPLETE, e -> labelThanksTo.setVisible(true));
            thanksToContainer.start();

            labelVersion = new UiText();
            labelVersion.setText(version);
            labelVersion.setWidth(1000);

            add(panel, (getStage().getStageWidth() - panelRect.getWidth()) / 2, (getStage().getStageHeight() - panelRect.getHeight()) / 4);
            UiTextInputProcessor.enableRoot(this);

            int labelVersionWidth = labelVersion.getText().length() * Font.getBitmapFont().getCharInfo('0').width();

            add(labelVersion, (getStage().getStageWidth() - labelVersionWidth) / 2, 20);

            if (CONFIG.getBoolean(AUTO_ENTER)) {
                enter(uiTextInputServer.getText(), uiTextInputPlayerName.getText());
            } else {
                getStage().addEventListener(this, Event.RESIZE, resizeEvent -> {
                    float width = getStage().getWidth();
                    float height = getStage().getHeight();
                    var root = getStage().getRoot();

                    plainRect.setWidth(D2D2.getStage().getWidth() * 2);
                    plainRect.setX(-D2D2.getStage().getWidth());

                    //root.setScaleY(height / D2D2World.ORIGIN_HEIGHT);
                    //root.setScaleX(root.getScaleY());

                    float w = width;
                    float ow = D2D2World.ORIGIN_WIDTH;
                    float s = root.getScaleX();

                    root.setX((w - ow * s) / 2);
                });
            }

            add(new UAFlag());

            add(new FpsMeter(), D2D2.getStage().getStageWidth() - 50, 5);

            if (!CONFIG.getString(PLAYERNAME).equals("") && !CONFIG.getString(SERVER).equals("")) {
                enter(CONFIG.getString(SERVER), CONFIG.getString(PLAYERNAME));
            }

            ResolutionChooser resolutionChooser = new ResolutionChooser();
            add(resolutionChooser, 270, 520);
            resolutionChooser.addEventListener(Chooser.ChooserEvent.CHOOSER_APPLY, e -> {
                VideoMode videoMode = resolutionChooser.getSelectedItem();

                if (videoMode == null) {
                    MonitorDevice.getInstance().setFullscreen(false);
                    CONFIG.setProperty(DISPLAY_RESOLUTION, ResolutionChooser.WINDOWED);
                    CONFIG.setProperty(DISPLAY_FULLSCREEN, "false");
                } else {
                    MonitorDevice.getInstance().setResolution(videoMode.getResolution());
                    MonitorDevice.getInstance().setFullscreen(true);
                    CONFIG.setProperty(DISPLAY_RESOLUTION, videoMode.getResolution());
                    CONFIG.setProperty(DISPLAY_FULLSCREEN, "true");
                }

                CONFIG.save();
            });

            boolean fullscreen = CONFIG.getBoolean(DISPLAY_FULLSCREEN);
            if (fullscreen) {
                String resolution = CONFIG.getString(DISPLAY_RESOLUTION);
                if (!resolution.isEmpty()) {
                    MonitorDevice.getInstance().setResolution(resolution);
                    MonitorDevice.getInstance().setFullscreen(true);
                }
                resolutionChooser.setCurrentItemByKey(resolution);
            } else {
                resolutionChooser.setCurrentItemByKey(ResolutionChooser.WINDOWED);
            }

            if (CONFIG.getString(DISPLAY_RESOLUTION).isEmpty() && CONFIG.getBoolean(DISPLAY_FULLSCREEN)) {
                VideoMode videoMode = LWJGLVideoModeUtils.getMaxVideoMode(MonitorDevice.getInstance().getMonitorDeviceId());
                MonitorDevice.getInstance().setResolution(videoMode.getResolution());
                MonitorDevice.getInstance().setFullscreen(true);
                CONFIG.setProperty(DISPLAY_RESOLUTION, videoMode.getResolution());
                CONFIG.save();
            }

        });

        addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.F -> {
                    if (e.isAlt()) D2D2.setFullscreen(!D2D2.isFullscreen());
                }
                case KeyCode.S -> {
                    if (e.isAlt()) D2D2.setSmoothMode(!D2D2.isSmoothMode());
                }
            }
        });
    }

    private void keyEnter(Event event) {
        enter(uiTextInputServer.getText(), uiTextInputPlayerName.getText());
    }

    public void enter(String server, String localPlayerName) {
        if (!PatternMatcher.check(uiTextInputPlayerName.getText(), NAME_PATTERN)) return;

        log.info("Enter try, server: {}, player name: {}", server, localPlayerName);

        CONFIG.setProperty(PLAYERNAME, localPlayerName);

        if (!server.contains(":")) {
            server = server.concat(":2245");
            uiTextInputServer.setText(server);
        }

        ServerInfoDto result = retrieveServerInfo(server);

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
                        D2D2.getStage().removeEventListener(this, Event.RESIZE);
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

    private @Nullable ServerInfoDto retrieveServerInfo(@NotNull String server) {
        String host = server.split(":")[0];
        int port = parseInt(server.split(":")[1]);

        Lock lock = new Lock();
        Holder<ServerInfoDto> resultHolder = new Holder<>();
        ServerInfoRetriever.retrieve(host, port, result -> {
            resultHolder.setValue(result);
            lock.unlockIfLocked();
        }, closeStatus -> {
            log.error(closeStatus.getErrorMessage());
        });

        lock.lock(5, TimeUnit.SECONDS);

        return resultHolder.getValue();
    }

    public static class UAFlag extends DisplayObjectContainer {

        final float factor = 0.25f;

        public UAFlag() {
            add(new PlainRect(factor * 240, factor * 160f / 2f, Color.of(0x1040FF)), 0, 0);
            add(new PlainRect(factor * 240, factor * 160f / 2f, Color.YELLOW), 0, factor * 160f / 2f);
        }
    }
}
