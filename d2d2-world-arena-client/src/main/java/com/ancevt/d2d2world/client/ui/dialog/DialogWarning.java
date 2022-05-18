
package com.ancevt.d2d2world.client.ui.dialog;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.panels.Button;
import com.ancevt.d2d2.panels.TitledPanel;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public class DialogWarning extends TitledPanel implements EventListener {

    private static final float WIDTH = 300;
    private static final float HEIGHT = 200;

    private final BitmapText bitmapText;

    public DialogWarning(String title, String info) {
        super(title);

        setSize(WIDTH, HEIGHT);

        bitmapText = new BitmapText();
        bitmapText.setBounds(220, 120);
        bitmapText.setColor(Color.BLACK);
        bitmapText.setText(info);
        add(bitmapText, 70, 20);

        addEventListener(Event.ADD_TO_STAGE, this::addToStage);
    }

    private void addToStage(Event event) {
        setXY((getStage().getWidth() - getWidth()) / 2, (getStage().getHeight() - getHeight()) / 2);
        Sprite icon = new Sprite(D2D2WorldArenaClientAssets.getWarningTexture());
        add(icon, 20, 20);

        Button button = new Button("OK") {
            @Override
            public void onButtonPressed() {
                super.onButtonPressed();
                ok();
            }
        };

        getRoot().addEventListener(InputEvent.KEY_DOWN, this);

        button.setFocused(true);

        add(button, (getWidth() - button.getWidth()) / 2, getHeight() - button.getHeight() - 10);
    }

    @Override
    public void onEvent(Event event) {
        var e = (InputEvent) event;
        if (e.getKeyCode() == KeyCode.ENTER) ok();
    }

    private void ok() {
        dispatchEvent(DialogWarningEvent.builder()
                .type(DialogWarningEvent.DIALOG_OK)
                .build());
        getRoot().removeEventListener(InputEvent.KEY_DOWN, this);
        removeFromParent();
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class DialogWarningEvent extends Event<DialogWarning> {
        public static final String DIALOG_OK = "dialogOk";
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2World.init(false, false);

        root.add(new DialogWarning("Warning", "Info text info text info text info text info text info text info text info text "));

        D2D2.loop();
    }
}
