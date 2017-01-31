package me.bumblebeee_.adventuremaps.listeners;

import me.bumblebeee_.adventuremaps.AdventureMap;
import me.bumblebeee_.adventuremaps.MapManager;
import me.bumblebeee_.adventuremaps.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!MapManager.players.containsKey(e.getPlayer()))
            return;

        Player p = e.getPlayer();
        p.getInventory().setContents(PlayerStorage.invs.get(p.getUniqueId()));
        p.teleport(PlayerStorage.locs.get(p.getUniqueId()));
        AdventureMap map = MapManager.players.get(p);
        PlayerStorage.invs.remove(p.getUniqueId());
        PlayerStorage.locs.remove(p.getUniqueId());
        MapManager.players.remove(p);

        if (map.getType().equalsIgnoreCase("single")) {
            map.cleanup();
        } else {
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

}