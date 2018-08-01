package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import org.bukkit.ChatColor;

public class MinecraftMessageHandler {

    private static boolean mc2dc;

    public static void init() {
        mc2dc = true;
    }

    public void handle(String msg) {
        if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(msg));
    }

    public boolean ismc2dc() {
        return mc2dc;
    }

    public void setMc2dc(boolean mc2dc) {
        MinecraftMessageHandler.mc2dc = mc2dc;
    }

}