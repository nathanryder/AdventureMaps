package me.bumblebeee_.adventuremaps;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.bumblebeee_.adventuremaps.commands.MainCommand;
import me.bumblebeee_.adventuremaps.listeners.registerAll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Maps extends JavaPlugin {

    Messages m = new Messages();
    MapManager map = new MapManager();

    public static HashMap<String, String> cmds = new HashMap<>();
    private static Plugin instance;

    //TODO
    //multiplayer
    //last line of sign show min/max

    @Override
    public void onEnable() {
        instance = this;
        setupFiles();
        registerAll.register();
        Bukkit.getServer().getPluginCommand("maps").setExecutor(new MainCommand());
        cmds.put("/maps help", "Shows all commands.");
        cmds.put("/maps list", "Shows all existing maps.");
        cmds.put("/maps create <name>", "Create a map.");
        cmds.put("/maps setspawn <name>", "Set a maps spawn.");
        cmds.put("/maps addreward <name> <command>", "Add a reward to a map.");
        cmds.put("/maps delreward <name> <command>", "Delete a reward from a map.");
        cmds.put("/maps setend <name>", "Set the end region of a map.");
        cmds.put("/maps setmap <name>", "Set the main folder for a map.");
        cmds.put("/maps setlimit <name>", "Set the amount limit for a map.");
        cmds.put("/maps ready <name>", "Check if map is ready to be played.");
        cmds.put("/maps settime <minutes>", "Set a time limit for a map.");
    }

    @Override
    public void onDisable() {
        for (Player p : MapManager.players.keySet()) {
            if (!p.isOnline())
                continue;
            storePlayerForLater(p);
        }
        for (AdventureMap map : MapManager.players.values()) {
            for (Player t : map.getPlayers()) {
                if (!t.isOnline())
                    continue;
                t.getInventory().setContents(PlayerStorage.invs.get(t.getUniqueId()));
                t.teleport(PlayerStorage.locs.get(t.getUniqueId()));
                PlayerStorage.invs.remove(t.getUniqueId());
                PlayerStorage.locs.remove(t.getUniqueId());
                MapManager.players.remove(t);
            }
            map.cleanup();
        }
    }

    public void setupFiles() {
        m.setup();
        map.setup();
        saveDefaultConfig();
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return (WorldEditPlugin)plugin;
    }

    public void storePlayerForLater(Player p) {
        UUID uuid = p.getUniqueId();
        if (!PlayerStorage.invs.containsKey(uuid))
            return;
        if (!PlayerStorage.locs.containsKey(uuid))
            return;

        File f = new File(getDataFolder() + File.separator + "storage.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

        Location l = PlayerStorage.locs.get(uuid);
        ItemStack[] items = PlayerStorage.invs.get(uuid);
        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
        inv.setContents(items);

        c.set("players." + String.valueOf(uuid) + ".location.world", l.getWorld().getName());
        c.set("players." + String.valueOf(uuid) + ".location.x", l.getBlockX());
        c.set("players." + String.valueOf(uuid) + ".location.y", l.getBlockY());
        c.set("players." + String.valueOf(uuid) + ".location.z", l.getBlockZ());
        c.set("players." + String.valueOf(uuid) + ".location.yaw", l.getYaw());
        c.set("players." + String.valueOf(uuid) + ".location.pitch", l.getPitch());

        final ArrayList<ItemStack> invCont = new ArrayList<>();
        for(ItemStack stack : items){
            if(stack != null) invCont.add(stack);
        }

        c.set("players." + p.getUniqueId() + ".inv", invCont);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}