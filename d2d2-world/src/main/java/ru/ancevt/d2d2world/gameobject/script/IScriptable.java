package ru.ancevt.d2d2world.gameobject.script;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.data.Property;
import ru.ancevt.d2d2world.gameobject.IGameObject;

public interface IScriptable extends IGameObject {

    @Property
    void setScript(@NotNull String script);

    @Property
    @NotNull String getScript();
}
