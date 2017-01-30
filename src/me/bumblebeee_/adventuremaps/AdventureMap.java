package me.bumblebeee_.adventuremaps;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
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
    }

    public void start() {
        //TODO change
        for (Player p : players) {
            p.sendTitle("Change", "Me", 20, 40, 20);
        }
    }

    public void end() {
        for (Player p : players) {
            ps.loadPlayer(p);
            p.sendTitle("Change", "Me", 20, 40, 20);
        }
        players.clear();
        List<String> rewards = mm.getRewards(name);
        for (String cmd : rewards) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
        }

        for (Player p : w.getPlayers())
            p.performCommand("spawn");
        Bukkit.getServer().unloadWorld(w, false);
        deleteMap(w.getWorldFolder());

        for (Player p : MapManager.players.keySet()) {
            if (MapManager.players.get(p) == this) {
                MapManager.players.remove(p);
            }
        }
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
        }
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


    String randomString( int len ){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }



}
