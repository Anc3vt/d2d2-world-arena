
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
    public static final String ACTOR_DEATH = "actorDeath";
    public static final String ACTOR_REPAIR = "actorRepair";
    public static final String ACTOR_ENTER_ROOM = "actorEnterRoom";
    public static final String ACTOR_HOOK = "actorHook";

    private final String weaponClassName;
    private final int ammunition;
    private final String roomId;
    private final float x;
    private final float y;
    private final int hookGameObjectId;
}
