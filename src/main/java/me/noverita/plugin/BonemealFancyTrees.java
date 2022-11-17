package me.noverita.plugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BonemealFancyTrees implements Listener {
    @EventHandler
    private void loadTreeSchematic(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getItem() != null
                && event.getItem().getType() == Material.BONE_MEAL
                && block != null
                && block.getType().name().endsWith("_SAPLING")) {

            // TODO: Protect against directory traversal
            File file = new File(MinecraftTTRPGTools.getDataDirectory() + "/schematics/" + event.getItem().getItemMeta().getDisplayName() + ".schematic");
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try {
                ClipboardReader reader = format.getReader(new FileInputStream(file));
                Clipboard clipboard = reader.read();
                try {
                    com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(event.getClickedBlock().getWorld());
                    EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);

                    // Saves our operation and builds the paste - ready to be completed.
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                            .to(BlockVector3.at(block.getX(), block.getY(), block.getZ())).ignoreAirBlocks(true).build();

                    try { // This simply completes our paste and then cleans up.
                        Operations.complete(operation);
                        editSession.flushSession();

                    } catch (WorldEditException e) { // If worldedit generated an exception it will go here
                        event.getPlayer().sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
