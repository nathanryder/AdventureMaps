package me.bumblebeee_.adventuremaps.listeners;

import me.bumblebeee_.adventuremaps.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        File f = new File(Maps.getInstance().getDataFolder() + File.separator + "storage.yml");
        if (!f.exists())
            return;

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection css = c.getConfigurationSection("players");
        if (css == null)
            return;

        Player p = e.getPlayer();
        String uuid = null;
        for (String s : css.getKeys(false)) {
            if (UUID.fromString(s).equals(p.getUniqueId())) {
                uuid = s;
            }
        }
        if (uuid == null)
            return;

        World w = Bukkit.getServer().getWorld(c.getString("players." + uuid + ".location.world"));
        int x = c.getInt("players." + uuid + ".location.x");
        int y = c.getInt("players." + uuid + ".location.y");
        int z = c.getInt("players." + uuid + ".location.z");
        float yaw = c.getInt("players." + uuid + ".location.yaw");
        float pitch = c.getInt("players." + uuid + ".location.pitch");
        ArrayList<ItemStack> s =(ArrayList<ItemStack>) c.getList("players." + uuid + ".inv");

        p.teleport(new Location(w, x, y, z, yaw, pitch));
        for (ItemStack i : s) {
            if (i == null)
                continue;
            p.getInventory().addItem(i);
        }
    }

}