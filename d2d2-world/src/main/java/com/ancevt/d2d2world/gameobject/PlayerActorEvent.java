package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PlayerActorEvent extends Event<PlayerActor> {

    public static final String SET_WEAPON = "setWeapon";
    public static final String AMMUNITION_CHANGE = "ammunitionChange";

    private final Weapon weapon;
    private final int ammunition;
}
