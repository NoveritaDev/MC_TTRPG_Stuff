package me.noverita.plugin.AutoTravel;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class AddNodeCommand implements CommandExecutor {
    static Map<Player, Location> prevNode = new HashMap();

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] strings) {
        if (commandSender instanceof Player player) {
            Location newLoc = player.getLocation();
            if (strings.length > 0) {
                HorseAutoTravel.getInstance().addNamedNode(newLoc, String.join(" ",strings));
            } else {
                HorseAutoTravel.getInstance().addNode(newLoc);
            }
            Location prev = prevNode.get(player);
            if (prev != null) {
                HorseAutoTravel.getInstance().addEdge(prev, newLoc);
            }
            prevNode.put(player, newLoc);
        }
        return true;
    }
}
