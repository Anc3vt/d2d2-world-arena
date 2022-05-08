/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2.display;

import com.ancevt.d2d2.D2D2;

public class ShaderProgram {

    private final String vertexShader;
    private final String fragmentShader;
    private final int id;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private String log;
    private boolean disposed;

    public ShaderProgram(String vertexShader, String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;

        id = D2D2.getStarter().prepareShaderProgram(this);

        if (id == -1) {
            throw new IllegalStateException(getLog());
        }
    }

    public int getId() {
        return id;
    }

    public String getFragmentShaderSource() {
        return fragmentShader;
    }

    public String getVertexShaderSource() {
        return vertexShader;
    }

    public void setHandles(int vertexShaderHandle, int fragmentShaderHandle) {
        this.vertexShaderHandle = vertexShaderHandle;
        this.fragmentShaderHandle = fragmentShaderHandle;
    }

    public int getFragmentShaderHandle() {
        return fragmentShaderHandle;
    }

    public int getVertexShaderHandle() {
        return vertexShaderHandle;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    public void dispose () {
        D2D2.getStarter().disposeShaderProgram(this);
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }
}
