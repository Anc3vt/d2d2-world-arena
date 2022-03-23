package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.math.RotationUtils;
import com.ancevt.d2d2world.scene.Particle;
import org.jetbrains.annotations.NotNull;

public class LazerBullet extends Bullet {

    private Sprite sprite;
    private boolean setToRemove;

    public LazerBullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        addEventListener(StandardBullet.class, Event.ADD_TO_STAGE, this::this_addToStage);
    }

    private void this_addToStage(Event event) {
        removeEventListener(StandardBullet.class);
        sprite = new Sprite(getMapkitItem().getTexture());
        sprite.setColor(Color.createRandomColor());
        sprite.setXY(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
        add(sprite);
    }

    @Override
    public void process() {
        float[] xy = RotationUtils.xySpeedOfDegree(getDegree());
        move(getSpeed() * xy[0], getSpeed() * xy[1]);

        super.process();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void destroy() {
        setSpeed(0);
        setToRemove = true;
        setCollisionEnabled(false);

        IDisplayObject displayObjectContainer = Particle.miniExplosion(10, Color.createRandomColor(), 5f);
        displayObjectContainer.setScale(0.25f, 0.25f);
        getParent().add(displayObjectContainer, getX(), getY());
    }

    @Override
    public void onEachFrame() {
        if (setToRemove && isOnWorld()) {
            toScale(0.8f, 0.8f);
            toAlpha(0.8f);
            moveY(-1);
            rotate((float) (Math.random() * 30));
            if (getAlpha() <= 0.1f) {
                getWorld().removeGameObject(this, false);
            }
        }
        super.onEachFrame();
    }

    @Override
    public void setPermanentSync(boolean permanentSync) {

    }

    @Override
    public boolean isPermanentSync() {
        return false;
    }
}
