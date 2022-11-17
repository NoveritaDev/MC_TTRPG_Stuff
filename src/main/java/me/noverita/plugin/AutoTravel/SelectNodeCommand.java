package me.noverita.plugin.AutoTravel;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectNodeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            Location closestNode = null;
            double minimum = Double.MAX_VALUE;
            for (Location loc : HorseAutoTravel.getInstance().getNodes()) {
                double temp = loc.distanceSquared(player.getLocation());
                if (temp < minimum) {
                    minimum = temp;
                    closestNode = loc;
                }
            }

            if (closestNode != null) {
                player.sendMessage("Node Selected: " + closestNode);
                AddNodeCommand.prevNode.put(player, closestNode);
            } else {
                player.sendMessage("No nodes found.");
            }
        }
        return true;
    }
}
