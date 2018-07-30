package me.monica.cat.dsb.handler;

import me.monica.cat.dsb.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.entity.Player;

public class DiscordMessageHandler {

    private boolean dc2mc = true;

    public void handleGuildMessage(User author, String msg) {
        if (author == Main.getPlugin().jda.getSelfUser() || author == null) return;
        if (dc2mc) Main.getPlugin().toDiscordMainTextChannel(msg);
    }

    public void handlePrivateMessage(Message message, User author) {
        String msg = message.getContentStripped();
        if (!msg.startsWith("!")) return;
        String[] str = msg.split(" ");
        switch (str[0]) {
            case "!link":
                Main.getPlugin().verifyStart(author, str[1]);
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
        }
    }

}
