package ru.ancevt.d2d2world.debug;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.event.TouchEvent;
import ru.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2.touch.TouchButton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardOpenOption.*;
import static ru.ancevt.d2d2.input.KeyCode.isShift;

public class DebugPanel extends DisplayObjectContainer {

    private static final Set<DebugPanel> debugPanels = new HashSet<>();

    private final BitmapText text;
    private final String systemPropertyName;
    private final PlainRect bg;
    private int oldX;
    private int oldY;
    private Root root;
    private boolean shiftDown;

    public DebugPanel(String systemPropertyName) {
        debugPanels.add(this);

        final int width = 300;
        final int height = 300;

        this.systemPropertyName = systemPropertyName;
        addEventListener(Event.EACH_FRAME, this::this_eachFrame);

        bg = new PlainRect(width, height, Color.BLACK);
        bg.setAlpha(0.75f);
        add(bg);

        text = new BitmapText();
        text.setColor(Color.WHITE);
        text.setBounds(width, height);
        add(text);

        TouchButton touchButton = new TouchButton(width, height, true);
        touchButton.addEventListener(TouchEvent.TOUCH_DOWN, this::touchButton_touchDown);
        touchButton.addEventListener(TouchEvent.TOUCH_DRAG, this::touchButton_touchDrag);

        addEventListener(DebugPanel.class, Event.ADD_TO_STAGE, this::this_addToStage);
        addEventListener(DebugPanel.class, Event.REMOVE_FROM_STAGE, this::this_removeFromStage);

        add(touchButton);
    }

    private void this_removeFromStage(Event event) {
        root.removeEventListeners(this);
    }

    private void this_addToStage(Event event) {
        load();
        root = getRoot();
        root.addEventListener(this, InputEvent.KEY_DOWN, this::root_keyDown);
        root.addEventListener(this, InputEvent.KEY_UP, this::root_keyUp);
    }

    private void root_keyDown(Event event) {
        var e = (InputEvent) event;
        if (isShift(e.getKeyCode())) {
            shiftDown = true;
        }
    }

    private void root_keyUp(Event event) {
        var e = (InputEvent) event;
        if (isShift(e.getKeyCode())) {
            shiftDown = false;
        }
    }

    private void touchButton_touchDown(Event event) {
        var e = (TouchEvent) event;
        oldX = (int) (e.getX() + getX());
        oldY = (int) (e.getY() + getY());
    }

    private void touchButton_touchDrag(Event event) {
        var e = (TouchEvent) event;

        if (shiftDown) {
            bg.setSize(e.getX() + 1, e.getY() + 1);
            text.setBounds(e.getX() + 1, e.getY() + 1);
            return;
        }

        final int tx = (int) (e.getX() + getX());
        final int ty = (int) (e.getY() + getY());

        move(tx - oldX, ty - oldY);

        oldX = tx;
        oldY = ty;
    }

    private void this_eachFrame(Event event) {
        if (System.getProperty(systemPropertyName) != null) {
            text.setText(System.getProperty(systemPropertyName));
        }

        if (bg.getWidth() < 10) bg.setWidth(10);
        if (text.getBoundWidth() < 10) text.setBoundWidth(10);
    }

    private void save() {
        try {
            Files.writeString(Path.of(systemPropertyName + ".tmp"),
                    (int) getX() + ";" + (int) getY() + ";" + (int) bg.getWidth() + ";" + (int) bg.getHeight() + ";" +
                            text.getText(), WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            if (Path.of(systemPropertyName + ".tmp").toFile().exists()) {
                final String string = Files.readString(Path.of(systemPropertyName + ".tmp"));
                final String[] split = string.split(";", 5);
                final int x = Integer.parseInt(split[0]);
                final int y = Integer.parseInt(split[1]);
                final int w = Integer.parseInt(split[2]);
                final int h = Integer.parseInt(split[3]);
                final String data = split[2];

                setXY(x, y);

                bg.setSize(w, h);
                text.setBounds(w, h);

                text.setText(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        debugPanels.forEach(DebugPanel::save);
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));
        root.setBackgroundColor(Color.DARK_GRAY);
        DebugPanel debugPanel = new DebugPanel("dwa");
        root.add(debugPanel);

        root.addEventListener(Event.EACH_FRAME, event -> System.setProperty("dwa", debugPanel.getX() + ""));

        System.setProperty("dwa", debugPanel.getX() + "");
        D2D2.loop();
        DebugPanel.saveAll();
    }

}




















