package me.bumblebeee_.adventuremaps.commands;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.bumblebeee_.adventuremaps.AdventureMap;
import me.bumblebeee_.adventuremaps.MapManager;
import me.bumblebeee_.adventuremaps.Maps;
import me.bumblebeee_.adventuremaps.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                for (String c : Maps.cmds.keySet()) {
                    sender.sendMessage(m.getMessage("helpMessage").replace("%cmd%", c).replace("%usage%",
                            Maps.cmds.get(c)));
                }
                return true;
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
                    cmds.append(args[i]).append(i + 1 == args.length ? "" : " ");
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
                    cmds.append(args[i]).append(i + 1 == args.length ? "" : " ");
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
                    mapName.append(args[i]).append(i + 1 == args.length ? "" : " ");
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
            } else if (args[0].equalsIgnoreCase("setlimit")) {
                if (!sender.hasPermission("maps.setlimit")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (!(args.length > 2)) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps setlimit <name> <amount>"));
                    return false;
                }

                String name = args[1];
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a number!");
                    return false;
                }

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                map.setLimit(name, amount);
                sender.sendMessage(m.getMessage("limitSuccessSet"));
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("maps.ready")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                Set<String> maps = map.getAllMaps();
                if (maps == null) {
                    sender.sendMessage(m.getMessage("noMapsFound"));
                    return true;
                } else {
                    sender.sendMessage(m.getMessage("foundMaps"));
                    for (String ma : map.getAllMaps()) {
                        sender.sendMessage(m.getMessage("mapsFound").replace("%name%", ma));
                    }
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("settime")) {
                if (!sender.hasPermission("maps.settime")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (!(args.length > 2)) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps setlimit <name> <minutes>"));
                    return false;
                }

                String name = args[1];
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a number!");
                    return false;
                }

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                map.setTimeLimit(name, amount);
                sender.sendMessage(m.getMessage("timeSuccessSet"));
                return true;
            } else if (args[0].equalsIgnoreCase("setplayerlimit")) {
                if (!sender.hasPermission("maps.setplayerlimit")) {
                    sender.sendMessage(m.getMessage("noPermissions"));
                    return false;
                }

                if (!(args.length > 2)) {
                    sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps setplayerlimit <name> <limit>"));
                    return false;
                }

                String name = args[1];
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a number!");
                    return false;
                }

                if (!map.mapExists(name)) {
                    sender.sendMessage(m.getMessage("mapNotExists").replace("%name%", name));
                    return false;
                }

                map.setPlayerLimit(name, amount);
                sender.sendMessage(m.getMessage("playerLimitSuccessSet"));
                return true;
            } else {
                sender.sendMessage(m.getMessage("invalidArgs").replace("%usage%", "/maps help"));
                return false;
            }
        }
        return false;
    }
}
