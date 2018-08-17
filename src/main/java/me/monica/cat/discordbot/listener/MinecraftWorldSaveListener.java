package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class MinecraftWorldSaveListener implements Listener {

    @EventHandler
    public void onWorldSave(WorldSaveEvent e) {
        if (!e.getWorld().getName().equalsIgnoreCase("RPG")) return;
        if (Main.getPlugin().config.getBoolean("Save")) Main.getPlugin().saveConfig();
    }
}
