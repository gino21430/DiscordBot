package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftMessageHandler {

    boolean mc2dc = true;
    public Map<String, Boolean> muteMap = new HashMap<>();

    public void handle(String msg) {
        Collection<? extends Player> players = Main.getPlugin().getServer().getOnlinePlayers();
        for (Player player : players) {
            if (muteMap.get(player.getUniqueId().toString())) players.remove(player);
        }
        if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(msg);
    }

}
