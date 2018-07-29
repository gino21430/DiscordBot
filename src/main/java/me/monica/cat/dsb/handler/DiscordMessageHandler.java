package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.entity.Player;

public class DiscordMessageHandler {

    public boolean dc2mc = true;

    public void handleGuildMessage(String msg) {
        if (dc2mc) Main.getPlugin().ToDiscordMainTextChannel(msg);
    }

    public void handlePrivateMessage(String msg, User author) {
        if (!msg.startsWith("!")) return;
        String[] str = msg.split(" ");
        switch (str[0]) {
            case "!link":
                break;
            case "!unlink":
                break;
            case "!dc2mc":
                if (str[1].equalsIgnoreCase("on")) dc2mc=true;
                else if (str[1].equalsIgnoreCase("off")) dc2mc=false;
                break;
            case "!unmute":
                break;
            case "!delmsg":
                break;
            case "!pm":
                String tmpStr = "";
                for (int i=3;i < str.length;i++)
                    tmpStr += str[i];
                Player player = Main.getPlugin().getServer().getPlayer(str[2]);
                if (player!=null)
                    Main.getPlugin().ToSendMessageToPlayer(tmpStr, author.getName(), player);
        }
    }
}
