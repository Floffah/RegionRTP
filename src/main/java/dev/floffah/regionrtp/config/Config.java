package dev.floffah.regionrtp.config;

import java.util.List;

public class Config {

    public int version = 1;

    public int maxTries = 20;
    public int averageY = 64;
    public boolean disallowedBlocksEnabled = true;
    public List<String> disallowedBlocks = List.of("air", "water", "chest");
    public boolean enableGlobalRegion = true;
    public Towny towny = new Towny();

    public static class Towny {

        public boolean disallowTowns = true;
        public boolean allowOpenTowns = false;
    }
}
