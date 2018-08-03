package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftPlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        DiscordMessageHandler dmh = new DiscordMessageHandler();
        dmh.mute(e.getPlayer().getUniqueId().toString());
    }
}
