package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;

public class MinecraftMessageHandler {

    private static boolean mc2dc;

    public static void init() {
        mc2dc = true;
    }

    public void handle(Player player, String msg) {

        String explosion = "tellraw @a {\"text\":\"[%s]\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:\\\"minecraft:%s\\\",Damage:0,Count:1,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}}}\"}}";
        // String.format(explosion,displayName,type,displayName,finalLore.toString())
        if (msg.contains("[i]")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            String type = item.getType().toString();
            String displayName = item.getItemMeta().getDisplayName();
            List<String> lore = item.getItemMeta().getLore();

            StringBuilder finalLore = new StringBuilder();
            Iterator iter = lore.iterator();
            while (iter.hasNext()) {
                String str = (String) iter.next();
                finalLore.append("[\\\"").append(str).append("\\\"]"); //[\"上面\"],[\"下面\"]
                if (iter.hasNext()) finalLore.append(",");
            }
            Main.log("Final Lore: " + finalLore.toString());

            System.out.printf(explosion, displayName, type, displayName, finalLore);
        }


        if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(msg));
    }

    public boolean ismc2dc() {
        return mc2dc;
    }

    public void setMc2dc(boolean mc2dc) {
        MinecraftMessageHandler.mc2dc = mc2dc;
    }

}