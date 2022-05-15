
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.sound.D2D2WorldSound;

public interface ISonicSynchronized extends IGameObject {

    default void playSound(String soundFilenameFromMapkit) {
        if (isOnWorld()) {
            String mapkitName = getMapkitItem().getMapkit().getName();
            String path = MapIO.getMapkitsDirectory() + mapkitName + "/" + soundFilenameFromMapkit;
            D2D2WorldSound.playSoundDoubleSafe(path, getWorld().getCamera(), getX(), getY());
            getWorld().getSyncDataAggregator().sound(this, soundFilenameFromMapkit);
        }
    }

    default void playSoundFromServer(String soundFilenameFromMapkit) {
        if (isOnWorld()) {
            String mapkitName = getMapkitItem().getMapkit().getName();
            String path = MapIO.getMapkitsDirectory() + mapkitName + "/" + soundFilenameFromMapkit;
            D2D2WorldSound.playSoundDoubleSafe(path, getWorld().getCamera(), getX(), getY());
        }
    }
}
