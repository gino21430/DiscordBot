package cat.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends ListenerAdapter {

    private static JDA jda;
    private static String channelID = "470842970434830363"; //1服
    Boolean DCmute = false;
    static void StartBot() {
        Discord.Log("Start Bot...");
        try
        {
            String token = "NDcwNzc5MDEwODMwNDM0MzQ0.DjnTIw.owM-NBBxb0ZRWNV4gDU-sIGJSHc";
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)           //The token of the account that is logging in.
                    .addEventListener(new Bot())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
            MC2DC(":white_check_mark: Bot Start Running!");
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    static void StopBot() {
        MC2DC(":no_entry: Bot Stop Running!");
        jda.shutdown();
    }

    static void MC2DC(String msg) {
        MessageChannel channel = jda.getTextChannelById(channelID);
        channel.sendMessage(msg).queue();
    }

    void DeleteAllMessages(String channelID) {
        Discord.Log("DeleteAllMessages");
        MessageChannel channel = jda.getTextChannelById(channelID);
        List<Message> toDel = new ArrayList<>();
        for (Message msg : channel.getIterableHistory()) {
            toDel.add(msg);
            //Discord.Log("To delete message: "+msg.getContentDisplay());
        }
        if (toDel.size()>=2) {
            ((TextChannel) channel).deleteMessages(toDel).queue();
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append("All messages have been **DELETE**");
            channel.sendMessage(messageBuilder.build()).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        JDA jda = event.getJDA();                       //JDA, the core of the api.
        //long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        User author = event.getAuthor();                //The user that sent the message
        if (author == jda.getSelfUser())
            return;
        Message message = event.getMessage();           //The message that was received.
        String msg = message.getContentDisplay();

        TextChannel logChannel = jda.getTextChannelById("471136766204575756");

        if (event.getTextChannel().getId().equals("470491828400029696")) {
            if (msg.startsWith("!cmd")) {
                msg = msg.replace("!cmd ","");
                Discord.Log("this will be execute: "+msg);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), msg);
                message.delete().queue();
                return;
            }else if (msg.startsWith("!mcmute")) {
                if (msg.endsWith("1")) Discord.MCmute = true;
                else if (msg.endsWith("0")) Discord.MCmute = false;
                message.delete().queue();
                return;
            }
        }

        logChannel.sendMessage("DC received<" + event.getAuthor().getName() + ">: " + msg).queue();
        if (!Discord.MCmute)
            Bukkit.broadcastMessage("§b[Discord] "+author.getName()+"§r > "+msg); //DC2MC
        //"[§c神之子§r] §eLv.§k987§r - §e"+

    }
}
