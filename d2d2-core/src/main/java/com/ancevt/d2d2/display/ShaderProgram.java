
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

        id = D2D2.getBackend().prepareShaderProgram(this);

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
        D2D2.getBackend().disposeShaderProgram(this);
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }
}
