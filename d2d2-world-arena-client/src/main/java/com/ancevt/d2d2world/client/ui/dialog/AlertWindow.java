package com.ancevt.d2d2world.client.ui.dialog;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import com.ancevt.d2d2world.client.ui.Button;
import com.ancevt.d2d2world.client.ui.UiText;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2.event.Event.ADD_TO_STAGE;

public class AlertWindow extends DisplayObjectContainer {

    private static final float DEFAULT_WIDTH = 400f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float PADDING = 20f;
    private static final float PADDING_CONTROLS = 30f;

    private final PlainRect bg;
    private final UiText uiText;
    private Runnable onCloseFunction;

    public AlertWindow() {
        bg = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        bg.setAlpha(0.95f);
        add(bg);

        uiText = new UiText();
        uiText.setSize(bg.getWidth() - PADDING * 2, bg.getHeight() - PADDING_CONTROLS);
        add(uiText, PADDING, PADDING);

        Button buttonOk = new Button("OK");
        buttonOk.setXY((getWidth() - buttonOk.getWidth()) / 2, getHeight() - PADDING_CONTROLS);
        add(buttonOk);

        buttonOk.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> {
            var e = (Button.ButtonEvent) event;
            close();
        });

        addEventListener(this, ADD_TO_STAGE, this::add_to_stage);
    }

    private void add_to_stage(Event event) {
        removeEventListener(this, ADD_TO_STAGE);
        getRoot().addEventListener(this, InputEvent.KEY_DOWN, e1 -> {
            var e = (InputEvent) e1;
            if (e.getKeyCode() == KeyCode.ENTER) {
                close();
            }
        });
    }

    public void setOnCloseFunction(Runnable onOkFunction) {
        this.onCloseFunction = onOkFunction;
    }

    public Runnable getOnCloseFunction() {
        return onCloseFunction;
    }

    public void setText(Object text) {
        uiText.setText(text);
    }

    public String getText() {
        return uiText.getText();
    }

    public void setSize(float w, float h) {
        setWidth(w);
        setHeight(h);
    }

    private void setWidth(float w) {
        bg.setWidth(w);
        uiText.setWidth(w - PADDING * 2f);
    }

    private void setHeight(float h) {
        bg.setHeight(h);
        uiText.setHeight(h - PADDING_CONTROLS - PADDING * 2f);
    }

    public float getWidth() {
        return bg.getWidth();
    }

    public float getHeight() {
        return bg.getHeight();
    }

    public void close() {
        getRoot().removeEventListener(this, InputEvent.KEY_DOWN);
        removeFromParent();
        if (onCloseFunction != null) {
            onCloseFunction.run();
        }
    }

    public void center() {
        setXY(
                (D2D2.getStage().getWidth() - getWidth()) / 2f,
                (D2D2.getStage().getHeight() - getHeight()) / 2f
        );
    }

    public static @NotNull AlertWindow show(String text, @NotNull DisplayObjectContainer doc) {
        AlertWindow alertWindow = new AlertWindow();
        alertWindow.setText(text);
        doc.add(alertWindow);
        alertWindow.center();
        return alertWindow;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldArenaClientAssets.load();
        root.setBackgroundColor(Color.GRAY);

        AlertWindow alertWindow = new AlertWindow();
        alertWindow.setText("Server is localhost:3333 unavailable");
        root.add(alertWindow);
        alertWindow.center();

        D2D2.loop();
    }
}
