package me.monica.cat.discordbot.handler;

import me.monica.cat.discordbot.Main;
import me.monica.cat.discordbot.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class MinecraftMessageHandler {

    private static boolean mc2dc;
    private static FileConfiguration OPNickname;

    public static void init() {
        mc2dc = true;
        ConfigUtil configUtil = new ConfigUtil();
        OPNickname = configUtil.loadConfig("OPNickname.yml");
    }

    public void save() {
        try {
            OPNickname.save(new File(Main.getPlugin().getDataFolder(),"OPNickname.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean handle(Player player, String msg) {
        if (msg.startsWith("@") || msg.startsWith("#")) return false;
        String prefix = handlePrefix(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        if (msg.contains("[i]") && item.getType() != Material.AIR) {
            Main.log(player.getName()+" tell a Item");
            StringBuilder tellraw = new StringBuilder();
            tellraw.append("tellraw @a [");

            String[] pure;

            // 處理物品
            if (msg.equals("[i]")) {
                tellraw.append(dealItem(item));
            } else {
                Main.log("BEFORE msg: \"" + msg + "\"");
                if (msg.endsWith("[i]")) msg += " ";
                if (msg.startsWith("[i]")) msg = " " + msg;
                Main.log("AFTER msg: \"" + msg + "\"");
                pure = msg.split("\\[i]");
                tellraw.append(dealPureString(prefix)).append(",");
                for (int i = 0, len = pure.length; i < len; i++) {
                    Main.log("pure[" + i + "]: \"" + pure[i] + "\"");
                    if (!pure[i].equals(" ")) {
                        tellraw.append(dealPureString(pure[i]));
                        if (i + 1 < len) { //後面還有咚咚
                            tellraw.append(",").append(dealItem(item));
                            if (!pure[i + 1].equals(" ")) //後面還有字串
                                tellraw.append(",");
                        }
                    } else { //現在是" "
                        if (i + 1 < len) { //是第一個，後面還有咚咚
                            Main.log("pure[i+1]: \"" + pure[i + 1] + "\"");
                            tellraw.append(dealItem(item));
                            if (!pure[i + 1].equals(" ")) //後面還有字串
                                tellraw.append(",");
                        }
                    }
                }
            }
            tellraw.append("]");
            Main.log("TELLRAW: " + tellraw.toString());
            getServer().dispatchCommand(getServer().getConsoleSender(), tellraw.toString());
            String displayName;
            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
                displayName = item.getType().toString();
            else displayName = item.getItemMeta().getDisplayName();
            msg = msg.replaceAll("\\[i]", "[" + displayName + "]");
            if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(msg));
        } else { //一般對話
            Main.getPlugin().toBroadcastToMinecraft(prefix+msg);
            if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(prefix+msg));
        }
        return true;
    }

    private String dealPureString(String msg) {
        return String.format("{\"text\":\"\",\"extra\":[{\"text\":\"%s\"}]}", msg);
    }

    private String dealItem(ItemStack item) {
        String displayName;
        String type = item.getType().toString();
        int amount = item.getAmount();
        StringBuilder finalLore = new StringBuilder();
        if (!item.hasItemMeta()) {
            Main.log("There is no ItemMeta");
            displayName = type;
            amount = 1;
            finalLore.append("\\\"").append("NO LORE").append("\\\"");
        } else {
            ItemMeta meta = item.getItemMeta();
            type = item.getType().toString();
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
        }
        String itemSection = "{\"text\":\"\",\"extra\":[{\"text\":\"[%s x %d]\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:\\\"minecraft:%s\\\",Damage:0,Count:%d,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}}}\"}}]}";
        return String.format(itemSection, displayName, amount, type, amount, displayName, finalLore);
    }

    private String handlePrefix(Player player) {
        if (player.isOp()) {
            String nickname = OPNickname.getString(player.getUniqueId().toString());
            if (nickname==null) nickname = "管理人員";
            return "§8[§c管理§8]§r " + ChatColor.translateAlternateColorCodes('&', nickname) + "§r §7-§r §c" + player.getName() + "§r > ";
        }
        try {
            File file = new File(Main.getPlugin().getDataFolder().getParentFile().getCanonicalPath() + "\\players\\" + player.getName() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String career = config.getString("玩家職業");
            int level = config.getInt("玩家等級");
            if (!config.getString("高級會員").equals("無"))
                return "§8[§e" + career + "§8] §eLv." + level + "§r §7-§r §b" + player.getName() + "§r > ";
            return "§8[§e" + career + "§8] §eLv." + level + "§r §7-§r §c" + player.getName() + "§r > ";
        } catch (IOException e) {
            e.printStackTrace();
            return "§l§c[Prefix錯誤]§r > ";
        }
    }

    public void setOPNickname(Player player, String nickname) {
        OPNickname.set(player.getUniqueId().toString(),nickname);
    }
}
