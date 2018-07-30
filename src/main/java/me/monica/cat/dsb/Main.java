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
    FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        startBot();
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

    public static void log(String msg) {
        getPlugin().getLogger().info(msg);
    }

     void startBot() {
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

    void stopBot() {
        ToDiscordMainTextChannel(":no_entry: Bot Stop Running!");
        jda.shutdown();
        jda = null;
    }
    
    void reload() {
        Util util = new Util();
        Thread reload = new Thread(()->{
            LLOCK(uuid2dcid);
            LLOCK(dcid2uuid);
            LLCOK(config);
            uuid2dcid.save("uuid2dcid");
            dcid2uuid.save("dcid2uuid");
            config = plugin.getConfig();
        }).start();
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals("discord")) return false;
        switch (args[0].toLowerCase()) {
            case "startBot":
                startBot();
                break;
            case "stopBot":
                stopBot();
                break;
            case "reload":
                reload();
                break;
            case "verify":
                if (sender instanceof Player)
                    verifyMinecraft(player);
                break;
        }
        return true;
    }

    public void toDiscordMainTextChannel(String msg) {
        mainText.sendMessage(msg).queue();
    }

    public void toSendMessageToMultilayers(String msg, Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendMessage(msg);
        }
    }

    public void toSendMessageToPlayer(String msg, String author, Player player) {
        player.sendMessage("[Discord] "+author+" 私訊你: "+msg);
    }


    FileConfiguration dcid2uuid;
    FileConfiguration uuid2dcid;
    Map<String,String> verify = new HashMap<>();
    
    public void verifyStart(TextChannel channel, User author, String minecraftName) {
        verify.put(str[1],author.getId());
    }
    
    public void verifyMinecraft(Player player) {
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        String dcid = verify.get(name);
        if (dcid!=null) {
            linkMap.put(dcid, name);
            Guild guild = jda.getGuildById(A"quietpond");
            GuildController gc = new GuildController(guild);
            Role playerRole = jda.getRoleById​("roleid");
            Role opRole = jda.getRoleById​("roleid");
            Member member = guild.getMemberById(dcid);
            if (player.isOp()) gc.addRolesToMember​(member,opRole);
            else gc.addRolesToMember​(member,playerRole);
            player.sendMessage("Linked to "+user.getName());
        }else
            player.sendMessage("You have not link any Discord user.")
    }
    
    public boolean detectNameChanged(Player player) {
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        String dcid = uuid2dcid.getString(uuid);
        
        if (linkMap.get(dcid)==name) return false;
        
        linkMap.edit(dcid,name);
        player.sendMessage("Detecting your name changed!");
    }
    
}
