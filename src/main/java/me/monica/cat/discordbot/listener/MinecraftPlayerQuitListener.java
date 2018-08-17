package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.Main;
import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftPlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.getPlugin().toDiscordMainTextChannel("===== " + e.getPlayer().getName() + "離開遊戲 =====");
        DiscordMessageHandler dmh = new DiscordMessageHandler();
        dmh.mute(e.getPlayer());
    }
}
