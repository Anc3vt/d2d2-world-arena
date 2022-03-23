package com.ancevt.d2d2world.gameobject;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class DebugPlayerActorCreator {


    public static @NotNull PlayerActor createTestPlayerActor(PlayerActor targetPlayerActor, @NotNull World world) {
        PlayerActor playerActor = (PlayerActor) MapkitManager.getInstance()
                .getByName(CharacterMapkit.NAME)
                .getItem("character_blake")
                .createGameObject(world.getNextFreeGameObjectId());

        playerActor.setXY(800, 304);
        playerActor.setDirection(Direction.LEFT);

        playerActor.setMaxHealth(100);
        playerActor.setHealth(100);

        world.addGameObject(playerActor, 5, false);
        playerActor.getController().setEnabled(true);

        playerActor.addEventListener(Event.EACH_FRAME, event -> {
            if (targetPlayerActor != null) {
                playerActor.setAimXY(targetPlayerActor.getX(), targetPlayerActor.getY());
            }
        });

        world.addEventListener(playerActor, WorldEvent.ACTOR_DEATH, event -> {
            world.removeEventListener(playerActor);
            Async.runLater(5, TimeUnit.SECONDS,
                    () -> createTestPlayerActor(targetPlayerActor, world).setXY((float) (Math.random() * 900), 300)
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
                    //playerActor.attack();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return playerActor;
    }
}
