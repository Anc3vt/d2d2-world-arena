package com.ancevt.d2d2world.gameobject;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;

public class DebugPlayerActorCreator {


    public static PlayerActor createTestPlayerActor(World world) {
        PlayerActor playerActor = (PlayerActor) MapkitManager.getInstance()
                .getByName(CharacterMapkit.NAME)
                .getItem("character_blake")
                .createGameObject(world.getNextFreeGameObjectId());

        playerActor.setXY(800, 304);
        playerActor.setDirection(Direction.LEFT);


        playerActor.setMaxHealth(10000);
        playerActor.setHealth(10000);

        world.addGameObject(playerActor, 5, false);
        Async.run(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    playerActor.attack();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return playerActor;
    }

}
