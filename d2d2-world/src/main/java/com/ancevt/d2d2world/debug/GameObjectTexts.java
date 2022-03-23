package com.ancevt.d2d2world.debug;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2.event.Event.REMOVE_FROM_STAGE;

public class GameObjectTexts extends DisplayObjectContainer {

    private final Map<IGameObject, BitmapText> bitmapTextMap;
    private final World world;

    private boolean enabled;

    public GameObjectTexts(World world) {
        this.world = world;
        bitmapTextMap = new HashMap<>();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if(!enabled) {
            clear();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void clear() {
        bitmapTextMap.values().forEach(DisplayObject::removeFromParent);
        bitmapTextMap.clear();
    }

    @Override
    public void onEachFrame() {
        if (!isEnabled()) return;

        world.getGameObjects().forEach(gameObject -> {
            var bitmapText = bitmapTextMap.get(gameObject);
            if (bitmapText == null) {
                bitmapText = new BitmapText();
                bitmapText.setText(String.valueOf(gameObject.getGameObjectId()));
                bitmapText.setScale(0.5f, 0.5f);
                gameObject.addEventListener(GameObjectTexts.class, REMOVE_FROM_STAGE, this::gameObject_removeFromStage);
                bitmapTextMap.put(gameObject, bitmapText);
                add(bitmapText);
            }
            bitmapText.setXY(gameObject.getX(), gameObject.getY());
        });
    }

    private void gameObject_removeFromStage(@NotNull Event event) {
        if (event.getSource() instanceof IGameObject gameObject) {
            gameObject.removeEventListener(GameObjectTexts.class);
            BitmapText bitmapText = bitmapTextMap.get(gameObject);
            if (bitmapText != null) {
                Async.runLater(5, TimeUnit.SECONDS, bitmapText::removeFromParent);
                bitmapText.setColor(Color.DARK_GRAY);
            }
            bitmapTextMap.remove(gameObject);
        }
    }

}
