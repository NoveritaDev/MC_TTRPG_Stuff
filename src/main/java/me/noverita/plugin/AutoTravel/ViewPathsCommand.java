package me.noverita.plugin.AutoTravel;

import com.google.common.graph.EndpointPair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.text.View;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViewPathsCommand implements CommandExecutor {
    private final Set<Player> active;
    private final Map<Player, Integer> taskIDs;
    private final JavaPlugin plugin;

    public ViewPathsCommand(JavaPlugin plugin) {
        active = new HashSet<>();
        taskIDs = new HashMap<>();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender commandSender,
            @NonNull Command command,
            @NonNull String s,
            @NonNull String[] strings
    ) {
        if (commandSender instanceof Player player) {
            if (active.remove(player)) {
                Bukkit.getScheduler().cancelTask(taskIDs.remove(player));
            } else {
                int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Location loc : HorseAutoTravel.getInstance().getNodes()) {
                        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 0, 0,0,0);
                        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0,0.5,0), 0, 0,0,0);
                        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0,1,0), 0, 0,0,0);
                        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0,1.5,0), 0, 0,0,0);
                        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0,2,0), 0, 0,0,0);
                    }
                    for (EndpointPair<Location> edge: HorseAutoTravel.getInstance().getEdges()) {
                        Location start = edge.nodeU();
                        Location end = edge.nodeV();
                        Vector vect = start.clone().subtract(end).toVector();
                        Location current = start.clone();
                        double length = vect.length();
                        Vector step = vect.clone().normalize();
                        for (int i = 0; i < length; ++i) {
                            current.getWorld().spawnParticle(Particle.FLAME, current.clone().add(0,2,0), 0,0,0,0);
                            current.subtract(step);
                        }
                    }
                }, 5, 5);
                taskIDs.put(player, id);
                active.add(player);
            }
            return true;
        }
        return false;
    }
}
