package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
        if (dc2mc) Main.getPlugin().toSendMessageToMultilayers(author.getName(), msg, uuids);
    }

    public void handlePrivateMessage(Message message, User author, PrivateChannel channel) {
        String msg = message.getContentStripped();
        if (!msg.startsWith("!")) return;
        Main.log("PM's message: " + msg);
        String[] str = msg.split(" ");
        switch (str[0]) {
            case "!link":
                if (str.length < 2) channel.sendMessage("You must enter a argument.").queue();
                else Main.getPlugin().verifyStart(author, str[1]);
                break;
            case "!unlink":
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