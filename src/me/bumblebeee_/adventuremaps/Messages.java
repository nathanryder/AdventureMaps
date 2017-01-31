package me.bumblebeee_.adventuremaps;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Messages {

    static File f;
    static YamlConfiguration c;

    public void setup() {
        if (!Maps.getInstance().getDataFolder().exists())
            Maps.getInstance().getDataFolder().mkdirs();

        Plugin pl = Maps.getInstance();
        File f = new File(pl.getDataFolder() + File.separator + "messages.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                pl.getLogger().severe("[Maps] Failed to create messages.yml");
                pl.getLogger().severe("[Maps] Disabling plugin..");
                pl.getPluginLoader().disablePlugin(pl);
            }
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

        Messages.f = f;
        Messages.c = c;

        createMessage("noPermissions", "&cYou do not have the required permissions!");
        createMessage("invalidArgs", "&cInvalid arguments! Correct usage: %usage%");
        createMessage("mapExists", "&cA map named %name% already exists!");
        createMessage("mapCreateSuccess", "&aSuccessfully created a map named %name%. You can now begin setting it up.");
        createMessage("mapNotExists", "&cFailed to find a map called %name%");
        createMessage("setspawnSuccess", "&aSuccessfully set spawn!");
        createMessage("rewardAddSuccess", "&aSuccessfully added new reward command!");
        createMessage("rewardDelSuccess", "&aSuccessfully removed reward command!");
        createMessage("noWESelection", "&cYou must make a selection with WorldEdit first!");
        createMessage("setEndPoint", "&aSuccessfully created end area!");
        createMessage("mapDirDoesntExist", "&cFailed to find a folder called %name%");
        createMessage("mapDirSetSuccess", "&aSuccessfully set map folder!");
        createMessage("failedToSetup", "&cFailed to set map as ready!");
        createMessage("setupSuccess", "&aSuccessfully setup map!");
        createMessage("waitWhileLoading", "&aPlease wait while a map is loaded..");
        createMessage("limitReached", "&cThere are too many maps running, please wait..");
        createMessage("limitSuccessSet", "&aSuccessfully set map limit!");
        createMessage("timeSuccessSet", "&aSuccessfully set time limit!");
        createMessage("noMapsFound", "&cThere is no maps yet!");
        createMessage("foundMaps", "&6The follow maps exists:");
        createMessage("mapsFound", "&6%name%");
        createMessage("helpMessage", "&6%cmd%&f: %usage%");
    }

    public String getMessage(String key) {
        String v = c.getString(key);
        if (v == null)
            return null;
        return ChatColor.translateAlternateColorCodes('&', v);
    }

    public void createMessage(String key, String value) {
        c.set(key, value);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
