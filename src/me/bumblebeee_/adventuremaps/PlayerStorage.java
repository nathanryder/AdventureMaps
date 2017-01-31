package me.bumblebeee_.adventuremaps;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStorage {

    public static HashMap<UUID, Location> locs = new HashMap<>();
    public static HashMap<UUID, ItemStack[]> invs = new HashMap<>();

    public void savePlayer(Player p) {
        if (locs.containsKey(p.getUniqueId()))
            locs.remove(p.getUniqueId());

        locs.put(p.getUniqueId(), p.getLocation());
        if (p.getInventory().getContents() != null) {
            invs.put(p.getUniqueId(), p.getInventory().getContents());
        }
    }

    public boolean loadPlayer(Player p) {
        if (!locs.containsKey(p.getUniqueId()))
            return false;

        p.teleport(locs.get(p.getUniqueId()));
        if (invs.get(p.getUniqueId()) != null)
            p.getInventory().setContents(invs.get(p.getUniqueId()));
        return true;
    }


}
