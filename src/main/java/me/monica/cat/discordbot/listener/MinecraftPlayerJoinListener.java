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
        Main.getPlugin().toDiscordMainTextChannel("===== " + player.getName() + "進入遊戲 =====");
        DiscordMessageHandler dmh = new DiscordMessageHandler();
        dmh.unmute(player);
        player.sendMessage("[§bDiscord§r] §e可透過\"/dc mute\"屏蔽來自Discord遊戲聊天室的訊息");
    }
}