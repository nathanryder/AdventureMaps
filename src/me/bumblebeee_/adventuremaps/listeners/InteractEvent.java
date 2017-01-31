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
import org.bukkit.inventory.ItemStack;

public class InteractEvent implements Listener {

    PlayerStorage ps = new PlayerStorage();
    Messages m = new Messages();
    MapManager mm = new MapManager();

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if (p.getInventory().getItemInMainHand() != null) {
            if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                if (!p.getInventory().getItemInMainHand().hasItemMeta()) return;
                if (!p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;
                ItemStack i = p.getInventory().getItemInMainHand();
                if (ChatColor.stripColor(i.getItemMeta().getDisplayName()).equalsIgnoreCase("Leave")) {
                    if (!MapManager.players.containsKey(p)) return;
                    AdventureMap map = MapManager.players.get(p);
                    p.getInventory().clear();
                    ps.loadPlayer(p);

                    if (MapManager.amounts.containsKey(map.getName())) {
                        int amount = MapManager.amounts.get(map.getName());
                        MapManager.amounts.remove(map.getName());
                        if (amount > 1) {
                            MapManager.amounts.put(map.getName(), amount - 1);
                        }
                    }

                    for (Sign s : MapManager.signs) {
                        int limit = mm.getLimit(map.getName());
                        int amount = 0;
                        if (MapManager.amounts.containsKey(map.getName())) {
                            amount = MapManager.amounts.get(map.getName());
                        }

                        if (s.getLine(2).equalsIgnoreCase(map.getName())) {
                            if (s.getLine(1).contains(map.getType())) {
                                if (amount < limit) {
                                    mm.setSignStatus(s, "&b&l<Click to join!>");
                                }
                            }
                        }
                    }

                    map.getPlayers().remove(p);
                    map.setPlaying(map.getPlaying() - 1);
                    MapManager.players.remove(p);
                    return;
                }
            }
        }
        //TODO test leaving and end zone for multiplayer


        if (b == null)
            return;
        if (b.getType() == Material.AIR)
            return;

        if (!(b.getState() instanceof Sign))
            return;

        Sign s = (Sign) b.getState();

        if (!ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[Adventure]"))
            return;

        e.setCancelled(true);
        String type = s.getLine(1);
        String arena = s.getLine(2);

        if (MapManager.players.containsKey(p)) {
            p.sendMessage(m.getMessage("alreadyIngame"));
            return;
        }

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
        } else if (type.toLowerCase().contains("multi")) {
            AdventureMap map;
            if (MapManager.waiting.size() > 0) {
                map = MapManager.waiting.get(0);
            } else {
                map = new AdventureMap(arena, "multi");
                if (!map.verify()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed! Please make sure the map is fully set up!"));
                    return;
                }
                map.load();
            }

            int playing = map.getPlaying();
            int playerLimit = map.getPlayerLimit();
            if (playing >= playerLimit)
                return;

            ps.savePlayer(p);
            p.getInventory().clear();

            p.sendMessage(m.getMessage("waitWhileLoading"));
            p.sendMessage(m.getMessage("waitForOtherPlayers"));
            map.addPlayer(p);

            if (MapManager.waiting.size() > 0) {
                AdventureMap m = MapManager.waiting.get(0);
                playing = m.getPlaying();
                playerLimit = m.getPlayerLimit();
                if (playing < playerLimit) {
                    s.setLine(3, ChatColor.translateAlternateColorCodes('&', "&2&l" + playing + "/" + playerLimit));
                    s.update();
                }
            } else {
                s.setLine(3, ChatColor.translateAlternateColorCodes('&', "&2&l<Click to join!>"));
                s.update();
            }
        }
    }

}