package cat.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscordGuildMessage extends ListenerAdapter {

    private static JDA jda;
    private static String mainChannel = "470842970434830363"; //1服
    private static String logChannel = "471136766204575756"; //discord log

    static void Log(String msg) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("MM.dd HH:mm:ss zzz");
        jda.getTextChannelById(logChannel).sendMessage("["+ft.format(date)+"]: "+msg).queue();
    }

    static void StartBot() {
        Discord.Log("Start Bot...");
        try
        {
            String token = "NDcwNzc5MDEwODMwNDM0MzQ0.DjnTIw.owM-NBBxb0ZRWNV4gDU-sIGJSHc";
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)           //The token of the account that is logging in.
                    .addEventListener(new DiscordGuildMessage())  //An instance of a class that will handle events.
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
        MessageChannel channel = jda.getTextChannelById(mainChannel);
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
            messageBuilder.append("All messages have been DELETE.");
            channel.sendMessage(messageBuilder.build()).queue();
        }
    }

    private void DeleteMessageDelay(Message message, int seconds) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(seconds*1000);
            } catch (InterruptedException ignored){}
            message.delete().queue();
        });
        thread.start();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        JDA jda = event.getJDA();                       //JDA, the core of the api.
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        String msg = message.getContentDisplay();
        TextChannel channel = event.getTextChannel();
        if (event.getAuthor() == null ||
                event.getAuthor().getId() == null ||
                jda.getSelfUser().getId() == null ||
                event.getAuthor().getId().equals(jda.getSelfUser().getId())) return;

        //execute console command
        /*
        if (event.getTextChannel().getId().equals("470491828400029696")) {
            if (msg.startsWith("!cmd")) {
                msg = msg.replace("!cmd ","");
                Discord.Log("To be execute: "+msg);
                Discord.getPlugin().getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), msg);
                DeleteMessageDelay(message,3);
                return;
            }else if (msg.startsWith("!mcmute")) {
                if (msg.endsWith("1")) Discord.MCmute = true;
                else if (msg.endsWith("0")) Discord.MCmute = false;
                DeleteMessageDelay(message,3);
                return;
            }
        }
        */
        //log
        jda.getTextChannelById(logChannel).sendMessage("DC received<" + event.getAuthor().getName() + ">: " + msg).queue();
        String name = Discord.UUID2Name.getString(Discord.DCID2UUID.getString(author.getId()));
        if (name==null) {
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append(author).append(" 您尚未連結Minecraft帳號\n").append("請私訊Bot\"!verify <遊戲名稱>\"以進行連結程序.");
            channel.sendMessage(messageBuilder.build()).queue();
            DeleteMessageDelay(message,5);
            //sDeleteMessageDelay(channel.getMessageById(channel.getLatestMessageId()).complete(),6);
            return;
        }else {
            Bukkit.broadcastMessage("[Discord]  "+name+" > "+msg);
        }
        if (!Discord.getPlugin().DC2MCmute)
            Bukkit.broadcastMessage("§b[Discord] "+author.getName()+"§r > "+msg); //DC2MC
        //"[§c神之子§r] §eLv.§k987§r - §e"+

    }

}
