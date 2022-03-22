package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.IEventDispatcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SceneEvent extends Event<IEventDispatcher> {

    public static final String MAP_LOADED = "mapLoaded";
}
