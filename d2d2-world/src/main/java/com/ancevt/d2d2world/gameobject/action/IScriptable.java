
package com.ancevt.d2d2world.gameobject.action;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.IGameObject;

public interface IScriptable extends IGameObject {

    @Property
    void setScript(@NotNull String script);

    @Property
    @NotNull String getScript();
}
