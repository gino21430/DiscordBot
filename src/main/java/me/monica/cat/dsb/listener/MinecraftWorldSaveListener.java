package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class MinecraftWorldSaveListener implements Listener {

    @EventHandler
    public void onWorldSave(WorldSaveEvent e) {
        if (Main.getPlugin().config.getBoolean("Save")) {
            Main.getPlugin().saveConfig();
            Main.getPlugin().toBroadcastToMinecraft("正在儲存設定檔...");
        }
    }
}
