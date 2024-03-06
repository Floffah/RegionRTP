package dev.floffah.regionrtp.config;

import java.util.List;

public class RegionsConfig {

    public List<RegionDef> regions = List.of(
        new RegionDef("spawn", "world", -100, -100, 100, 100)
    );

    public static class RegionDef {

        public String name;
        public String world;
        public int minX;
        public int minZ;
        public int maxX;
        public int maxZ;

        public RegionDef() {}

        public RegionDef(
            String name,
            String world,
            int minX,
            int minZ,
            int maxX,
            int maxZ
        ) {
            this.name = name;
            this.world = world;
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
        }
    }
}
