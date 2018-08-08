package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.Main;
import me.monica.cat.discordbot.handler.MinecraftMessageHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;

public class MinecraftMessageListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        String messsge = e.getMessage();
        MinecraftMessageHandler minecraftMessageHandler = new MinecraftMessageHandler();
        File file = new File(Main.getPlugin().getDataFolder().getParentFile().getPath() + "\\players\\" + e.getPlayer().getName() + ".yml");
        if (YamlConfiguration.loadConfiguration(file).getString("高級會員").equals("無"))
            e.setMessage(ChatColor.stripColor(messsge));
        e.setCancelled(minecraftMessageHandler.handle(e.getPlayer(), messsge));
    }
}
