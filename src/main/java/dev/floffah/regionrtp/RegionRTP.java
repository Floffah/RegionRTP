package dev.floffah.regionrtp;

import dev.floffah.regionrtp.algorithm.RandomLocationAlgorithm;
import dev.floffah.regionrtp.command.RTPAdmin;
import dev.floffah.regionrtp.command.RandomTP;
import dev.floffah.regionrtp.config.ConfigProvider;
import dev.floffah.regionrtp.config.MessageProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RegionRTP extends JavaPlugin {

    ConfigProvider configProvider;
    MessageProvider messageProvider;

    RandomLocationAlgorithm randomLocationFinder;

    public boolean townyIntegrationEnabled = false;

    @Override
    public void onLoad() {
        this.configProvider = new ConfigProvider(this);
        this.messageProvider = new MessageProvider(this);
    }

    @Override
    public void onEnable() {
        try {
            Class.forName("com.palmergames.bukkit.towny.TownyAPI");
            this.townyIntegrationEnabled = true;
            this.getLogger().info("Towny integration enabled");
        } catch (ClassNotFoundException e) {
            this.townyIntegrationEnabled = false;
            this.getLogger().info("Towny integration disabled");
        }

        RTPAdmin rtpaCommandInst = new RTPAdmin(this);
        PluginCommand rtpaBukkitCommand = this.getCommand("rtpadmin");
        rtpaBukkitCommand.setExecutor(rtpaCommandInst);
        rtpaBukkitCommand.setTabCompleter(rtpaCommandInst);

        RandomTP randomtpCommandInst = new RandomTP(this);
        PluginCommand randomtpBukkitCommand = this.getCommand("randomtp");
        randomtpBukkitCommand.setExecutor(randomtpCommandInst);
        randomtpBukkitCommand.setTabCompleter(randomtpCommandInst);

        this.randomLocationFinder = new RandomLocationAlgorithm(this);

        this.getLogger().info("RegionRTP enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("RegionRTP enabled!");
    }

    public void handleFatalException(Exception e) {
        this.getLogger().severe("A fatal exception occurred. Disabling...");
        e.printStackTrace();

        this.getServer().getPluginManager().disablePlugin(this);
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    public RandomLocationAlgorithm getRandomLocationFinder() {
        return randomLocationFinder;
    }
}
