package ru.ancevt.d2d2world.desktop.ui.dialog;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2.event.IEventDispatcher;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2.panels.Button;
import ru.ancevt.d2d2.panels.TitledPanel;
import ru.ancevt.d2d2world.D2D2World;

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
        Sprite icon = new Sprite("d2d2-world-common-tileset-warning");
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
        dispatchEvent(new DialogWarningEvent(DialogWarningEvent.DIALOG_OK, this));
        getRoot().removeEventListener(InputEvent.KEY_DOWN, this);
        removeFromParent();
    }

    public static class DialogWarningEvent extends Event {
        public static final String DIALOG_OK = "dialogOk";

        public DialogWarningEvent(String type, IEventDispatcher source) {
            super(type, source);
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        D2D2World.init();

        root.add(new DialogWarning("Warning", "Info text info text info text info text info text info text info text info text "));

        D2D2.loop();
    }
}
