package me.noverita.plugin.AutoTravel;

import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<Player, Integer> travelers;

    public TravelCommand(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        travelers = new HashMap<>();
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender commandSender,
            @NonNull Command command,
            @NonNull String s,
            @NonNull String[] strings
    ) {
        if (commandSender instanceof Player player) {
            List<Location> path = HorseAutoTravel.getInstance().playerGetPath(player.getLocation(), String.join(" ", strings));

            CraftWorld world = ((CraftWorld) path.get(0).getWorld());
            CustomHorse horse = new CustomHorse(player.getLocation());
            world.addEntityToWorld(horse, CreatureSpawnEvent.SpawnReason.CUSTOM);
            horse.getBukkitEntity().addPassenger(player);

            Location loc = path.remove(0);
            horse.setDestination(loc);

            travelers.put(player,Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                Vec3 nextVec = new Vec3(loc.getX(), loc.getY(), loc.getZ());

                @Override
                public void run() {
                    if (horse.position().distanceTo(nextVec) < 5) {
                        if (path.isEmpty()) {
                            Bukkit.getScheduler().cancelTask(travelers.get(player));
                        } else {
                            Location loc = path.remove(0);
                            nextVec = new Vec3(loc.getX(), loc.getY(), loc.getZ());
                            horse.setDestination(loc);
                        }
                    }
                }
            },5,5));
        }
        return true;
    }
}
