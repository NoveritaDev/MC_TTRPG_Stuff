package me.noverita.plugin.FancyTrees;

import net.minecraft.nbt.*;
import net.minecraft.world.level.material.Material;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Schematic {
    private final byte[] blocks;
    private final byte[] data;
    private final short width;
    private final short length;
    private final short height;

    public Schematic(byte[] blocks, byte[] data, short width, short length, short height) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.length = length;
        this.height = height;
    }

    static Schematic loadSchematic(File file) {
        try {
            if (file.exists()) {
                CompoundTag schematicNBT = NbtIo.readCompressed(file);
                short width = schematicNBT.getShort("Width");
                short height = schematicNBT.getShort("Height");
                short length = schematicNBT.getShort("Length");
                byte[] blocks = schematicNBT.getByteArray("Blocks");
                byte[] data = schematicNBT.getByteArray("Data");
                return new Schematic(blocks, data, width, length, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void paste(File file, Location location, Boolean ignoreAirBlock, CompletableFuture<Void> completableFuture) {
        Schematic schematicData = loadSchematic(file);
        short length = schematicData.length;
        short width = schematicData.width;
        short height = schematicData.height;
        location.subtract(width / 2.00, height / 2.00, length / 2.00); // Centers the schematic

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
                    //schematicData.blocks[index];
                    //schematicData.data[index];
                }
            }
        }

        completableFuture.complete(null);
    }
}
