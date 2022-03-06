package ru.ancevt.d2d2world.net.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ServerMapInfoDto implements ExtraDto{

    private String name;

    private String filename;

    @Builder.Default
    private Set<Mapkit> mapkits = Set.of();

    @Data
    @Builder
    public static class Mapkit {

        private String name;

        private String uid;

        private Set<String> files;
    }
}
