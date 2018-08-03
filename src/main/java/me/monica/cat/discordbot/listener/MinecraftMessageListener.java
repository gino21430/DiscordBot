package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.handler.MinecraftMessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MinecraftMessageListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        String messsge = e.getMessage();
        MinecraftMessageHandler minecraftMessageHandler = new MinecraftMessageHandler();
        e.setCancelled(minecraftMessageHandler.handle(e.getPlayer(), messsge));
    }
}
