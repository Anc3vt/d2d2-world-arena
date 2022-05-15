
package com.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.action.ActionProgram;

public interface IActioned extends IGameObject {

    @Property
    void setActionProgramData(@NotNull String actionProgramData);

    @Property
    @NotNull String getActionProgramData();

    @NotNull ActionProgram getActionProgram();
}
