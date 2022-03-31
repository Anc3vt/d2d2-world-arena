package com.ancevt.d2d2world.net.dto.server;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapContentInfoDto implements Dto {

    private final String name;
    private final String filename;
    private final Set<Mapkit> mapkits;
    private final Set<Character> characters;

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class Mapkit {
        private final String name;
        private final String dirname;
        private final Set<String> files;
    }

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class Character {
        private final String mapkitItemName;
        private final String mapkitName;
    }
}
