package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.Main;
import net.dv8tion.jda.core.managers.GuildController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MinecraftBanPlayerListener implements Listener {

    @EventHandler
    public void onBanPlayer(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage();
        if (msg.startsWith("/ban")) {
            Main.getPlugin().unlink(e.getPlayer().getUniqueId().toString());
            //TODO addRole "Bang"
        } else if (msg.startsWith("/pardon")) {
            GuildController gc = new GuildController(Main.getPlugin().getGuild());
            //TODO unban
        }

    }
}