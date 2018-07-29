package me.monica.cat.dsb;

import me.monica.cat.dsb.listener.DiscordPrivateMessageListener;
import me.monica.cat.dsb.listener.DiscordGuildMessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.Collection;

public final class Main extends JavaPlugin {

    JDA jda;
    TextChannel mainText;
    TextChannel logText;

    @Override
    public void onEnable() {
        // Plugin startup logic
        StartBot();
        mainText = jda.getTextChannelById("");
        logText = jda.getTextChannelById("");
        mainText.sendMessage("**Server Start Running**").queue();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mainText.sendMessage("**Server Stop Running**").queue();
        StopBot();
    }

    public static Main getPlugin() {
        return getPlugin(Main.class);
    }

    public static void Log(String msg) {
        getPlugin().getLogger().info(msg);
    }

     void StartBot() {
        try
        {
            String token = "NDcwNzc5MDEwODMwNDM0MzQ0.DjnTIw.owM-NBBxb0ZRWNV4gDU-sIGJSHc"; //1服BOT
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addEventListener(new DiscordGuildMessageListener())
                    .addEventListener(new DiscordPrivateMessageListener())
                    .buildBlocking();
            ToDiscordMainTextChannel(":white_check_mark: Bot Start Running!");
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    void StopBot() {
        ToDiscordMainTextChannel(":no_entry: Bot Stop Running!");
        jda.shutdown();
        jda = null;
    }

    public void ToDiscordMainTextChannel(String msg) {
        mainText.sendMessage(msg).queue();
    }

    public void ToSendMessageToMultilayers(String msg, Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendMessage(msg);
        }
    }

    public void ToSendMessageToPlayer(String msg, String author, Player player) {
        player.sendMessage("[Discord] "+author+" 私訊你: "+msg);
    }


}
