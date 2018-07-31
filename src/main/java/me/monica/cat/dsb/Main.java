package me.monica.cat.dsb;

import me.monica.cat.dsb.handler.MinecraftMessageHandler;
import me.monica.cat.dsb.listener.DiscordGuildMessageListener;
import me.monica.cat.dsb.listener.DiscordPrivateMessageListener;
import me.monica.cat.dsb.listener.MinecraftMessageListener;
import me.monica.cat.dsb.listener.MinecraftPlayerJoinListener;
import me.monica.cat.dsb.util.ConfigUtil;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.*;

public final class Main extends JavaPlugin {

    private static TextChannel mainText;
    public JDA jda;
    private TextChannel logText;
    private FileConfiguration dcid2uuid = null;
    private FileConfiguration uuid2dcid = null;
    private Map<String, String> verify = new HashMap<>();
    private Map<String, String> linkMap = new HashMap<>();

    public static Main getPlugin() {
        return getPlugin(Main.class);
    }

    public static void log(String msg) {
        getPlugin().getLogger().info(msg);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MinecraftMessageListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftBanPlayerListener(), this);
        loadConfig();
        // Plugin startup logic
        startBot(Bukkit.getConsoleSender());
        mainText.sendMessage("**Server Start Running**").queue();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (jda != null) {
            mainText.sendMessage("**Server Stop Running**").queue();
            stopBot(Bukkit.getConsoleSender());
        }
    }

    private void startBot(CommandSender sender) {

        if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN) {
            sender.sendMessage("Its status is not SHUTDOWN!");
            return;
        }

        try {
            //1服BOT
            String token = getConfig().getString("token");
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addEventListener(new DiscordGuildMessageListener())
                    .addEventListener(new DiscordPrivateMessageListener())
                    .buildBlocking(JDA.Status.CONNECTED);
            mainText = jda.getTextChannelById("470842970434830363");
            logText = jda.getTextChannelById("471136766204575756");
            toDiscordMainTextChannel(":white_check_mark: Bot Start Running!");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopBot(CommandSender sender) {
        if (jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.SHUTTING_DOWN) {
            sender.sendMessage("Its status have been SHUTDOWN!");
            return;
        }
        String name = "Console";
        if (sender instanceof Player) name = sender.getName();
        toDiscordMainTextChannel(":no_entry: Bot was stopped by " + name);
        saveConfig();
        jda.shutdown();
    }

    private void loadConfig() {
        ConfigUtil configUtil = new ConfigUtil();
        uuid2dcid = configUtil.loadConfig(new File(getDataFolder(), "dcid2uuid.yml"));
        dcid2uuid = configUtil.loadConfig(new File(getDataFolder(), "uuid2dcid.yml"));
        linkMap = configUtil.loadLinkMap(new File(getDataFolder(), "linkedUser.txt"));
    }

    private void reload() {
        reloadConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        log("cmd: " + command.getName());
        if (!command.getName().equals("discord")) return false;
        if (args == null || args[0] == null) {
            log("args is null!");
            return false;
        }
        log("args[0]: " + args[0]);
        switch (args[0].toLowerCase()) {
            case "startbot":
                startBot(sender);
                return true;
            case "stopbot":
                stopBot(sender);
                return true;
            case "reload":
                reload();
                return true;
            case "verify":
                if (sender instanceof Player)
                    verifyMinecraft((Player) sender);
                return true;
            case "delmsg":
                if (args[1] == null) return false;
                deleteAllMessages(args[1]);
                return true;
            case "mute":
                if (!(sender instanceof Player)) return false;
                if (args[1] == null) return false;
                DiscordMessageHandler dmh = new DiscordMessageHandler();
                if (args[1]=="on") dmh.mute(sender.getUniqueId().toString());
                if (args[1]=="off") dmh.unmute(sender.getUniqueId().toString());
                return true;
            default:
                return false;
        }
    }

    public void toDiscordMainTextChannel(String msg) {
        mainText.sendMessage(msg).queue();
    }

    public void toSendMessageToMultilayers(String msg, Collection<? extends Player> players) {
        for (Player player : players)
            player.sendMessage(msg);
    }

    public void toSendMessageToPlayer(String msg, String author, Player player) {
        player.sendMessage("[Discord] " + author + " 私訊你: " + msg);
    }

    private void deleteAllMessages(String channelID,String name) {
        log("DeleteAllMessages");
        TextChannel channel = jda.getTextChannelById(channelID);
        while (true) {
            List<Message> toDel = new ArrayList<>();
            int count = 0;
            for (Message msg : channel.getIterableHistory()) {
                toDel.add(msg);
                count += 1;
                if (toDel.size() == 99) break;
                //Discord.Log("To delete message: "+msg.getContentDisplay());
            }
            if (toDel.size() < 2) break;
            channel.deleteMessages(toDel).queue();
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append("All messages was DELETE by "+name);
            channel.sendMessage(messageBuilder.build()).queue();
        }
    }

    public void verifyStart(User author, String minecraftName) {
        verify.put(minecraftName, author.getId());
    }

    private void verifyMinecraft(Player player) {
        String name = player.getName();
        String dcid = verify.get(name);
        if (dcid != null) {
            linkMap.put(dcid, name);
            Guild guild = jda.getGuildById("quietpond");
            GuildController gc = new GuildController(guild);
            Role playerRole = jda.getRoleById("roleid");
            Role opRole = jda.getRoleById("roleid");
            Member member = guild.getMemberById(dcid);
            if (player.isOp()) gc.addRolesToMember(member, opRole);
            else gc.addRolesToMember(member, playerRole);
            player.sendMessage("Linked to " + member.getUser().getName());
        } else
            player.sendMessage("You have not link any discord user.");
    }
    
    public void unlink(String uuid) {
        String dcid = uuid2dcid.get(uuid);
        uuid2dcid.remove(uuid);
        dcid2uuid.remove(dcid);
        linkMap.remove(dcid);
        Guild guild = jda.getGuildById("quietpondId");
        GuildController gc = new GuildController(guild);
        Role bannedRole = jda.getRoleById("roleid");
        Role opRole = jda.getRoleById("roleid");
        Member member = guild.getMemberById(dcid);
        gc.removeRolesFromMember(member,member.getRoles());
        
    }
    
    public void tempBan(String dcid) {
        Guild guild = jda.getGuildById("quietpondId");
        GuildController gc = new GuildController(guild);
        Role bannedRole = jda.getRoleById("roleid");
        Role opRole = jda.getRoleById("roleid");
        Member member = guild.getMemberById(dcid);
        gc.addRolesToMember(member,bannedRole);
    }

    public void detectNameChanged(Player player) {
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        String dcid = uuid2dcid.getString(uuid);
        if (dcid == null) return;
        if (linkMap.get(dcid).equals(name)) return;

        linkMap.put(dcid, name);
        player.sendMessage("Detecting your name changed!");
    }

}