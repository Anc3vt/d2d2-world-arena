/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.dev;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.D2D2WorldAssets;
import com.ancevt.d2d2world.control.LocalPlayerController;
import com.ancevt.d2d2world.gameobject.PlayerActor_;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.world.Camera;
import com.ancevt.d2d2world.world.World;

import java.io.IOException;

public class Dev {

    private static World world;

    private static float MOVE_SPEED = 16.0f;
    private static final CameraMovement cameraMovement = new CameraMovement();

    private static PlayerActor_ playerActor;

    private static LocalPlayerController controller = new LocalPlayerController();

    public static void main(String[] args) throws IOException {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldAssets.load();
        D2D2World.init(false, false);

        MapIO.setMapkitsDirectory("/home/ancevt/java/workspace/d2d2-world-arena/d2d2-world-arena-server/data/mapkits/");
        MapIO.setMapsDirectory("/home/ancevt/java/workspace/d2d2-world-arena/d2d2-world-arena-server/data/maps/");

        GameMap map = MapIO.load("map0.wam");

        world = new World();
        world.setMap(map);

        stage.addEventListener(Dev.class, InputEvent.KEY_DOWN, Dev::keyDown);
        stage.addEventListener(Dev.class, InputEvent.KEY_UP, Dev::keuUp);
        stage.addEventListener(Dev.class, Event.EACH_FRAME, Dev::eachFame);

        world.setRoomRectVisible(true);

        Container container = new Container();

        container.add(world);
        stage.add(container, stage.getWidth() / 2, stage.getHeight() / 2);

        container.setScale(3f, 3f);
        world.setPlaying(true);
        world.getCamera().setBoundsLock(true);

        stage.addEventListener(Dev.class, Event.RESIZE, event -> {
            world.getCamera().setViewportSize(stage.getWidth(), stage.getHeight());
            world.getCamera().setBounds(world.getRoom().getWidth(), world.getRoom().getHeight());
            container.setXY(stage.getWidth() / 2, stage.getHeight() / 2);
        });

        playerActor = (PlayerActor_) BuiltInMapkit.getInstance().getItemById("blake").createGameObject(1);
        playerActor.setXY(64, 64);
        playerActor.setLocalPlayerActor(true);
        playerActor.setName("_player");
        playerActor.setController(controller);

        controller.setEnabled(true);

        world.getCamera().setAttachedTo(playerActor);

        world.addGameObject(playerActor, 5, false);

        D2D2.loop();
    }

    private static void eachFame(Event event) {
        Camera cam = world.getCamera();
        if (cameraMovement.l) cam.moveX(-MOVE_SPEED);
        if (cameraMovement.r) cam.moveX(MOVE_SPEED);
        if (cameraMovement.t) cam.moveY(-MOVE_SPEED);
        if (cameraMovement.b) cam.moveY(MOVE_SPEED);
        world.getCamera().process();
    }

    private static void keyDown(Event event) {
        var e = (InputEvent) event;
        if (e.isShift()) {
            switch (e.getKeyCode()) {
                case KeyCode.A -> cameraMovement.l = true;
                case KeyCode.D -> cameraMovement.r = true;
                case KeyCode.W -> cameraMovement.t = true;
                case KeyCode.S -> cameraMovement.b = true;
            }
        } else {
            controller.key(e.getKeyCode(), e.getKeyChar(), true);
        }
    }

    private static void keuUp(Event event) {
        var e = (InputEvent) event;
        switch (e.getKeyCode()) {
            case KeyCode.A -> cameraMovement.l = false;
            case KeyCode.D -> cameraMovement.r = false;
            case KeyCode.W -> cameraMovement.t = false;
            case KeyCode.S -> cameraMovement.b = false;
        }

        controller.key(e.getKeyCode(), e.getKeyChar(), false);
    }

    private static class CameraMovement {
        boolean l, r, t, b;
    }
}

















