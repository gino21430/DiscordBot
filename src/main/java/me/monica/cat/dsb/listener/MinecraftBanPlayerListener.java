package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MinecraftBanPlayerListener implements Listener {

    @EventHandler
    public void onBanPlayer(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage();
        if (msg.toLowerCase().startsWith("/ban")) {
            String name = msg.substring(5);
            Main.getPlugin().unlink(name, true);
        }
    }
}