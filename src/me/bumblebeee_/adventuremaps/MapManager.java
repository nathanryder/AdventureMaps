package me.bumblebeee_.adventuremaps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapManager {

    static File f;
    static YamlConfiguration c;
    public static HashMap<Player, AdventureMap> players = new HashMap<>();
    public static HashMap<String, Integer> amounts = new HashMap<>();
    public static ArrayList<Sign> signs = new ArrayList<>();
    public static ArrayList<AdventureMap> waiting = new ArrayList<>();

    public void createMap(String name) {
        c.set("maps." + name.toLowerCase() + ".name", name);
        c.set("maps." + name.toLowerCase() + ".ready", false);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSpawn(String name, Location l) {
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        double yaw = l.getYaw();
        double pitch = l.getPitch();

        c.set("maps." + name.toLowerCase() + ".spawn.x", x);
        c.set("maps." + name.toLowerCase() + ".spawn.y", y);
        c.set("maps." + name.toLowerCase() + ".spawn.z", z);
        c.set("maps." + name.toLowerCase() + ".spawn.pitch", pitch);
        c.set("maps." + name.toLowerCase() + ".spawn.yaw", yaw);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getAllMaps() {
        ConfigurationSection cs = c.getConfigurationSection("maps");
        if (cs == null)
            return null;
        return cs.getKeys(false);
    }

    public void setEndPoint(String name, Location l1, Location l2) {
        int x1 = l1.getBlockX();
        int y1 = l1.getBlockY();
        int z1 = l1.getBlockZ();

        int x2 = l2.getBlockX();
        int y2 = l2.getBlockY();
        int z2 = l2.getBlockZ();

        c.set("maps." + name.toLowerCase() + ".end.one.x", x1);
        c.set("maps." + name.toLowerCase() + ".end.one.y", y1);
        c.set("maps." + name.toLowerCase() + ".end.one.z", z1);
        c.set("maps." + name.toLowerCase() + ".end.two.x", x2);
        c.set("maps." + name.toLowerCase() + ".end.two.y", y2);
        c.set("maps." + name.toLowerCase() + ".end.two.z", z2);

        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRewardCommand(String name, String cmd) {
        List<String> fCmds = new ArrayList<>();
        List<String> cmds = c.getStringList("maps." + name.toLowerCase() + ".rewards");
        if (cmds == null) {
            fCmds.add(cmd);
        } else if (!cmds.isEmpty()) {
            fCmds.add(cmd);
        } else {
            fCmds.addAll(cmds);
            fCmds.add(cmd);
        }

        c.set("maps." + name.toLowerCase() + ".rewards", fCmds);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delRewardCommand(String name, String cmd) {
        List<String> cmds = c.getStringList("maps." + name.toLowerCase() + ".rewards");
        if (cmds == null || cmds.isEmpty())
            return false;
        if (!cmds.contains(cmd))
            return false;

        cmds.remove(cmd);
        c.set("maps." + name.toLowerCase() + ".rewards", cmds);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setMap(String name, String mapName) {
        c.set("maps." + name.toLowerCase() + ".map", mapName);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getRewards(String name) {
        name = ChatColor.stripColor(name).toLowerCase();
        return c.getStringList("maps." + name + ".rewards");
    }

    public String getMapName(String name) {
        return c.getString("maps." + name.toLowerCase() + ".map");
    }


    public boolean mapExists(String name) {
        name = ChatColor.stripColor(name);
        String mapN = c.getString("maps." + name.toLowerCase() + ".name");
        if (mapN == null)
            return false;
        if (mapN.equalsIgnoreCase(name))
            return true;
        return false;
    }

    public boolean mapDirExists(String dirName) {
        File f = new File(Maps.getInstance().getDataFolder() + File.separator + "maps" + File.separator + dirName);
        if (f.isDirectory())
            return true;
        return false;
    }

    public void setSignStatus(Sign s, String status) {
        s.setLine(3, ChatColor.translateAlternateColorCodes('&', status));
        s.update();
    }

    public boolean setReady(String name) {
        name = name.toLowerCase();
        String n = c.getString("maps." + name + ".name");
        int spawnX = c.getInt("maps." + name + ".spawn.x");
        int endX1 = c.getInt("maps." + name + ".end.one.x");
        List<String> rewards = c.getStringList("maps." + name + ".rewards");
        String mapN = c.getString("maps." + name + ".map");

        if (n == null || rewards.isEmpty())
            return false;

        c.set("maps." + name + ".ready", true);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setLimit(String name, int amount) {
        c.set("maps." + name.toLowerCase() + ".limit", amount);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTimeLimit(String name, int amount) {
        c.set("maps." + name.toLowerCase() + ".timeLimit", amount);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getLimit(String name) {
        return  c.getInt("maps." + name.toLowerCase() + ".limit");
    }

    public boolean isReady(String name) {
        return c.getBoolean("maps." + name.toLowerCase() + ".ready");
    }

    public void setup() {
        Plugin pl = Maps.getInstance();
        File f = new File(pl.getDataFolder() + File.separator + "maps.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                pl.getLogger().severe("[Maps] Failed to create maps.yml");
                pl.getLogger().severe("[Maps] Disabling plugin..");
                pl.getPluginLoader().disablePlugin(pl);
            }
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

        MapManager.f = f;
        MapManager.c = c;
    }

    public void setPlayerLimit(String name, int amount) {
        c.set("maps." + name.toLowerCase() + ".playerLimit", amount);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}