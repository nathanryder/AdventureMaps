package me.bumblebeee_.adventuremaps.listeners;

import me.bumblebeee_.adventuremaps.AdventureMap;
import me.bumblebeee_.adventuremaps.MapManager;
import me.bumblebeee_.adventuremaps.Messages;
import me.bumblebeee_.adventuremaps.PlayerStorage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {

    PlayerStorage ps = new PlayerStorage();
    Messages m = new Messages();
    MapManager mm = new MapManager();

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if (b == null)
            return;
        if (b.getType() == Material.AIR)
            return;

        if (!(b.getState() instanceof Sign))
            return;

        Sign s = (Sign) b.getState();

        if (!ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[Adventure]"))
            return;

        String type = s.getLine(1);
        String arena = s.getLine(2);

        if (type.toLowerCase().contains("single")) {
            AdventureMap map = new AdventureMap(arena, "single");
            if (!map.verify()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed! Please make sure the map is fully set up!"));
                return;
            }

            int limit = mm.getLimit(map.getName());
            int amount = 0;
            if (MapManager.amounts.containsKey(map.getName())) {
                amount = MapManager.amounts.get(map.getName());
            }
            if (limit != 0 && amount >= limit) {
                p.sendMessage(m.getMessage("limitReached"));
                return;
            }

            ps.savePlayer(p);
            p.getInventory().clear();

            p.sendMessage(m.getMessage("waitWhileLoading"));
            map.load();
            map.addPlayer(p);
        }
    }

}