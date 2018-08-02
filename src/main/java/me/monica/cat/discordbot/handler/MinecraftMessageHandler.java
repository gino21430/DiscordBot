package me.monica.cat.discordbot.handler;

import me.monica.cat.discordbot.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class MinecraftMessageHandler {

    private static boolean mc2dc;

    public static void init() {
        mc2dc = true;
    }

    public void handle(Player player, String msg) {
        if (msg.contains("[i]")) {
            StringBuilder tellraw = new StringBuilder();
            tellraw.append("tellraw @a [");
            String[] pure = msg.split("\\[i]");
            for (int i = 0, len = pure.length; i < len; i++) {
                tellraw.append(dealPureString(pure[i]));
                tellraw.append(dealItem(player.getInventory().getItemInMainHand()));
                if (i + 1 < len) tellraw.append(",");
            }
            tellraw.append("]");
            Main.log("tellraw: " + tellraw.toString());
            getServer().dispatchCommand(getServer().getConsoleSender(), tellraw.toString());
            if (mc2dc) {
                String displayName;
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                if (meta.hasDisplayName()) displayName = meta.getDisplayName();
                else displayName = player.getInventory().getItemInMainHand().getType().toString();
                Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(msg.replace("[i]", "[" + displayName + "]")));
            }
        } else if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(msg));
    }

    private String dealPureString(String msg) {
        return String.format("{\"text\":\"\",\"extra\":[{\"text\":\"%s\"}]}", msg);
    }

    private String dealItem(ItemStack item) {
        int amount = item.getAmount();
        ItemMeta meta = item.getItemMeta();
        String type = item.getType().toString();
        String displayName;
        StringBuilder finalLore = new StringBuilder();
        if (meta.hasDisplayName()) displayName = meta.getDisplayName();
        else displayName = type;

        if (meta.hasLore()) {
            List<String> loreList = meta.getLore();
            Iterator iter = loreList.iterator();
            while (iter.hasNext()) {
                finalLore.append("\\\"").append(iter.next()).append("\\\"");
                if (iter.hasNext()) finalLore.append(",");
            }
        }
        String itemSection = "{\"text\":\"\",\"extra\":[{\"text\":\"[%s]\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:\\\"minecraft:%s\\\",Damage:0,Count:%d,tag:{display:{Name:\\\"%s\\\",Lore:\\\"[%s]\\\"}}}\"}}]}";
        return String.format(itemSection, displayName, type, amount, displayName, finalLore);
    }

}
