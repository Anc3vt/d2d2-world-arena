
package com.ancevt.d2d2world.client.net;

public class Player {

    private final int id;
    private String name;
    private int color;
    private int ping;
    private int frags;

    private boolean chatOpened;
    private int playerActorGameObjectId;

    public Player(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void setPlayerActorGameObjectId(int playerActorGameObjectId) {
        this.playerActorGameObjectId = playerActorGameObjectId;
    }

    public int getPlayerActorGameObjectId() {
        return playerActorGameObjectId;
    }

    public void setChatOpened(boolean b) {
        this.chatOpened = b;
    }

    public boolean isChatOpened() {
        return chatOpened;
    }

    public int getFrags() {
        return frags;
    }

    public void incrementFrags() {
        setFrags(getFrags() + 1);
    }

    public void decrementFrags() {
        setFrags(getFrags() - 1);
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

    @Override
    public String toString() {
        return "RemotePlayer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", ping=" + ping +
                ", frags=" + frags +
                '}';
    }

    public void update(String remotePlayerName, int remotePlayerColor) {
        setName(remotePlayerName);
        setColor(remotePlayerColor);
    }
}
