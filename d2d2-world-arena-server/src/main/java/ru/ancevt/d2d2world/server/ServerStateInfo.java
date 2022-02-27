package ru.ancevt.d2d2world.server;

public class ServerStateInfo {
    private String name;
    private String version;
    private String mapKit;
    private String map;
    private String mod;
    private int maxPlayers;

    ServerStateInfo() {
        name = "";
        version = "";
        mapKit = "";
        map = "";
        mod = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setMapKit(String mapKit) {
        this.mapKit = mapKit;
    }

    public String getMapKit() {
        return mapKit;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String toString() {
        return "ServerStateInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", mapKit='" + mapKit + '\'' +
                ", map='" + map + '\'' +
                ", mod='" + mod + '\'' +
                '}';
    }
}
