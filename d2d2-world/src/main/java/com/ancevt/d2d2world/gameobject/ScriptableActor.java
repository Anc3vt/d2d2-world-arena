/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.gameobject.action.ActionProgram;
import com.ancevt.d2d2world.gameobject.action.IScriptable;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class ScriptableActor extends Actor implements IActioned, IScriptable {

    private String script = "";
    private ActionProgram actionProgram;
    private String actionProgramData;

    public ScriptableActor(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        actionProgram = ActionProgram.STUB;
    }

    @Override
    public void setScript(@NotNull String script) {
        this.script = script;
    }

    @Override
    public @NotNull String getScript() {
        return script;
    }

    @Override
    public void setActionProgramData(@NotNull String actionProgramData) {
        this.actionProgramData = actionProgramData;
    }

    @Override
    public @NotNull String getActionProgramData() {
        return actionProgramData;
    }

    @NotNull
    @Override
    public ActionProgram getActionProgram() {
        return actionProgram;
    }
}
