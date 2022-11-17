package me.noverita.plugin;

import me.noverita.plugin.AutoTravel.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MinecraftTTRPGTools extends JavaPlugin {
    private static File dataFolder;

    @Override
    public void onEnable() {
        readConfig();
        getCommand("rphelp").setExecutor(new RPHelpCommand());
        new File(getDataFolder() + "/rp_help_files").mkdirs();

        getCommand("travel").setExecutor(new TravelCommand(this));
        getCommand("viewpaths").setExecutor(new ViewPathsCommand(this));
        getCommand("addtravelnode").setExecutor(new AddNodeCommand());
        getCommand("selecttravelnode").setExecutor(new SelectNodeCommand());
        getCommand("finishtravelpath").setExecutor(new FinishPathCommand());


        if (getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            getServer().getPluginManager().registerEvents(new BonemealFancyTrees(), this);
        }
    }

    @Override
    public void onDisable() {

    }

    private void readConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        dataFolder = getDataFolder();

        // All messages that this plugin sends are in a similar format.
        // This allows you to set a common prefix.
        String messagePrefix = config.getString("messagePrefix");
        Utils.setMessagePrefix(messagePrefix);
    }

    static File getDataDirectory() {
        return dataFolder;
    }
}
