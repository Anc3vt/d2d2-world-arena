package ru.ancevt.d2d2world.gameobject;

import ru.ancevt.d2d2world.data.Property;

public interface ISpeedable {

    @Property
    void setSpeed(float speed);

    @Property
    float getSpeed();
}
