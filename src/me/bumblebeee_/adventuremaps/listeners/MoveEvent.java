package me.bumblebeee_.adventuremaps.listeners;

import me.bumblebeee_.adventuremaps.AdventureMap;
import me.bumblebeee_.adventuremaps.MapManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class MoveEvent implements Listener {

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ())
            return;
        ArrayList<Player> inside = new ArrayList<>();
        for (AdventureMap map : MapManager.players.values()) {
            Location l1 = map.getEndPointOne();
            Location l2 = map.getEndPointTwo();
            for (Player p : map.getPlayers()) {
                if (map.getType().equalsIgnoreCase("single")) {
                    if (isInside(p.getLocation(), l1, l2)) {
                        map.end(true);
                        return;
                    }
                } else {
                    if (isInside(p.getLocation(), l1, l2)) {
                        if (inside.contains(p))
                            continue;
                        inside.add(p);
                    }
                }
            }
            if (inside.size() == map.getPlayers().size()) {
                map.end(true);
                return;
            }
        }

    }

    public boolean isInside(Location loc, Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getZ() >= z1 && loc.getZ() <= z2;
    }

}