
package com.ancevt.d2d2world.client.scene.intro;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.regex.PatternMatcher;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
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
import com.ancevt.d2d2world.client.net.ServerInfoRetriever;
import com.ancevt.d2d2world.client.scene.GameRoot;
import com.ancevt.d2d2world.client.settings.MonitorManager;
import com.ancevt.d2d2world.client.ui.Chooser;
import com.ancevt.d2d2world.client.ui.dialog.AlertWindow;
import com.ancevt.d2d2world.client.ui.Font;
import com.ancevt.d2d2world.client.ui.MonitorChooser;
import com.ancevt.d2d2world.client.ui.ResolutionChooser;
import com.ancevt.d2d2world.client.ui.UiText;
import com.ancevt.d2d2world.client.ui.UiTextInput;
import com.ancevt.d2d2world.client.ui.UiTextInputEvent;
import com.ancevt.d2d2world.client.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.client.settings.ClientConfig.AUTO_ENTER;
import static com.ancevt.d2d2world.client.settings.ClientConfig.CONFIG;
import static com.ancevt.d2d2world.client.settings.ClientConfig.DISPLAY_FULLSCREEN;
import static com.ancevt.d2d2world.client.settings.ClientConfig.DISPLAY_MONITOR;
import static com.ancevt.d2d2world.client.settings.ClientConfig.DISPLAY_RESOLUTION;
import static com.ancevt.d2d2world.client.settings.ClientConfig.PLAYERNAME;
import static com.ancevt.d2d2world.client.settings.ClientConfig.SERVER;
import static java.lang.Integer.parseInt;

@Slf4j
public class IntroRoot extends Root {

    private static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final DisplayObjectContainer panel;
    private final PlainRect panelRect;
    private final UiTextInput uiTextInputServer;
    private final UiTextInput uiTextInputPlayername;
    private final MonitorChooser monitorChooser;
    private final ResolutionChooser resolutionChooser;
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

        uiTextInputPlayername = new UiTextInput();
        uiTextInputPlayername.requestFocus();
        uiTextInputPlayername.addEventListener(UiTextInputEvent.TEXT_ENTER, this::keyEnter);
        uiTextInputPlayername.addEventListener(UiTextInputEvent.TEXT_CHANGE, event -> {
            var e = (UiTextInputEvent) event;
            boolean valid = PatternMatcher.check(e.getText(), NAME_PATTERN);
            uiTextInputPlayername.setColor(valid ? Color.WHITE : Color.RED);
        });

        uiTextInputServer.addEventListener(UiTextInputEvent.TEXT_ENTER, event -> uiTextInputPlayername.requestFocus());

        String playername = CONFIG.getString(PLAYERNAME);
        if (!playername.equals("")) {
            uiTextInputPlayername.setText(playername);
        }

        panel = new DisplayObjectContainer();

        panelRect = new PlainRect(330, 200, Color.WHITE);
        panelRect.setVisible(false);

        panel.add(panelRect);
        panel.add(labelServer, 20, 20);
        panel.add(labelPlayerName, 20, 60);

        panel.add(uiTextInputServer, 130, 20 - 10);
        panel.add(uiTextInputPlayername, 130, 60 - 10);

        Button button = new Button("Enter") {
            @Override
            public void onButtonPressed() {
                enter(uiTextInputServer.getText(), uiTextInputPlayername.getText());
            }
        };
        button.setWidth(panelRect.getWidth());
        panel.add(button, 10, 100);

        monitorChooser = new MonitorChooser();
        resolutionChooser = new ResolutionChooser();

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
                enter(uiTextInputServer.getText(), uiTextInputPlayername.getText());
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

            add(monitorChooser, 270, 500);
            monitorChooser.addEventListener(Chooser.ChooserEvent.CHOOSER_APPLY, e -> {
                long monitorId = monitorChooser.getSelectedItemObject();
                MonitorManager.getInstance().setMonitorDeviceId(monitorId);
                CONFIG.setProperty(DISPLAY_MONITOR, monitorId);
                CONFIG.save();
            });

            monitorChooser.addEventListener(Chooser.ChooserEvent.CHOOSER_SWITCH, e -> {
                resolutionChooser.fill();
            });

            long monitorId = CONFIG.getLong(DISPLAY_MONITOR);
            if (monitorId == 0L) {
                MonitorManager.getInstance().setToPrimaryMonitorDeviceId();
                monitorChooser.setCurrentItemByValue(MonitorManager.getInstance().getPrimaryMonitorId());
            } else {
                MonitorManager.getInstance().setMonitorDeviceId(monitorId);
                monitorChooser.setCurrentItemByValue(monitorId);
            }

