package com.ancevt.d2d2world.playground;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.media.SoundMachine;
import com.ancevt.d2d2.panels.Button;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class PlaygroundSound {

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

        Button buttonPlay = new Button("play") {
            @Override
            public void onButtonPressed() {
                play();
            }
        };
        Button buttonStop = new Button("stop") {
            @Override
            public void onButtonPressed() {
                stop();
            }
        };

        root.add(buttonPlay, 100, 100);
        root.add(buttonStop, 100, 150);

        D2D2.loop();
    }

    private static void play() {
        SoundMachine.getInstance().play("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/automatic.wav");
        //snd.play();
    }

    private static void stop() {
        SoundMachine.getInstance().stop("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/automatic.wav");
        //snd.stop();
    }
}
