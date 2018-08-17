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
            OPNickname.save(new File(Main.getPlugin().getDataFolder(), "OPNickname.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle(Player player, String msg) {
        if (msg.startsWith("@") || msg.startsWith("#") || msg.startsWith("$")) return;
        String prefix = handlePrefix(player);
        ItemStack item = player.getInventory().getItemInMainHand();

        if (msg.toLowerCase().contains("[i]") && item.getType() != Material.AIR) {
            StringBuilder tellraw = new StringBuilder();
            tellraw.append("tellraw @a [");

            String[] pure;

            // 處理物品
            if (msg.toLowerCase().equals("[i]")) {
                tellraw.append(dealPureString(prefix)).append(",").append(dealItem(item));
            } else {
                if (msg.toLowerCase().endsWith("[i]")) msg += " ";
                if (msg.toLowerCase().startsWith("[i]")) msg = " " + msg;

                msg = translateAlternateColorCodes(msg);
                if (isVip(player.getName()) && !player.isOp()) {
                    Main.log("Player:" + player.getName() + ",msg:" + msg);
                    msg = ChatColor.stripColor(msg);
                }

                pure = msg.split("\\[[iI]]");
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

            Main.log("tellraw: " + tellraw.toString());
            //getServer().dispatchCommand(getServer().getConsoleSender(), tellraw.toString());

            String displayName;
            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
                displayName = item.getType().toString();
            else displayName = item.getItemMeta().getDisplayName();
            msg = msg.replaceAll("\\[[i|I]]", "[" + displayName + "]");
            if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(prefix + msg));
        } else { //一般對話
            msg = translateAlternateColorCodes(msg);
            if (isVip(player.getName()) && !player.isOp()) {
                msg = ChatColor.stripColor(msg);
            }
            //Main.getPlugin().toBroadcastToMinecraft(prefix + msg);
            if (mc2dc) Main.getPlugin().toDiscordMainTextChannel(ChatColor.stripColor(prefix + msg));
        }
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
            displayName = type;
            amount = 1;
            finalLore.append("\\\"").append(" ").append("\\\"");
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
            } else {
                finalLore.append("\\\"").append("\\\"");
            }
        }
        String itemSection = "{\"text\":\"\",\"extra\":[{\"text\":\"[%s x %d]\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:\\\"minecraft:%s\\\",Damage:0,Count:%d,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}}}\"}}]}";
        return String.format(itemSection, displayName, amount, type, amount, displayName, finalLore);
    }

    private String handlePrefix(Player player) {
        if (player.isOp()) {
            String nickname = OPNickname.getString(player.getUniqueId().toString());
            if (nickname == null) nickname = "管管";
            return "§8[§c管理§8]§r " + translateAlternateColorCodes(nickname) + "§r §7-§r §c" + player.getName() + "§r > ";
        }
        try {
            File file = new File(Main.getPlugin().getDataFolder().getParentFile().getCanonicalPath() + "\\players\\" + player.getName() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String career = config.getString("玩家職業");
            String level = config.getString("玩家等級");
            if (!config.getString("高級會員").equals("無"))
                return "§8[§c" + career + "§8] §eLv." + level + "§r §7-§r §b" + player.getName() + "§r > ";
            return "§8[§c" + career + "§8] §eLv." + level + "§r §7-§r §e" + ChatColor.stripColor(player.getName()) + "§r > ";
        } catch (IOException e) {
            e.printStackTrace();
            return "§l§c[Prefix錯誤]§r §e" + player.getName() + "§r > ";
        }
    }

    private String translateAlternateColorCodes(String message) {
        char[] b = message.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if ((b[i] == '&' || b[i] == '$') && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    private boolean isVip(String name) {
        File file = new File(Main.getPlugin().getDataFolder().getParentFile().getPath() + "\\players\\" + name + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getString("高級會員").equals("無");
    }

    public void setOPNickname(Player player, String nickname) {
        OPNickname.set(player.getUniqueId().toString(), nickname);
    }


}
