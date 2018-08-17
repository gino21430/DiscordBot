package me.monica.cat.discordbot.handler;

import me.monica.cat.discordbot.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
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
    private static Main main;

    public static void init() {
        main = Main.getPlugin();
        dc2mc = true;
        uuids = new ArrayList<>();
    }

    public void handleGuildMessage(Message message) {
        User author = message.getAuthor();
        if (author == Main.getPlugin().jda.getSelfUser() || author == null) return;
        if (message.getChannel() != main.getMainText()) return;
        String authorName = main.linkedUser.getString(author.getId());
        if (authorName == null) {
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append(author).append(" 您未綁定Minecraft帳號呦");
            Main.getPlugin().toDiscordMainTextChannel(messageBuilder.build());
        }
        if (dc2mc)
            Main.getPlugin().toSendMessageToMultilayers(authorName, ChatColor.stripColor(message.getContentStripped()), uuids);
    }

    public void handlePrivateMessage(Message message, User author, PrivateChannel channel) {
        String msg = message.getContentRaw();
        Member member = Main.getPlugin().getGuild().getMember(author);
        if (!msg.startsWith("!")) return;
        Main.log("PM's message: " + msg);
        String[] args = msg.split(" ");
        if (args.length < 1) {
            channel.sendMessage("可用命令 : link , unlink").queue();
            if (member.hasPermission(Permission.BAN_MEMBERS))
                channel.sendMessage("管理員命令 : dc2mc , delmsg , pm").queue();
            return;
        }
        args[0] = args[0].replaceAll("[-\\\\/_$^&*]", "");
        switch (args[0]) {
            case "!link":
                if (args.length < 2 || args.length > 2) {
                    channel.sendMessage("請必須輸入您的minecraft名稱.").queue();
                    return;
                }
                switch (Main.getPlugin().verifyStart(author, args[1])) {
                    case 0:
                        channel.sendMessage(">>> 請在60秒內於遊戲裡輸入\"/discord verify\"完成驗證 <<<").queue();
                        Thread thread = new Thread(() -> {
                            Date date = new Date();
                            long startTime = date.getTime();
                            while (date.getTime() <= startTime + 60000) {
                                if (!Main.getPlugin().verify.containsValue(author.getId())) {
                                    channel.sendMessage("您已完成綁定!").queue();
                                    return;
                                }
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Main.getPlugin().verify.remove(args[1], author.getId());
                            channel.sendMessage("[Error] 超過60秒未於遊戲裡完成驗證，驗證取消").queue();
                        });
                        thread.start();
                        break;
                    case 1:
                        channel.sendMessage("[Error] 稍早有其他用戶對此角色進行驗證程序").queue();
                        break;
                    case 2:
                        channel.sendMessage("[Error] " + args[1] + "目前並不在線，請進入伺服器後再試一次").queue();
                        break;
                    case 3:
                        channel.sendMessage("[Error] 此角色已被其他用戶綁定").queue();
                }
                return;
            case "!unlink":
                Main.getPlugin().unlink(author.getId(), false);
                channel.sendMessage("已解除綁定").queue();
                return;
            case "!pm":
                if (args.length < 2) {
                    channel.sendMessage("pm <在線玩家名稱> <訊息>").queue();
                    return;
                }
                StringBuilder tmpStr = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    tmpStr.append(args[i]).append(" ");
                Player player = Main.getPlugin().getServer().getPlayer(args[1]);
                if (player != null) {
                    Main.getPlugin().toSendMessageToPlayer(tmpStr.toString(), author.getName(), player);
                }
                return;
        }

        //下方switch為管理員命令
        if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("可用命令 : link , unlink").queue();
            return;
        }
        switch (args[0]) {
            case "!dc2mc":
                if (args.length < 2) {
                    channel.sendMessage("dc2mc on|off").queue();
                    return;
                }
                if (args[1].equalsIgnoreCase("on")) dc2mc = true;
                else if (args[1].equalsIgnoreCase("off")) dc2mc = false;
                return;
            case "!delmsg":
                if (args.length < 3) {
                    channel.sendMessage("delmsg <channelID> <amount>").queue();
                    return;
                }
                if (!args[2].matches("[0-9]+")) return;
                Main.getPlugin().deleteAllMessages(args[1], author.getName(), Integer.valueOf(args[2]));
                return;
            default:
                channel.sendMessage("可用命令 : link , unlink\n管理員命令 : dc2mc , delmsg , pm").queue();
        }
    }

    public void mute(Player player) {
        uuids.remove(player.getUniqueId().toString());
        player.sendMessage("[§bDiscord] 靜音來自Discord頻道的訊息: §a開啟");
        Main.log("uuids size: " + uuids.size());
    }

    public void unmute(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!uuids.contains(uuid)) uuids.add(uuid);
        player.sendMessage("[§bDiscord] 靜音來自Discord頻道的訊息: §e關閉");
        Main.log("uuids size: " + uuids.size());
    }

}