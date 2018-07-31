package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import org.bukkit.entity.Player;

import java.util.*;

public class MinecraftMessageHandler {

    private List<String> muteList = new ArrayList<>();
    private boolean mc2dc = true;

    public void handle(String msg) {
        if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(msg);
    }

    public boolean ismc2dc() {
        return mc2dc;
    }

    public void setMc2dc(boolean mc2dc) {
        this.mc2dc = mc2dc;
    }

}