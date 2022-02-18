package ru.ancevt.d2d2world.net.client;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerInfoRetrieveResult {
    private final String name;
    private final String version;
    private final String protocolVersion;
    private final String map;
    private final String mapKit;
    private final String mod;
    private final List<RemotePlayer> players;

    public ServerInfoRetrieveResult(@NotNull String name,
                                    @NotNull String version,
                                    @NotNull String protocolVersion,
                                    @NotNull String map,
                                    @NotNull String mapKit,
                                    @NotNull String mod,
                                    @NotNull List<RemotePlayer> players) {
        this.name = name;
        this.version = version;
        this.protocolVersion = protocolVersion;
        this.map = map;
        this.mapKit = mapKit;
        this.mod = mod;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getMapKit() {
        return mapKit;
    }

    public String getMap() {
        return map;
    }

    public String getMod() {
        return mod;
    }

    public List<RemotePlayer> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "ServerInfoRetrieveResult{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", map='" + map + '\'' +
                ", mapKit='" + mapKit + '\'' +
                ", mod='" + mod + '\'' +
                ", players=" + players +
                '}';
    }
}