            add(resolutionChooser, 270, monitorChooser.getY() + 35);
            resolutionChooser.addEventListener(Chooser.ChooserEvent.CHOOSER_APPLY, e -> {
                VideoMode videoMode = resolutionChooser.getSelectedItemObject();

                if (videoMode == null) {
                    MonitorManager.getInstance().setFullscreen(false);
                    CONFIG.setProperty(DISPLAY_RESOLUTION, ResolutionChooser.WINDOWED);
                    CONFIG.setProperty(DISPLAY_FULLSCREEN, "false");
                } else {
                    MonitorManager.getInstance().setResolution(videoMode.getResolution());
                    MonitorManager.getInstance().setFullscreen(true);
                    CONFIG.setProperty(DISPLAY_RESOLUTION, videoMode.getResolution());
                    CONFIG.setProperty(DISPLAY_FULLSCREEN, "true");
                }

                CONFIG.save();
            });

            boolean fullscreen = CONFIG.getBoolean(DISPLAY_FULLSCREEN);
            if (fullscreen) {
                String resolution = CONFIG.getString(DISPLAY_RESOLUTION);
                if (!resolution.isEmpty()) {
                    MonitorManager.getInstance().setResolution(resolution);
                    MonitorManager.getInstance().setFullscreen(true);
                }
                resolutionChooser.setCurrentItemByKey(resolution);
            } else {
                resolutionChooser.setCurrentItemByKey(ResolutionChooser.WINDOWED);
            }

            if (CONFIG.getString(DISPLAY_RESOLUTION).isEmpty() && CONFIG.getBoolean(DISPLAY_FULLSCREEN)) {
                VideoMode videoMode = GLFWUtils.getMaxVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
                MonitorManager.getInstance().setResolution(videoMode.getResolution());
                MonitorManager.getInstance().setFullscreen(true);
                CONFIG.setProperty(DISPLAY_RESOLUTION, videoMode.getResolution());
                CONFIG.save();
            }

        });

        addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.ENTER -> {
                    if (e.isAlt()) {
                        if (MonitorManager.getInstance().isFullscreen()) {
                            MonitorManager.getInstance().setFullscreen(false);
                            resolutionChooser.setCurrentItemByKey(ResolutionChooser.WINDOWED);
                            CONFIG.setProperty(DISPLAY_RESOLUTION, ResolutionChooser.WINDOWED);
                            CONFIG.setProperty(DISPLAY_FULLSCREEN, "false");
                        } else {
                            long monitorDeviceId = MonitorManager.getInstance().getMonitorIdByWindow();
                            MonitorManager.getInstance().setMonitorDeviceId(monitorDeviceId);
                            MonitorManager.getInstance().setFullscreen(true);
                            monitorChooser.setCurrentItemByValue(monitorDeviceId);
                            resolutionChooser.setCurrentItemByKey(MonitorManager.getInstance().getResolution());
                            CONFIG.setProperty(DISPLAY_RESOLUTION, MonitorManager.getInstance().getResolution());
                            CONFIG.setProperty(DISPLAY_FULLSCREEN, "true");
                        }
                        CONFIG.save();
                    }
                }
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
        enter(uiTextInputServer.getText(), uiTextInputPlayername.getText());
    }

    public void enter(String server, String localPlayerName) {
        if (!PatternMatcher.check(uiTextInputPlayername.getText(), NAME_PATTERN)) return;

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
        AlertWindow alertWindow = AlertWindow.show(text, D2D2.getStage().getRoot());
        alertWindow.setXY(
                (D2D2World.ORIGIN_WIDTH - alertWindow.getWidth()) / 2,
                (D2D2World.ORIGIN_HEIGHT - alertWindow.getHeight()) / 2
        );
        alertWindow.setOnCloseFunction(uiTextInputPlayername::requestFocus);
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

    public void updateResolutionControls() {
        if (MonitorManager.getInstance().isFullscreen()) {
            resolutionChooser.setCurrentItemByKey(MonitorManager.getInstance().getResolution());
        } else {
            resolutionChooser.setCurrentItemByKey(ResolutionChooser.WINDOWED);
        }

        monitorChooser.setCurrentItemByValue(MonitorManager.getInstance().getMonitorDeviceId());
    }

    public void setControlsEnabled(boolean b) {
        uiTextInputPlayername.setEnabled(b);
        uiTextInputServer.setEnabled(b);
        resolutionChooser.setEnabled(b);
        monitorChooser.setEnabled(b);
    }

    public static class UAFlag extends DisplayObjectContainer {

        final float factor = 0.25f;

        public UAFlag() {
            add(new PlainRect(factor * 240, factor * 160f / 2f, Color.of(0x1040FF)), 0, 0);
            add(new PlainRect(factor * 240, factor * 160f / 2f, Color.YELLOW), 0, factor * 160f / 2f);
        }
    }
}
