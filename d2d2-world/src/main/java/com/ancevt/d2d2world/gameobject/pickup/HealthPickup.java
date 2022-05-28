
package com.ancevt.d2d2world.gameobject.pickup;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

public class HealthPickup extends Pickup {

    /**
     * Used by reflection
     */
    public static final String IDLE_COORDS = "256,0,16,16";
    private int value;

    public HealthPickup(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        //bubbleSprite.setTexture(D2D2World.getPickupBubbleTexture16());
        //bubbleSprite.setAlpha(0.75f);
        bubbleSprite.removeFromParent();
        //bubbleSprite.setXY(-8, -8);
        setValue(100);
    }

    @Property
    public void setValue(int value) {
        this.value = value;

        if (value >= 100) {
            getImage().setColor(Color.RED);
        } else if (value >= 50) {
            getImage().setColor(Color.YELLOW);
        } else {
            getImage().setColor(Color.GREEN);
        }
    }

    @Property
    public int getValue() {
        return value;
    }

    @Override
    public boolean onPlayerActorPickUpPickup(@NotNull PlayerActor playerActor) {
        if (playerActor.getHealth() >= playerActor.getMaxHealth()) return false;
        playerActor.setHealth(playerActor.getHealth() + value);
        return true;
    }

    @Override
    public String toString() {
        return "HealthPickup{" +
                "value=" + value +
                '}';
    }

    public static void main(String[] args) {
        MapIO.setMapkitsDirectory("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/");

        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating"));
        D2D2World.init(true, false);

        Pickup pickup = new HealthPickup(BuiltInMapkit.getInstance().getItemById("pickup_" + HealthPickup.class.getSimpleName()), 0);
        pickup.setScale(1.5f, 1.5f);

        stage.add(pickup, 100, 100);
        stage.add(new FpsMeter());
        D2D2.loop();
    }
}
