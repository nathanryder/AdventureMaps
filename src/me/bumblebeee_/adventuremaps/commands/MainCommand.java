package me.bumblebeee_.adventuremaps.commands;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.bumblebeee_.adventuremaps.MapManager;
import me.bumblebeee_.adventuremaps.Maps;
import me.bumblebeee_.adventuremaps.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    Messages m = new Messages();
    MapManager map = new MapManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("maps")) {
            if (args.length < 1) {
                sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps help"));
                return false;
            }

            if (args[0].equalsIgnoreCase("help")) {
                //TODO
            } else if (args[0].equalsIgnoreCase("create")) {
                if (!sender.hasPermission("maps.create")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps create <name>"));
                    return false;
                }

                String name = args[1];

                if (map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapExists").replace("%name%", name));
                    return false;
                }

                map.createMap(name);
                sender.sendMessage(m.getMessage("mapCreateSuccess").replace("%name%", name));
                return true;
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;

                if (!sender.hasPermission("maps.setspawn")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps setspawn <name>"));
                    return false;
                }

                String name = args[1];

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                map.setSpawn(name, p.getLocation());
                p.sendMessage(m.getMessage("setspawnSuccess"));
                return true;
            } else if (args[0].equalsIgnoreCase("addreward")) {
                if (!sender.hasPermission("maps.addreward")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (!(args.length > 2)) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps addreward <name> <command>"));
                    return false;
                }

                String name = args[1];
                StringBuilder cmds = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    cmds.append(args[i]).append(i+1==args.length ? "" : " ");
                }

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                map.addRewardCommand(name, cmds.toString());
                sender.sendMessage(m.getMessage("rewardAddSuccess"));
                return true;
            } else if (args[0].equalsIgnoreCase("delreward")) {
                if (!sender.hasPermission("maps.delreward")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (!(args.length > 2)) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps delreward <name> <command>"));
                    return false;
                }

                String name = args[1];
                StringBuilder cmds = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    cmds.append(args[i]).append(i+1==args.length ? "" : " ");
                }

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                map.delRewardCommand(name, cmds.toString());
                sender.sendMessage(m.getMessage("rewardDelSuccess"));
                return true;
            } else if (args[0].equalsIgnoreCase("setend")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;

                if (!sender.hasPermission("maps.setend")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps setspawn <name>"));
                    return false;
                }
                String name = args[1];

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                Selection sel = Maps.getWorldEdit().getSelection(p);

                if (sel == null) {
                    p.sendMessage(m.getMessage("noWESelection"));
                    return false;
                }

                if (!(sel instanceof CuboidSelection)) {
                    p.sendMessage(ChatColor.RED + "Your selection isn't a cuboid.");
                    return false;
                }

                Location min = sel.getMinimumPoint();
                Location max = sel.getMaximumPoint();

                map.setEndPoint(name, min, max);
                p.sendMessage(m.getMessage("setEndPoint"));
                return true;
            } else if (args[0].equalsIgnoreCase("setmap")) {
                if (!sender.hasPermission("maps.setmap")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (!(args.length > 2)) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps setmap <name> <map>"));
                    return false;
                }

                String name = args[1];
                StringBuilder mapName = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    mapName.append(args[i]).append(i+1==args.length ? "" : " ");
                }

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                if (!map.mapDirExists(mapName.toString())) {
                    sender.sendMessage(m.getMessage("mapDirDoesntExist").replace("%name%", mapName.toString()));
                    return false;
                }

                map.setMap(name, mapName.toString());
                sender.sendMessage(m.getMessage("mapDirSetSuccess"));
                return true;
            } else if (args[0].equalsIgnoreCase("ready")) {
                if (!sender.hasPermission("maps.ready")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps ready <name>"));
                    return false;
                }
                String name = args[1];

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                boolean ready = map.setReady(name);
                if (!ready) {
                    sender.sendMessage(m.getMessage("failedToSetup"));
                    return false;
                }

                sender.sendMessage(m.getMessage("setupSuccess"));
                return true;
            }
        }
        return false;
    }
}
