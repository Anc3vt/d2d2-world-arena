
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.client.net.Player;

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
    private List<Player> remotePlayers;

    public TabWindow() {
        plainRect = new PlainRect(Color.BLACK);
        add(plainRect);

        texts = new CopyOnWriteArrayList<>();

        uiServerName = new UiText();
        uiServerName.setAutoSize(true);

        addEventListener(Event.ADD_TO_STAGE, this::addToStage);
    }

    private void addToStage(Event event) {
        float sw = getStage().getWidth();
        float sh = getStage().getHeight();

        setXY(STAGE_PADDING, STAGE_PADDING);
        plainRect.setAlpha(BACKGROUND_ALPHA);

        add(uiServerName, 10, 10);

        drawTitle();
    }

    public void setPlayers(List<Player> remotePlayers) {
        this.remotePlayers = remotePlayers;

        clear();
        redraw();
    }

    private void redraw() {
        int y = 70;
        plainRect.setSize(D2D2.getStage().getWidth() - STAGE_PADDING * 2, D2D2.getStage().getHeight() - STAGE_PADDING * 2);
        for (Player player : remotePlayers) {
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

    public void setServerInfo(String serverName, int players, int maxPlayers) {
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
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));

        TabWindow tabWindow = new TabWindow();

        tabWindow.setServerInfo("D2D2 World Server", 50, 100);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Player player = new Player(i + 1, "Remote_Player_" + i, Color.createRandomColor().getValue());
            player.setPing((int) (Math.random() * 100) + 10);
            player.setFrags((int) (Math.random() * 100) + 10);
            players.add(player);
        }

        tabWindow.setPlayers(players);

        root.add(tabWindow);

        D2D2.loop();
    }
}





















