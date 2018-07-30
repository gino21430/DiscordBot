package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.Main;
import me.monica.cat.dsb.handler.MinecraftMessageHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MinecraftPlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //update offlineLinkMap's name
        Player player = e.getPlayer();
        Main.getPlugin().detectNameChanged(player);
        e.setJoinMessage("Welcome to the Summon's Rift");
        MinecraftMessageHandler minecraftMessageHandler = new MinecraftMessageHandler();
        minecraftMessageHandler.muteMap.put(player.getUniqueId().toString(), false);
    }
}