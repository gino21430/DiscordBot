package me.monica.cat.discordbot.handler;


import me.monica.cat.discordbot.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscordMessageHandler {

    private static boolean dc2mc;
    private static List<String> uuids;

    public static void init() {
        dc2mc = true;
        uuids = new ArrayList<>();
    }

    public void handleGuildMessage(User author, String msg) {
        if (author == Main.getPlugin().jda.getSelfUser() || author == null) return;
        if (dc2mc) Main.getPlugin().toSendMessageToMultilayers(author.getName(), ChatColor.stripColor(msg), uuids);
    }

    public void handlePrivateMessage(Message message, User author, PrivateChannel channel) {
        String msg = message.getContentStripped();
        if (!msg.startsWith("!")) return;
        Main.log("PM's message: " + msg);
        String[] str = msg.split(" ");
        switch (str[0]) {
            case "!link":
                if (str.length < 2 || str.length > 2) channel.sendMessage("請輸入您的minecraft名稱.").queue();
                switch (Main.getPlugin().verifyStart(author, str[1])) {
                    case 0:
                        channel.sendMessage(">>> 請盡速於遊戲裡輸入\"/discord link\"完成驗證 <<<").queue();
                        new Thread(() -> {
                            Date date = new Date();
                            long now = date.getTime();
                            while (new Date().getTime() <= now + 60) {
                                if (!Main.getPlugin().verify.containsValue(author.getId())) {
                                    channel.sendMessage("您已完成驗證!").queue();
                                    return;
                                }
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Main.getPlugin().verify.remove(str[1], author.getId());
                            channel.sendMessage("[Error] 超過60秒未於遊戲裡完成驗證，驗證取消").queue();
                        }).start();
                        break;
                    case 1:
                        channel.sendMessage("[Error] 稍早有其他用戶對此角色進行驗證程序").queue();
                        break;
                    case 2:
                        channel.sendMessage("[Error] " + str[1] + "目前並不在線，請進入伺服器後再試一次").queue();
                    case 3:
                        channel.sendMessage("[Error] 此角色已被其他用戶綁定").queue();
                }
                break;
            case "!unlink":
                Main.getPlugin().unlink(author.getId(), false);
                break;
            case "!dc2mc":
                if (str[1].equalsIgnoreCase("on")) dc2mc = true;
                else if (str[1].equalsIgnoreCase("off")) dc2mc = false;
                break;
            case "!unmute":
                break;
            case "!delmsg":
                break;
            case "!pm":
                StringBuilder tmpStr = new StringBuilder();
                for (int i = 3; i < str.length; i++)
                    tmpStr.append(str[i]);
                Player player = Main.getPlugin().getServer().getPlayer(str[2]);
                if (player != null)
                    Main.getPlugin().toSendMessageToPlayer(tmpStr.toString(), author.getName(), player);
                break;
            case "!WTF":
                break;
        }
    }

    public void mute(String uuid) {
        uuids.remove(uuid);
        Main.log("uuids size: " + uuids.size());
    }

    public void unmute(String uuid) {
        if (!uuids.contains(uuid)) uuids.add(uuid);
        Main.log("uuids size: " + uuids.size());
    }

}