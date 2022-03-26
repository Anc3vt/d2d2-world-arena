package com.ancevt.d2d2world.gameobject;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class DebugPlayerActorCreator {


    public static synchronized @NotNull PlayerActor createTestPlayerActor(PlayerActor targetPlayerActor, @NotNull World world) {
        PlayerActor playerActor = (PlayerActor) MapkitManager.getInstance()
                .getByName(BuiltInMapkit.NAME)
                .getItem("character_blake")
                .createGameObject(IdGenerator.INSTANCE.getNewId());

        playerActor.setXY((float) (Math.random() * 900), (float) (Math.random() * 400));
        playerActor.setDirection(Direction.LEFT);

        playerActor.setMaxHealth(25);
        playerActor.setHealth(25);

        world.addGameObject(playerActor, 5, false);
        playerActor.getController().setEnabled(true);

        playerActor.addEventListener(Event.EACH_FRAME, event -> {
            if (targetPlayerActor != null) {
                playerActor.setAimXY(targetPlayerActor.getX(), targetPlayerActor.getY());
            }
        });

        world.addEventListener(playerActor, WorldEvent.ACTOR_DEATH, event -> {
            world.removeEventListener(playerActor);
            Async.runLater(2, TimeUnit.SECONDS,
                    () -> {
                        createTestPlayerActor(targetPlayerActor, world);
                    }
            );
        });

        Async.run(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    playerActor.getController().setA(true);
                    if(Math.random() > 0.5) playerActor.getController().setB(true);
                    Thread.sleep(500);
                    playerActor.getController().setA(false);
                    playerActor.getController().setB(false);

                    if(Math.random() > 0.5) playerActor.nextWeapon();
                    //playerActor.attack();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return playerActor;
    }
}
