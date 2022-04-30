package com.ancevt.d2d2world.desktop.ui.playerarrowview;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.DebugGrid;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.IDisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;

import java.util.HashMap;
import java.util.Map;

public class PlayerArrowView extends DisplayObjectContainer {

    private final IDisplayObject world;
    private float viewportWidth;
    private float viewportHeight;

    //private final BorderedRect debugBorderedRect;
    private final Map<IDisplayObject, PlayerArrow> playerArrowMap;

    public PlayerArrowView(IDisplayObject world) {
        this.world = world;
        //debugBorderedRect = new BorderedRect(null, Color.YELLOW);
        //add(debugBorderedRect);

        playerArrowMap = new HashMap<>();
    }

    public IDisplayObject getWorld() {
        return world;
    }

    public void createPlayerArrow(IDisplayObject target, Color color) {
        PlayerArrow playerArrow = new PlayerArrow(this);
        playerArrow.setColor(color);
        playerArrow.setTarget(target);
        add(playerArrow);
        playerArrowMap.put(target, playerArrow);
    }

    public void removePlayerArrow(IDisplayObject target) {
        IDisplayObject playerArrow = playerArrowMap.remove(target);
        if(playerArrow != null) {
            playerArrow.removeFromParent();
        }
    }

    public void clear() {
        playerArrowMap.keySet().forEach(IDisplayObject::removeFromParent);
        playerArrowMap.clear();
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public void setViewport(float width, float height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        //debugBorderedRect.setSize(width, height);
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(1000, 800, "(floating)"));
        D2D2World.init(true, true);
        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);

        IDisplayObjectContainer container = new DisplayObjectContainer();

        Sprite sprite = new Sprite("satellite");

        IDisplayObjectContainer world = new DisplayObjectContainer();

        world.add(new DebugGrid());
        world.add(sprite, D2D2.getStage().getStageWidth() / 4, D2D2.getStage().getStageHeight() / 4);

        PlayerArrowView view = new PlayerArrowView(world);
        view.setViewport(300, 200);
        view.createPlayerArrow(sprite, Color.BLUE);

        container.add(view, 64, 64);

        final float speed = 16 * 5;

        root.addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;

            switch (e.getKeyCode()) {
                case KeyCode.A -> world.moveX(speed);
                case KeyCode.D -> world.moveX(-speed);
                case KeyCode.W -> world.moveY(speed);
                case KeyCode.S -> world.moveY(-speed);
            }
        });

        root.addEventListener(InputEvent.MOUSE_MOVE, event -> {
            var e = (InputEvent) event;
            sprite.setXY(
                    (e.getX() - world.getX()) / world.getAbsoluteScaleX(),
                    (e.getY() - world.getY()) / world.getAbsoluteScaleY()
            );
        });

        container.add(world);
        container.setScale(2f, 2f);

        root.add(container);
        D2D2.loop();
    }
}
