package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.handler.DiscordMessageHandler;
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
