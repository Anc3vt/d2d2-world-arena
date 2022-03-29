package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ActorEvent extends Event<Actor> {

    public static final String SET_WEAPON = "setWeapon";
    public static final String AMMUNITION_CHANGE = "ammunitionChange";

    private final String weaponClassName;
    private final int ammunition;
}
