package me.bumblebeee_.adventuremaps.listeners;

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
        PlayerStorage.invs.remove(p.getUniqueId());
        PlayerStorage.locs.remove(p.getUniqueId());
        MapManager.players.remove(p);

        //THIS IS WHAT WE WANT TO DO NEXT
        //TODO clean up map - Remember it might be multiplayer
    }

}