package me.monica.cat.discordbot.listener;

import me.monica.cat.discordbot.Main;
import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MinecraftPlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Main.getPlugin().detectNameChanged(player);
        DiscordMessageHandler dmh = new DiscordMessageHandler();
        dmh.unmute(player);
        player.sendMessage("可透過\"/dc mute\"決定是否屏蔽來自Discord的訊息");
    }
}