package dev.floffah.regionrtp.algorithm;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.floffah.regionrtp.RegionRTP;
import dev.floffah.regionrtp.config.RegionsConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RandomLocationAlgorithm {

    private RegionRTP plugin;

    public RandomLocationAlgorithm(RegionRTP plugin) {
        this.plugin = plugin;
    }

    public Location findRandomLocation(
        RegionsConfig.RegionDef region,
        Player player
    ) {
        int tries = 0;

        while (tries < this.plugin.getConfigProvider().getConfig().maxTries) {
            int x =
                (int) (Math.random() * (region.maxX - region.minX)) +
                region.minX;
            int z =
                (int) (Math.random() * (region.maxZ - region.minZ)) +
                region.minZ;

            Location loc = new Location(
                this.plugin.getServer().getWorld(region.world),
                x,
                0,
                z
            );

            loc.setY(
                loc
                    .getWorld()
                    .getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ())
            );

            if (loc.getY() == 0) {
                tries++;
                continue;
            }

            for (String disallowedBlock : this.plugin.getConfigProvider()
                .getConfig()
                .disallowedBlocks) {
                if (
                    loc
                        .getBlock()
                        .getType()
                        .toString()
                        .equalsIgnoreCase(disallowedBlock)
                ) {
                    tries++;
                    continue;
                }
            }

            if (
                this.plugin.townyIntegrationEnabled &&
                this.plugin.getConfigProvider().getConfig().towny.disallowTowns
            ) {
                Town town = TownyAPI.getInstance().getTown(loc);

                if (town != null) {
                    if (
                        !this.plugin.getConfigProvider()
                            .getConfig()
                            .towny.allowOpenTowns
                    ) {
                        if (!town.isOpen()) {
                            tries++;
                            continue;
                        }
                    } else {
                        tries++;
                        continue;
                    }
                }
            }

            return loc;
        }

        return null;
    }
}
