package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.meta.*;
import org.bukkit.command.*;

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
			ItemMeta meta = item.getItemMeta();
            String type = item.getType().toString();
			String displayName;
			if (meta.hasDisplayName()) displayName = meta.getDisplayName();
			else displayName = type;
			
			StringBuilder finalLore = new StringBuilder();
			if (meta.hasLore()) {
				List<String> loreList = meta.getLore();
				Iterator iter = loreList.iterator();
				while(iter.hasNext()) {
					finalLore.append("\\\"").append(iter.next()).append("\\\"");
					if (iter.hasNext()) finalLore.append(",");
				}
			} else finalLore.append("");
            
            Main.log("Final Lore: " + finalLore.toString());

            String tellraw = String.format(explosion, displayName, type, displayName, finalLore);
			Main.getPlugin().getServer().dispatchCommand(new ConsoleCommandSender(),tellraw);
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
