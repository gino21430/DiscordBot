package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftMessageHandler implements CommandExecutor {

    private final Main plugin;

    public MinecraftMessageHandler(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    boolean mc2dc = true;
    private Map<String, Boolean> muteMap = new HashMap<>();

    public void handle(String msg) {
        Collection<? extends Player> players = Main.getPlugin().getServer().getOnlinePlayers();
        for (Player player : players) {
            if (muteMap.get(player.getUniqueId().toString())) players.remove(player);
        }
        if (mc2dc) Main.getPlugin().ToSendMessageToMultilayers(msg,players);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }
}
