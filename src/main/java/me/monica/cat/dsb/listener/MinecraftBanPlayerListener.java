package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.Main;
import me.monica.cat.dsb.handler.DiscordMessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MinecraftBanPlayerListener implements Listener {

    @EventHandler
    public void onBanPlayer(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage();
        if (!msg.startsWith("/ban")) return;
        Main.getPlugin().unlink(e.getPlayer().getUniqueId().toString());
    }
}