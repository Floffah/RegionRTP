package dev.floffah.regionrtp.command;

import dev.floffah.regionrtp.RegionRTP;
import dev.floffah.regionrtp.config.RegionsConfig;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RTPAdmin implements CommandExecutor, TabCompleter {

    RegionRTP plugin;

    public RTPAdmin(RegionRTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        if (!sender.hasPermission("regionrtp.admin")) {
            sender.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError("You do not have permission to do this.")
            );
            return true;
        }

        Component helpMessage = getHelpMessage(sender);

        if (args.length == 0) {
            sender.sendMessage(helpMessage);

            return true;
        }

        if (args[0].equals("reload")) {
            try {
                this.plugin.getConfigProvider().readConfig();
                this.plugin.getConfigProvider().readConfig();
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(
                    this.plugin.getMessageProvider()
                        .formatAdminError(
                            "Could not read config files. See console for details."
                        )
                );
            }

            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "<green><bold>Reloaded the config!</bold></green>"
                    )
            );
        } else if (sender instanceof Player player) {
            switch (args[0].toLowerCase()) {
                case "createregion":
                    this.createRegion(player, args);
                    break;
                case "deleteregion":
                    this.deleteRegion(player, args);
                    break;
                case "setpoint":
                    this.setPoint(player, args);
                    break;
            }
        } else {
            sender.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError("Only players can execute this command.")
            );
        }

        return true;
    }

    private void createRegion(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(getHelpMessage(player));
            return;
        }

        String regionName = args[1].toLowerCase();

        RegionsConfig.RegionDef existingRegion =
            this.plugin.getConfigProvider()
                .getRegions()
                .regions.stream()
                .filter(region -> region.name.equals(regionName))
                .findFirst()
                .orElse(null);

        if (existingRegion != null) {
            player.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError("A region with that name already exists")
            );
            return;
        }

        this.plugin.getConfigProvider()
            .getRegions()
            .regions.add(
                new RegionsConfig.RegionDef(
                    regionName,
                    player.getWorld().getName(),
                    0,
                    0,
                    0,
                    0
                )
            );
        try {
            this.plugin.getConfigProvider().writeRegions();
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError(
                        "Could not write config files. See console for details."
                    )
            );
            return;
        }

        player.sendMessage(
            Component.text()
                .append(
                    Component.text("Created the region ")
                        .append(
                            Component.text(regionName).decorate(
                                TextDecoration.BOLD
                            )
                        )
                        .color(NamedTextColor.GREEN),
                    Component.newline(),
                    Component.text("Set point 1")
                        .color(NamedTextColor.BLUE)
                        .hoverEvent(
                            HoverEvent.showText(
                                Component.text("Runs: ")
                                    .color(NamedTextColor.BLUE)
                                    .decorate(TextDecoration.BOLD)
                                    .append(
                                        Component.text(
                                            "/rtpa setpoint " +
                                            regionName +
                                            " 1"
                                        )
                                            .color(NamedTextColor.BLUE)
                                            .decoration(
                                                TextDecoration.BOLD,
                                                false
                                            )
                                    )
                            )
                        )
                        .clickEvent(
                            ClickEvent.suggestCommand(
                                "/rtpa setpoint " + regionName + " 1"
                            )
                        ),
                    Component.text(" | ").color(NamedTextColor.GRAY),
                    Component.text("Set point 2")
                        .color(NamedTextColor.BLUE)
                        .hoverEvent(
                            HoverEvent.showText(
                                Component.text("Runs: ")
                                    .color(NamedTextColor.BLUE)
                                    .decorate(TextDecoration.BOLD)
                                    .append(
                                        Component.text(
                                            "/rtpa setpoint " +
                                            regionName +
                                            " 2"
                                        )
                                            .color(NamedTextColor.BLUE)
                                            .decoration(
                                                TextDecoration.BOLD,
                                                false
                                            )
                                    )
                            )
                        )
                        .clickEvent(
                            ClickEvent.suggestCommand(
                                "/rtpa setpoint " + regionName + " 2"
                            )
                        )
                )
        );
    }

    private void deleteRegion(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(getHelpMessage(player));
            return;
        }

        String regionName = args[1];

        if (args.length < 3 || !args[1].equalsIgnoreCase("confirm")) {
            player.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "<red><bold>Are you sure?</bold> Type <italic>/rtpa deleteregion <region_name> confirm</italic> to confirm</red>",
                        Placeholder.unparsed("region_name", regionName)
                    )
            );
        }

        this.plugin.getConfigProvider()
            .getRegions()
            .regions.removeIf(region -> region.name.equals(regionName));

        try {
            this.plugin.getConfigProvider().writeRegions();
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError(
                        "Could not write config files. See console for details."
                    )
            );
            return;
        }

        player.sendMessage(
            MiniMessage.miniMessage()
                .deserialize(
                    "<green><bold>Deleted the region <italic>" +
                    regionName +
                    "</italic></bold></green>"
                )
        );
    }

    private void setPoint(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(getHelpMessage(player));
            return;
        }

        String regionName = args[1];
        int point = Integer.parseInt(args[2]);

        if (point != 1 && point != 2) {
            player.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError("Invalid point")
            );
            return;
        }

        this.plugin.getConfigProvider()
            .getRegions()
            .regions.stream()
            .filter(region -> region.name.equals(regionName))
            .findFirst()
            .ifPresent(region -> {
                if (point == 1) {
                    region.minX = player.getLocation().getBlockX();
                    region.minZ = player.getLocation().getBlockZ();
                } else {
                    region.maxX = player.getLocation().getBlockX();
                    region.maxZ = player.getLocation().getBlockZ();
                }
            });

        try {
            this.plugin.getConfigProvider().writeRegions();
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(
                this.plugin.getMessageProvider()
                    .formatAdminError(
                        "Could not write config files. See console for details."
                    )
            );
            return;
        }

        player.sendMessage(
            MiniMessage.miniMessage()
                .deserialize(
                    "<green><bold>Set point " +
                    point +
                    " for region <italic>" +
                    regionName +
                    "</italic> to <italic>" +
                    player.getLocation().getBlockX() +
                    ", " +
                    player.getLocation().getBlockZ() +
                    "</italic></bold></green>"
                )
        );
    }

    @NotNull
    private Component getHelpMessage(@NotNull CommandSender sender) {
        String helpMessage = "";

        helpMessage +=
        "<bold><gradient:#FCD05C:#4498DB>-- RegionRTP Admin --</gradient><reset><newline>";
        helpMessage +=
        "<#FCD05C>- /rtpa reload <gray>- <#7BA8CC>Reload the plugin";

        if (sender instanceof Player) {
            helpMessage +=
            "#FCD05C>- /rtpa createregion \\<name> <gray>- <#7BA8CC>Create a region<newline>";
            helpMessage +=
            "<#FCD05C>- /rtpa deleteregion \\<name> <gray>- <#7BA8CC>Delete a region<newline>";
            helpMessage +=
            "<#FCD05C>- /rtpa setpoint \\<name> \\<1|2> <gray>- <#7BA8CC>Set a region's min/max point<newline>";
        }

        return MiniMessage.miniMessage().deserialize(helpMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        if (!sender.hasPermission("regionrtp.admin")) {
            return null;
        }

        if (args.length == 1) {
            return List.of(
                "createregion",
                "deleteregion",
                "setpoint",
                "reload"
            );
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "createregion":
                    return List.of();
                case "deleteregion":
                case "setpoint":
                    return this.plugin.getConfigProvider()
                        .getRegions()
                        .regions.stream()
                        .map(region -> region.name)
                        .toList();
            }
        } else if (args.length == 3) {
            if (args[0].equals("setpoint")) {
                return List.of("1", "2");
            }
        }

        return null;
    }
}
