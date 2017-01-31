package me.bumblebeee_.adventuremaps;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdventureMap {

    MapManager mm = new MapManager();
    PlayerStorage ps = new PlayerStorage();
    SecureRandom rnd = new SecureRandom();


    File f;
    YamlConfiguration c;

    String name;
    String type;
    List<Player> players = new ArrayList<>();

    String worldName;
    File mapCOPY;
    World w;

    int playing = 0;
    int playerLimit;

    public AdventureMap(String arena, String type) {
        this.name = ChatColor.stripColor(arena);
        this.type = type;

        File f = new File(Maps.getInstance().getDataFolder() + File.separator + "maps.yml");
        if (!f.exists()) {
            Maps.getInstance().getLogger().severe("[AdventureMaps] Failed to load maps.yml");
            Maps.getInstance().getLogger().severe("[AdventureMaps] Disabling plugin..");
            Maps.getInstance().getPluginLoader().disablePlugin(Maps.getInstance());
            return;
        }
        this.f = f;
        this.c = YamlConfiguration.loadConfiguration(f);

        if (type.equalsIgnoreCase("multi")) {
            playerLimit = c.getInt("maps." + name.toLowerCase() + ".playerLimit");
            MapManager.waiting.add(this);
        }
    }

    public void start() {
        for (Player p : players) {
            String title = ChatColor.translateAlternateColorCodes('&', Maps.getInstance().getConfig().getString("startTitle.title"));
            String subtitle = ChatColor.translateAlternateColorCodes('&', Maps.getInstance().getConfig().getString("startTitle.subtitle"));
            p.sendTitle(title, subtitle, 20, 40, 20);
        }

        if (MapManager.amounts.containsKey(name)) {
            int amount = MapManager.amounts.get(name);
            MapManager.amounts.remove(name);
            MapManager.amounts.put(name, amount+1);
        } else {
            MapManager.amounts.put(name, 1);
        }

        for (Sign s : MapManager.signs) {
            int limit = mm.getLimit(name);
            int amount = 0;
            if (MapManager.amounts.containsKey(name)) {
                amount = MapManager.amounts.get(name);
            }

            if (s.getLine(2).equalsIgnoreCase(name)) {
                if (s.getLine(1).contains(type)) {
                    if (amount >= limit) {
                        mm.setSignStatus(s, "&b&lMap Limit Reached!");
                    }
                }
            }
        }

        int time = c.getInt("maps." + name.toLowerCase() + ".timeLimit");
        if (time != 0) {
            startTimer(time);
        }
    }

    public void end(boolean reward) {
        for (Player p : players) {
            p.getInventory().clear();
            ps.loadPlayer(p);
            String title = ChatColor.translateAlternateColorCodes('&', Maps.getInstance().getConfig().getString("endTitle.title"));
            String subtitle = ChatColor.translateAlternateColorCodes('&', Maps.getInstance().getConfig().getString("endTitle.subtitle"));
            p.sendTitle(title, subtitle, 20, 40, 20);
        }
        if (reward) {
            List<String> rewards = mm.getRewards(name);
            for (String cmd : rewards) {
                if (cmd.contains("%player%")) {
                    for (Player p : players) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("%player%", p.getName()));
                    }
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
                }
            }
        }

        players.clear();
        cleanup();

        if (MapManager.amounts.containsKey(name)) {
            int amount = MapManager.amounts.get(name);
            MapManager.amounts.remove(name);
            if (amount > 1) {
                MapManager.amounts.put(name, amount-1);
            }
        }

        for (Sign s : MapManager.signs) {
            int limit = mm.getLimit(name);
            int amount = 0;
            if (MapManager.amounts.containsKey(name)) {
                amount = MapManager.amounts.get(name);
            }

            if (s.getLine(2).equalsIgnoreCase(name)) {
                if (s.getLine(1).contains(type)) {
                    if (amount < limit) {
                        mm.setSignStatus(s, "&b&l<Click to join!>");
                    }
                }
            }
        }
    }

    public void startTimer(final int time) {
        Bukkit.getServer().getScheduler().runTaskLater(Maps.getInstance(), new Runnable() {
            @Override
            public void run() {
                end(false);
            }
        }, time*1200);
    }

    public void cleanup() {
        for (Player p : w.getPlayers())
            p.performCommand("spawn");
        Bukkit.getServer().unloadWorld(w, false);
        deleteMap(w.getWorldFolder());

        HashMap<Player, AdventureMap> iterator = (HashMap<Player, AdventureMap>) MapManager.players.clone();
        for (Player p : iterator.keySet()) {
            if (iterator.get(p) == this) {
                MapManager.players.remove(p);
            }
        }
        MapManager.amounts.remove(name);
    }

    private void deleteMap(File dir) {
        File[] files = dir.listFiles();

        for(File file : files) {
            if(file.isDirectory()) {
                this.deleteMap(file);
                file.delete();
            } else {
                file.delete();
            }
        }
        dir.delete();
    }

    public String getType() {
        return type;
    }

    public String getName() { return name; }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean verify() {
        if (!mm.mapExists(name))
            return false;
        if (!mm.isReady(name))
            return false;


        String mapName = mm.getMapName(name);
        File mapC = new File(Maps.getInstance().getDataFolder() + File.separator + "maps" + File.separator + mapName);
        if (!mapC.isDirectory())
            return false;

        mapCOPY = mapC;
        return true;
    }

    public void load() {
        worldName = randomString(10);
        File dest = new File(Maps.getInstance().getServer().getWorldContainer().getAbsoluteFile() + File.separator + worldName);
        try {
            FileUtils.copyDirectory(mapCOPY, dest);
        } catch (IOException e) {
            Maps.getInstance().getLogger().severe("[AdventureMaps] Failed to copy folder");
            Maps.getInstance().getLogger().severe("[AdventureMaps] Disabling plugin..");
            Maps.getInstance().getPluginLoader().disablePlugin(Maps.getInstance());
        }

        World w = Bukkit.getServer().createWorld(new WorldCreator(worldName));
        w.setAutoSave(false);
        this.w = w;
    }

    public void addPlayer(Player p) {
        players.add(p);
        MapManager.players.put(p, this);
        if (type.contains("single")) {
            teleportToSpawn(p);
            start();
        } else {
            playing++;
            if (playing == playerLimit) {
                for (Player t : players) {
                    teleportToSpawn(t);
                }
                start();
                MapManager.waiting.remove(this);
            }
        }

        ItemStack leave = new ItemStack(Material.BARRIER);
        ItemMeta lm = leave.getItemMeta();
        lm.setDisplayName(ChatColor.RED + "Leave");
        leave.setItemMeta(lm);
        p.getInventory().setItem(8, leave);
    }

    public void setPlaying(int playing) {
        this.playing = playing;
    }

    public void teleportToSpawn(Player p) {
        int x = c.getInt("maps." + name.toLowerCase() + ".spawn.x");
        int y = c.getInt("maps." + name.toLowerCase() + ".spawn.y");
        int z = c.getInt("maps." + name.toLowerCase() + ".spawn.z");
        int pitch = c.getInt("maps." + name.toLowerCase() + ".spawn.pitch");
        int yaw = c.getInt("maps." + name.toLowerCase() + ".spawn.yaw");

        p.teleport(new Location(w, x, y, z, yaw, pitch));
    }

    public Location getEndPointOne() {
        int x1 = c.getInt("maps." + name.toLowerCase() + ".end.one.x");
        int y1 = c.getInt("maps." + name.toLowerCase() + ".end.one.y");
        int z1 = c.getInt("maps." + name.toLowerCase() + ".end.one.z");
        return new Location(w, x1, y1, z1);
    }

    public Location getEndPointTwo() {
        int x2 = c.getInt("maps." + name.toLowerCase() + ".end.two.x");
        int y2 = c.getInt("maps." + name.toLowerCase() + ".end.two.y");
        int z2 = c.getInt("maps." + name.toLowerCase() + ".end.two.z");
        return new Location(w, x2, y2, z2);
    }

    public int getPlaying() { return playing; }

    public int getPlayerLimit() { return playerLimit; }


    String randomString( int len ){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }



}
