package com.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.ancevt.d2d2world.net.dto.Dto;

import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerContentInfoDto implements Dto {

    private final String name;

    private final String filename;

    private final Set<Mapkit> mapkits;

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class Mapkit {

        private final String name;

        private final String uid;

        private final Set<String> files;
    }
}
