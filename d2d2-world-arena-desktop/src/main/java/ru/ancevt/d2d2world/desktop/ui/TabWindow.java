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
package ru.ancevt.d2d2world.desktop.ui;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2world.net.client.RemotePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.String.format;

public class TabWindow extends DisplayObjectContainer {

    private static final float BACKGROUND_ALPHA = 0.75f;
    private static final float STAGE_PADDING = 40.0f;
    private static final float COLUMN_1 = 10f;
    private static final float COLUMN_2 = 100f;
    private static final float COLUMN_3 = 400f;
    private static final float COLUMN_4 = 600f;

    private final PlainRect plainRect;
    private final UiText uiServerName;
    private final List<IDisplayObject> texts;
    private List<RemotePlayer> remotePlayers;

    public TabWindow() {
        plainRect = new PlainRect(Color.BLACK);
        add(plainRect);

        texts = new CopyOnWriteArrayList<>();

        uiServerName = new UiText();
        uiServerName.setAutoSize(true);

        addEventListener(Event.ADD_TO_STAGE, this::addToStage);
    }

    private void addToStage(Event event) {
        float sw = getStage().getStageWidth();
        float sh = getStage().getStageHeight();

        setXY(STAGE_PADDING, STAGE_PADDING);
        plainRect.setSize(sw - STAGE_PADDING * 2, sh - STAGE_PADDING * 2);
        plainRect.setAlpha(BACKGROUND_ALPHA);

        add(uiServerName, 10, 10);

        drawTitle();
    }

    public void setPlayers(int localPlayerId,
                           String localPlayerName,
                           int localPlayerFrags,
                           int localPlayerPing,
                           Color color,
                           List<RemotePlayer> remotePlayers) {
        this.remotePlayers = remotePlayers;

        clear();
        redraw();
        drawLocalPlayerInfo(localPlayerId, localPlayerName, localPlayerFrags, localPlayerPing, color);

    }

    private void drawLocalPlayerInfo(int localPlayerId,
                                     String localPlayerName,
                                     int localPlayerFrags,
                                     int localPlayerPing,
                                     Color color) {

        addPlayerTexts(50, localPlayerId, localPlayerName, localPlayerFrags, localPlayerPing, color);
    }

    private void redraw() {
        int y = 70;
        for (RemotePlayer player : remotePlayers) {
            Color color = Color.of(player.getColor());
            addPlayerTexts(y, player.getId(), player.getName(), player.getFrags(), player.getPing(), color);
            y += 20;
        }
    }

    private void addPlayerTexts(int y, int id, String name, int frags, int ping, Color color) {
        UiText uiId = new UiText(id);
        UiText uiName = new UiText(name);
        UiText uiFrags = new UiText(frags);
        UiText uiPing = new UiText(ping);

        uiId.setColor(color);
        uiName.setColor(color);
        uiFrags.setColor(color);
        uiPing.setColor(color);

        add(uiId, COLUMN_1, y);
        add(uiName, COLUMN_2, y);
        add(uiFrags, COLUMN_3, y);
        add(uiPing, COLUMN_4, y);

        texts.addAll(Set.of(uiId, uiName, uiFrags, uiPing));
    }

    private void clear() {
        while (!texts.isEmpty()) {
            IDisplayObject displayObject = texts.remove(0);
            displayObject.removeFromParent();
        }
    }

    public void setServerName(String serverName, int players, int maxPlayers) {
        uiServerName.setText(format("%s (%d/%d)", serverName, players, maxPlayers));
    }

    private void drawTitle() {
        Color color = Color.GRAY;

        UiText uiId = new UiText("id");
        uiId.setColor(color);

        UiText uiName = new UiText("name");
        uiName.setColor(color);

        UiText uiFrags = new UiText("frags");
        uiFrags.setColor(color);

        UiText uiPing = new UiText("ping");
        uiPing.setColor(color);

        add(uiId, COLUMN_1, 30);
        add(uiName, COLUMN_2, 30);
        add(uiFrags, COLUMN_3, 30);
        add(uiPing, COLUMN_4, 30);
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        root.setBackgroundColor(Color.DARK_BLUE);

        TabWindow tabWindow = new TabWindow();

        tabWindow.setServerName("D2D2 World Server", 50, 100);

        List<RemotePlayer> players = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            RemotePlayer player = new RemotePlayer(i + 1, "Remote_Player_" + i, Color.createRandomColor().getValue());
            player.setPing((int) (Math.random() * 100) + 10);
            player.setFrags((int) (Math.random() * 100) + 10);
            players.add(player);
        }

        tabWindow.setPlayers(99, "Ancevt", 50, 49, Color.GREEN, players);

        root.add(tabWindow);

        D2D2.loop();
    }
}





















