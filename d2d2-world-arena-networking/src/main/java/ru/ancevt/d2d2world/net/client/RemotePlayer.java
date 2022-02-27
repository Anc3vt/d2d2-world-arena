/*
 *   D2D2 World Arena Networking
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package ru.ancevt.d2d2world.net.client;

public class RemotePlayer {

    private final int id;
    private String name;
    private int color;
    private int controllerState;
    private float x;
    private float y;
    private int ping;
    private int frags;

    public RemotePlayer(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getControllerState() {
        return controllerState;
    }

    public void setControllerState(int controllerState) {
        this.controllerState = controllerState;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "RemotePlayer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", controllerState=" + controllerState +
                ", x=" + x +
                ", y=" + y +
                ", ping=" + ping +
                ", frags=" + frags +
                '}';
    }

    public void update(String remotePlayerName, int remotePlayerColor) {
        setName(remotePlayerName);
        setColor(remotePlayerColor);
    }
}
