
package com.ancevt.d2d2.debug;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.TouchButtonEvent;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.touch.TouchButton;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.ancevt.d2d2.input.KeyCode.isShift;
import static java.nio.file.StandardOpenOption.*;

public class DebugPanel extends DisplayObjectContainer {

    private static final Map<String, DebugPanel> debugPanels = new HashMap<>();
    private static boolean enabled;
    private static float scale = 1;

    private final BitmapText text;
    private final String systemPropertyName;
    private final PlainRect bg;
    private final TouchButton touchButton;
    private int oldX;
    private int oldY;
    private boolean shiftDown;

    private DebugPanel(String systemPropertyName) {
        debugPanels.put(systemPropertyName, this);

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
        add(text, 1, 1);

        touchButton = new TouchButton(width, height, true);
        touchButton.addEventListener(TouchButtonEvent.TOUCH_DOWN, this::touchButton_touchDown);
        touchButton.addEventListener(TouchButtonEvent.TOUCH_DRAG, this::touchButton_touchDrag);

        addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);

        add(touchButton);

        setScale(scale, scale);
    }

    public void setText(Object text) {
        System.setProperty(systemPropertyName, String.valueOf(text));
    }

    public static void setScale(float scale) {
        DebugPanel.scale = scale;
    }

    public static float getScale() {
        return DebugPanel.scale;
    }

    public static void setEnabled(boolean enabled) {
        DebugPanel.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private void this_addToStage(Event event) {
        removeEventListener(this, Event.ADD_TO_STAGE);
        load();
        Root root = D2D2.getStage().getRoot();
        root.addEventListener(InputEvent.KEY_DOWN, this::root_keyDown);
        root.addEventListener(InputEvent.KEY_UP, this::root_keyUp);
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
        var e = (TouchButtonEvent) event;
        oldX = (int) (e.getX() + getX());
        oldY = (int) (e.getY() + getY());
        dispatchEvent(event);
    }

    private void touchButton_touchDrag(Event event) {
        var e = (TouchButtonEvent) event;

        if (shiftDown) {
            bg.setSize(e.getX() + 1, e.getY() + 1);
            if (bg.getWidth() < 5f) {
                bg.setWidth(5f);
            }
            if (bg.getHeight() < 5f) {
                bg.setHeight(5f);
            }

            text.setBounds(bg.getWidth(), bg.getHeight());
            touchButton.setSize(bg.getWidth(), bg.getHeight());
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
            text.setText("[" + systemPropertyName + "]\n" + System.getProperty(systemPropertyName));
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
                touchButton.setSize(w, h);
                text.setText(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        debugPanels.values().forEach(DebugPanel::save);
    }

    public static Optional<DebugPanel> show(String propertyName) {
        return show(propertyName, "");
    }

    public static Optional<DebugPanel> show(String propertyName, Object value) {
        if (enabled) {
            DebugPanel debugPanel = debugPanels.get(propertyName);
            if (debugPanel == null) {
                debugPanel = new DebugPanel(propertyName);
            }

            D2D2.getStage().getRoot().add(debugPanel);
            if (propertyName != null) {
                System.setProperty(propertyName, String.valueOf(value));
            }
            return Optional.of(debugPanel);
        }
        return Optional.empty();
    }

    public static Optional<DebugPanel> show(String propertyName, @NotNull Supplier<Object> supplier) {
        return show(propertyName, supplier.get());
    }

    public static void setProperty(String key, Object value) {
        System.setProperty(key, String.valueOf(value));
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        root.setBackgroundColor(Color.DARK_GRAY);

        DebugPanel.setEnabled(true);

        root.addEventListener(Event.EACH_FRAME, event -> {
            DebugPanel.show("debug-panel").ifPresent(debugPanel -> {
                debugPanel.setText(debugPanel.getX());
            });
        });

        D2D2.loop();
        DebugPanel.saveAll();
    }

}



