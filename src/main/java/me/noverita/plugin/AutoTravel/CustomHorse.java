package me.noverita.plugin.AutoTravel;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

public class CustomHorse extends Horse {
    public CustomHorse(Location location) {
        super(EntityType.HORSE, ((CraftWorld) location.getWorld()).getHandle());

        this.setPos(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void registerGoals() {
        this.goalSelector.removeAllGoals();
    }

    public void setDestination(Location loc) {
        getNavigation().moveTo(loc.getX(), loc.getY(), loc.getZ(), 2.5);
    }
}
