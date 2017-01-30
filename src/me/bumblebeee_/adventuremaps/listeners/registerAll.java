package me.bumblebeee_.adventuremaps.listeners;

import me.bumblebeee_.adventuremaps.Maps;
import org.bukkit.Bukkit;

public class registerAll {

    public static void register() {
        Bukkit.getServer().getPluginManager().registerEvents(new SignChange(), Maps.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new MoveEvent(), Maps.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new InteractEvent(), Maps.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuit(), Maps.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), Maps.getInstance());
    }

}
