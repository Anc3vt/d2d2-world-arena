package ru.ancevt.d2d2.demo;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;

public class D2D2Demo_AddToStageContainer {
    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();

        DisplayObjectContainer container = new DisplayObjectContainer();

        Sprite sprite = new Sprite("satellite") {
            {
                addEventListener(Event.ADD_TO_STAGE, e -> {
                    System.out.println(e);
                });
            }
        };



        // sprite.dispatchEvent(EventPool.simpleEventSingleton(Event.ADD_TO_STAGE, sprite));

        root.add(container);

        container.add(sprite);
        D2D2.loop();
    }
}
