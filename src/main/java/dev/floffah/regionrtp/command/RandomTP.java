package dev.floffah.regionrtp.command;

import dev.floffah.regionrtp.RegionRTP;
import dev.floffah.regionrtp.config.RegionsConfig;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomTP implements CommandExecutor, TabCompleter {

    RegionRTP plugin;

    public RandomTP(RegionRTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        if (!sender.hasPermission("regionrtp.rtp")) {
            sender.sendMessage(
                plugin.getMessageProvider().getMessage("no-permission")
            );
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(
                plugin
                    .getMessageProvider()
                    .getMessage(
                        sender.hasPermission("regionrtp.rtp.others")
                            ? "randomtp-usage-others"
                            : "randomtp-usage"
                    )
            );
            return true;
        }

        String regionName;
        String playerName;

        if (args.length == 1) {
            regionName = args[0];
            playerName = sender.getName();
        } else {
            regionName = args[0];
            playerName = args[1];
        }

        if (!(sender instanceof Player)) {
            if (playerName.equals(sender.getName())) {
                sender.sendMessage(
                    plugin.getMessageProvider().getMessage("no-console")
                );
                return true;
            }
        }

        Player player = plugin.getServer().getPlayer(playerName);

        if (player == null) {
            sender.sendMessage(
                plugin.getMessageProvider().getMessage("player-not-found")
            );
            return true;
        }

        if (
            !playerName.equals(sender.getName()) &&
            !sender.hasPermission("regionrtp.rtp.others")
        ) {
            sender.sendMessage(
                plugin.getMessageProvider().getMessage("no-permission")
            );
            return true;
        }

        RegionsConfig.RegionDef region =
            this.plugin.getConfigProvider()
                .getRegions()
                .regions.stream()
                .filter(r -> r.name.equals(regionName))
                .findFirst()
                .orElse(null);

        if (region == null) {
            sender.sendMessage(
                plugin.getMessageProvider().getMessage("region-not-found")
            );
            return true;
        }

        Location randomLocation =
            this.plugin.getRandomLocationFinder()
                .findRandomLocation(region, player);

        if (randomLocation == null) {
            sender.sendMessage(
                plugin.getMessageProvider().getMessage("no-location-found")
            );
            return true;
        }

        player.teleport(randomLocation);

        sender.sendMessage(
            plugin
                .getMessageProvider()
                .getMessage(
                    "teleported",
                    Placeholder.unparsed("region_name", region.name),
                    Placeholder.unparsed("player_name", player.getName())
                )
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        return null;
    }
}
