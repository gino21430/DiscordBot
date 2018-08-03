package me.monica.cat.discordbot;


import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import me.monica.cat.discordbot.handler.MinecraftMessageHandler;
import me.monica.cat.discordbot.listener.*;
import me.monica.cat.discordbot.util.ConfigUtil;
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
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {

    public JDA jda;
    public FileConfiguration config;
    public Map<String, String> verify;
    private Guild guild;
    private GuildController gc;
    private TextChannel mainText;
    private FileConfiguration dcid2uuid;
    private FileConfiguration uuid2dcid;
    private FileConfiguration linkedUser;

    public static Main getPlugin() {
        return getPlugin(Main.class);
    }

    public static void log(String msg) {
        getPlugin().getLogger().info(msg);
    }

    @Override
    public void onEnable() {
        init();
        getServer().getPluginManager().registerEvents(new MinecraftMessageListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftWorldSaveListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftBanPlayerListener(), this);
        startBot(Bukkit.getConsoleSender(), true);
        DiscordMessageHandler.init();
        MinecraftMessageHandler.init();
        //if (jda != null && jda.getStatus()==JDA.Status.CONNECTED)
    }

    @Override
    public void onDisable() {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            mainText.sendMessage("**Server Stopping**").queue();
            stopBot(Bukkit.getConsoleSender());
        }
    }

    private void startBot(CommandSender sender, boolean isStartUp) {
        if (jda != null) {
            if (jda.getStatus() == JDA.Status.SHUTTING_DOWN) {
                sender.sendMessage("It is SHUTTING DOWN!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else return;
        }
        String token = config.getString("Token");
        Thread init1 = new Thread(() -> {
            try {
                jda = new JDABuilder(AccountType.BOT)
                        .setToken(token)
                        .addEventListener(new DiscordGuildMessageListener())
                        .addEventListener(new DiscordPrivateMessageListener())
                        .buildBlocking(JDA.Status.CONNECTED);
                mainText = jda.getTextChannelById(config.getString("Channel"));
                guild = mainText.getGuild();
                gc = new GuildController(guild);
                mainText.sendMessage(":white_check_mark: Bot was started").queue();
                if (isStartUp) mainText.sendMessage("**Server Starting**").queue();
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        init1.start();
    }

    private void stopBot(CommandSender sender) {
        if (jda == null ||
                jda.getStatus() == JDA.Status.SHUTTING_DOWN ||
                jda.getStatus() == JDA.Status.SHUTDOWN) {
            sender.sendMessage("It has been SHUTDOWN!");
            return;
        }
        String name = "Console";
        if (sender instanceof Player) name = sender.getName();
        toDiscordMainTextChannel(":no_entry: Bot was stopped by " + name);
        saveConfig();
        jda.shutdown();
    }

    private void init() {
        ConfigUtil configUtil = new ConfigUtil();
        config = configUtil.loadConfig("config.yml");
        verify = new HashMap<>();
        uuid2dcid = configUtil.loadConfig("uuid2dcid.yml");
        dcid2uuid = configUtil.loadConfig("dcid2uuid.yml");
        linkedUser = configUtil.loadConfig("linkedUsers.yml");
    }

    public void reload(CommandSender sender) {
        stopBot(sender);
        init();
    }

    public void saveConfig() {
        try {
            uuid2dcid.save(new File(getDataFolder(), "uuid2dcid.yml"));
            uuid2dcid.save(new File(getDataFolder(), "dcid2uuid.yml"));
            linkedUser.save(new File(getDataFolder(), "linkedUsers.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equals("discord")) {
                if (args.length < 1) return false;
                switch (args[0].toLowerCase()) {
                    case "start":
                        startBot(sender, false);
                        return true;
                    case "stop":
                        stopBot(sender);
                        return true;
                    case "reload":
                        init();
                        return true;
                    case "verify":
                        if (sender instanceof Player) verifyMinecraft((Player) sender);
                        return true;
                    case "delmsg":
                        if (args.length < 2) throw new InsuffcientArgumentsException(2);
                        else deleteAllMessages(args[1], sender.getName());
                        return true;
                    case "mute":
                        if (!(sender instanceof Player)) return false;
                        if (args.length < 2) throw new InsuffcientArgumentsException(2);
                        else {
                            DiscordMessageHandler dmh = new DiscordMessageHandler();
                            switch (args[1]) {
                                case "on":
                                    dmh.mute(((Player) sender).getUniqueId().toString());
                                    break;
                                case "off":
                                    dmh.unmute(((Player) sender).getUniqueId().toString());
                                    break;
                                default:
                                    sender.sendMessage("§6/discord mute on|off");
                                    break;
                            }
                        }
                        return true;
                    default:
                        return false;
                }
            } else if (command.getName().equals("color")) {
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i < 10; i++)
                    tmp.append("§").append(i).append(i).append("§r");
                sender.sendMessage(tmp.toString());
                return true;
            }
        } catch (InsuffcientArgumentsException e) {
            e.warn(sender);
            return true;
        }
        return false;
    }

    public void toDiscordMainTextChannel(String msg) {
        mainText.sendMessage(msg).queue();
    }

    public void toBroadcastToMinecraft(String msg) {
        getServer().broadcastMessage("[§c系統公告§r] " + msg);
    }

    public void toSendMessageToMultilayers(String authorName, String msg, List<String> uuids) {
        for (String uuid : uuids) {
            getServer().getPlayer(UUID.fromString(uuid)).sendMessage("§8[§r§aDiscord§r§8]§r §e" + authorName + "§r > " + msg);
        }
    }

    public void toSendMessageToPlayer(String msg, String author, Player player) {
        player.sendMessage("[Discord] " + author + " 私訊你: " + msg);
    }

    private void deleteAllMessages(String channelID, String name) {
        log("DeleteAllMessages");
        TextChannel channel = jda.getTextChannelById(channelID);
        while (true) {
            List<Message> toDel = new ArrayList<>();
            for (Message msg : channel.getIterableHistory()) {
                toDel.add(msg);
                if (toDel.size() == 99) break;
                //Discord.Log("To delete message: "+msg.getContentDisplay());
            }
            if (toDel.size() < 2) break;
            channel.deleteMessages(toDel).queue();
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append("All messages was DELETE by ").append(name);
            channel.sendMessage(messageBuilder.build()).queue();
        }
    }

    public int verifyStart(User author, String minecraftName) {
        if (linkedUser.getString(author.getId()) != null) return 3; //已被綁定
        if (getServer().getPlayer(minecraftName) == null) return 2; //角色不在線
        if (verify.get(minecraftName) != null) return 1; //已有人在對同角色進行驗證
        log("提出要求用戶: " + author.getName() + ", 驗證對象: " + minecraftName);
        verify.put(minecraftName, author.getId());
        log("========== 目前清單 ==========");
        for (Map.Entry entry : verify.entrySet())
            log("提出要求用戶: " + entry.getValue() + ", 驗證對象: " + entry.getKey());
        return 0;
    }

    private void verifyMinecraft(Player player) {
        String name = player.getName();
        String dcid = verify.get(name);
        if (dcid != null) {
            linkedUser.set(dcid, name);
            verify.remove(name, dcid);
            Role playerRole = jda.getRoleById("roleid");
            Role opRole = jda.getRoleById("roleid");
            Member member = guild.getMemberById(dcid);
            if (player.isOp()) gc.addRolesToMember(member, opRole).queue();
            else gc.addRolesToMember(member, playerRole).queue();
            player.sendMessage("Linked to " + member.getUser().getName());
        } else
            player.sendMessage("You have not link any discord user.");
    }

    public void unlink(String input, boolean isFromMinecraft) {
        String uuid, dcid;
        if (isFromMinecraft) {
            uuid = getServer().getPlayer(input).getUniqueId().toString();
            dcid = uuid2dcid.getString(uuid);
        } else {
            dcid = input;
            uuid = dcid2uuid.getString(dcid);
        }
        uuid2dcid.set(uuid, null);
        dcid2uuid.set(dcid, null);
        linkedUser.set(dcid, null);
        Member member = guild.getMemberById(dcid);
        gc.removeRolesFromMember(member, member.getRoles()).queue();
    }

    public void detectNameChanged(Player player) {
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        String dcid = uuid2dcid.getString(uuid);
        if (dcid == null) return;
        if (linkedUser.getString(dcid).equals(name)) return;
        linkedUser.set(dcid, name);
        player.sendMessage("Detecting your name changed!");
    }

}
