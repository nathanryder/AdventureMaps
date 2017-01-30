package me.bumblebeee_.adventuremaps.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (!e.getLine(0).equalsIgnoreCase("[Adventure]"))
            return;
        if (e.getLine(1).isEmpty())
            return;
        if (e.getLine(2).isEmpty())
            return;

        e.setLine(0, ChatColor.AQUA + "[Adventure]");
        e.setLine(1, ChatColor.RED + e.getLine(1));
        e.setLine(2, ChatColor.GREEN + e.getLine(2));
        e.setLine(3, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "<Click to join!>");
    }

}
