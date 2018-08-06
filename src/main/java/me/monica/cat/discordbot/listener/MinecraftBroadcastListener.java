package me.monica.cat.discordbot.listener;

import me.monica.cat.discordbot.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;

public class MinecraftBroadcastListener implements Listener {

    @EventHandler
    public void onBroadcast(BroadcastMessageEvent e) {
        Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(e.getMessage()));
    }
}
