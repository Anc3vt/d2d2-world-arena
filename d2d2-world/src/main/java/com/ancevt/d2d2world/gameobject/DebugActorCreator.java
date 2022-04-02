package com.ancevt.d2d2world.gameobject;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.gameobject.weapon.AutomaticWeapon;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class DebugActorCreator {


    public static synchronized @NotNull PlayerActor createTestPlayerActor(PlayerActor targetPlayerActor, @NotNull World world) {
        PlayerActor playerActor = (PlayerActor) MapkitManager.getInstance()
                .getMapkit(BuiltInMapkit.NAME)
                .getItem("character_ava2")
                .createGameObject(IdGenerator.INSTANCE.getNewId());

        playerActor.setXY((float) (Math.random() * 900), (float) (Math.random() * 100));
        playerActor.setDirection(Direction.LEFT);
        playerActor.setName("dpa_" + playerActor.getName());

        playerActor.setMaxHealth(100);
        playerActor.setHealth(100);

        world.addGameObject(playerActor, 5, false);
        playerActor.getController().setEnabled(true);

        playerActor.addEventListener(Event.EACH_FRAME, event -> {
            if (targetPlayerActor != null) {
                playerActor.setAimXY(targetPlayerActor.getX(), targetPlayerActor.getY());
                playerActor.addWeapon(AutomaticWeapon.class, 1000);
            }
        });

        playerActor.addEventListener(playerActor, ActorEvent.ACTOR_DEATH, event -> {
            playerActor.removeEventListener(playerActor);
            var e = (ActorEvent) event;
            Async.runLater(2, TimeUnit.SECONDS, () -> {
                createTestPlayerActor(targetPlayerActor, world);
            });
        });

        Async.run(() -> {
            while (playerActor.isAlive()) {
                try {
                    Thread.sleep(500);
                    playerActor.getController().setA(true);
                    if (Math.random() > 0.5) playerActor.getController().setB(true);
                    Thread.sleep(500);
                    playerActor.getController().setA(false);
                    playerActor.getController().setB(false);

                    if (Math.random() > 0.5) playerActor.nextWeapon();
                    //playerActor.attack();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return playerActor;
    }
}
