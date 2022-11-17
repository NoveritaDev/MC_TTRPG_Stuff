package me.noverita.plugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RPHelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        if (commandSender instanceof Player) {
            if (strings.length == 0) {
                commandSender.sendMessage("This server has the following roleplay help documents available:");

                File dataFolder = new File(MinecraftTTRPGTools.getDataDirectory() + "/rp_help_files");
                if (dataFolder.exists()) {
                    File[] files = dataFolder.listFiles();
                    if (files != null) {
                        TextComponent base = new TextComponent("");
                        for (File file : files) {
                            String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
                            TextComponent component = new TextComponent(" - " + name + "\n");
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rphelp " + name));
                            base.addExtra(component);
                        }
                        commandSender.spigot().sendMessage(base);
                    } else {
                        Utils.sendMessage((Player) commandSender, "An error has occurred and no RP help files have been found. Please contact an admin.");
                    }
                }
            } else {
                String filename = String.join(" ", strings);
                String baseDirectory = "error";
                try {
                    baseDirectory = new File(MinecraftTTRPGTools.getDataDirectory() + "/rp_help_files").getCanonicalPath();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                File file = new File(baseDirectory + "/" + filename + ".txt");
                try {
                    Bukkit.getLogger().info(file.getCanonicalPath());
                    if (file.getCanonicalPath().startsWith(baseDirectory)) {
                        if (file.exists()) {
                            String message = Files.readString(file.toPath());
                            commandSender.sendMessage(message);
                        } else {
                            Utils.sendMessage((Player) commandSender, "The specified section \"" + filename + "\" doesn't seem to exist. If you believe this is an error, contact an admin.");
                        }
                    } else {
                        Utils.sendMessage((Player) commandSender, "A directory traversal attack seems to have been attempted. Admins have been notified.");
                    }
                } catch (IOException e) {
                    Utils.sendMessage((Player) commandSender, "An error has occurred, the file could not be accessed.");
                }
            }

            return true;
        }
        commandSender.sendMessage("not player");
        return false;
    }
}
