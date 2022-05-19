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

public class DialogWindow extends DisplayObjectContainer {

    private static final float DEFAULT_WIDTH = 400f;
    private static final float DEFAULT_HEIGHT = 200f;
    private static final float PADDING = 20f;
    private static final float PADDING_CONTROLS = 30f;

    private final PlainRect bg;
    private final UiText uiText;
    private final Button buttonOk;
    private final Button buttonCancel;
    private Runnable onOkFunction;
    private Runnable onCancelFunction;

    public DialogWindow() {
        bg = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        bg.setAlpha(0.95f);
        add(bg);

        uiText = new UiText();
        uiText.setSize(bg.getWidth() - PADDING * 2, bg.getHeight() - PADDING_CONTROLS);
        add(uiText, PADDING, PADDING);

        buttonOk = new Button("OK");
        buttonOk.setXY((getWidth() - buttonOk.getWidth()) / 2 - 50, getHeight() - PADDING_CONTROLS);
        buttonOk.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> ok());
        add(buttonOk);

        buttonCancel = new Button("Cancel");
        buttonCancel.setXY((getWidth() - buttonOk.getWidth()) / 2 + 50, getHeight() - PADDING_CONTROLS);
        buttonCancel.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, event -> cancel());
        add(buttonCancel);

        addEventListener(this, ADD_TO_STAGE, this::add_to_stage);
    }

    private void add_to_stage(Event event) {
        removeEventListener(this, ADD_TO_STAGE);
        getRoot().addEventListener(this, InputEvent.KEY_DOWN, e1 -> {
            var e = (InputEvent) e1;
            switch (e.getKeyCode()) {
                case KeyCode.ENTER -> ok();
                case KeyCode.ESCAPE -> cancel();
            }
        });
    }

    public void setOnOkFunction(Runnable onOkFunction) {
        this.onOkFunction = onOkFunction;
    }

    public Runnable getOnOkFunction() {
        return onOkFunction;
    }

    public void setOnCancelFunction(Runnable onCancelFunction) {
        this.onCancelFunction = onCancelFunction;
    }

    public Runnable getOnCancelFunction() {
        return onCancelFunction;
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

    public void ok() {
        D2D2.getStage().getRoot().removeEventListener(this, InputEvent.KEY_DOWN);
        removeFromParent();
        if (onOkFunction != null) {
            onOkFunction.run();
        }
    }

    public void cancel() {
        D2D2.getStage().getRoot().removeEventListener(this, InputEvent.KEY_DOWN);
        removeFromParent();
        if (onCancelFunction != null) {
            onCancelFunction.run();
        }
    }

    public void center() {
        setXY(
                (getStage().getWidth() - getWidth()) / 2f,
                (getStage().getHeight() - getHeight()) / 2f
        );
    }

    public static @NotNull DialogWindow show(String text, @NotNull DisplayObjectContainer doc) {
        DialogWindow dialogWindow = new DialogWindow();
        dialogWindow.setText(text);
        doc.add(dialogWindow);
        dialogWindow.center();
        return dialogWindow;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldArenaClientAssets.load();
        root.setBackgroundColor(Color.GRAY);

        DialogWindow dialogWindow = new DialogWindow();
        dialogWindow.setText("Screen resolution was set to 1920x1080. \nLeave this configuration? (5 sec)");
        root.add(dialogWindow);
        dialogWindow.center();

        D2D2.loop();
    }
}
