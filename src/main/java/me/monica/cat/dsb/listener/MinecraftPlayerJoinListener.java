package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.Main;
import me.monica.cat.dsb.handler.DiscordMessageHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MinecraftPlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Main.getPlugin().detectNameChanged(player);
        e.setJoinMessage("Welcome to the Summon's Rift");
        DiscordMessageHandler dmh = new DiscordMessageHandler();
        dmh.unmute(player.getUniqueId().toString());
        //minecraftMessageHandler.unmute(player.getUniqueId().toString());
    }
}