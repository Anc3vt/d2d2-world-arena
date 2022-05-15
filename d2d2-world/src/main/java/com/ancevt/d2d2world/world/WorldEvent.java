
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.map.Room;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class WorldEvent extends Event<World> {

    public static final String CHANGE_ROOM = "changeRoom";
    public static final String ACTOR_DEATH = "worldActorDeath";
    public static final String WORLD_PROCESS = "worldProcess";
    public static final String PLAYER_ACTOR_TAKE_BULLET = "playerActorTakeBullet";
    public static final String ROOM_SWITCH_COMPLETE = "roomSwitchComplete";
    public static final String ADD_GAME_OBJECT = "addGameObject";
    public static final String REMOVE_GAME_OBJECT = "removeGameObject";
    public static final String BULLET_DOOR_TELEPORT = "bulletDoorTeleport";
    public static final String DESTROYABLE_BOX_DESTROY = "destroyableBoxDestroy";
    public static final String ACTOR_ATTACK = "actorAttack";

    private final float x;
    private final float y;
    private final String roomId;
    private final Weapon.Bullet bullet;
    private final IGameObject gameObject;
    private final Actor actor;
    private final int deadActorGameObjectId;
    private final int killerGameObjectId;
    private final Room room;

}
