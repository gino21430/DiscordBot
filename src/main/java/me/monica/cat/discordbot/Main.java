package me.monica.cat.discordbot;


import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import me.monica.cat.discordbot.handler.MinecraftMessageHandler;
import me.monica.cat.discordbot.handler.ServerStatusHandler;
import me.monica.cat.discordbot.listener.*;
import me.monica.cat.discordbot.util.ConfigUtil;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
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
    public FileConfiguration linkedUser;
    private Guild guild;
    private GuildController gc;
    private TextChannel mainText;
    private FileConfiguration dcid2uuid;
    private FileConfiguration uuid2dcid;

    public static Main getPlugin() {
        return getPlugin(Main.class);
    }

    public static void log(String msg) {
        getPlugin().getLogger().info(msg);
    }

    public JDA getJda() {
        return this.jda;
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getMainText() {
        return mainText;
    }

    @Override
    public void onEnable() {
        init();
        getServer().getPluginManager().registerEvents(new MinecraftMessageListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftWorldSaveListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftBroadcastListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftPlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new MinecraftBanPlayerListener(), this);
        startBot(Bukkit.getConsoleSender(), true);
        //if (jda != null && jda.getStatus()==JDA.Status.CONNECTED)
    }

    @Override
    public void onDisable() {
        Thread thread = new Thread(() -> {
            if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
                mainText.sendMessage("**Server Stopping**").queue();
                stopBot(Bukkit.getConsoleSender());
            }
        });
        thread.start();
    }

    private void startBot(CommandSender sender, boolean isStartUp) {
        if (jda != null) {
            log("jda is not NULL");
            if (jda.getStatus() == JDA.Status.SHUTTING_DOWN) {
                sender.sendMessage("It is SHUTTING DOWN!");
                return;
            } else if (jda.getStatus() != JDA.Status.SHUTDOWN) return;
        }

        String token = config.getString("Token");
        log("ready to starting");
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
                ServerStatusHandler serverStatusHandler = new ServerStatusHandler();
                serverStatusHandler.runTimerTask();
            } catch (LoginException | InterruptedException e) {
                log("Error while JDA init");
                e.printStackTrace();
            }
        });
        init1.setName("JDAInit");
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
        try {
            mainText.sendMessage(":no_entry: Bot was stopped by " + name).complete(false);
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().cancelAllTasks();
        saveConfig();
        jda.shutdown();
    }

    private void init() {
        ConfigUtil configUtil = new ConfigUtil();
        config = configUtil.loadConfig("config.yml");
        verify = new HashMap<>();
        dcid2uuid = configUtil.loadConfig("dcid2uuid.yml");
        uuid2dcid = configUtil.loadConfig("uuid2dcid.yml");
        linkedUser = configUtil.loadConfig("linkedUsers.yml");
        DiscordMessageHandler.init();
        MinecraftMessageHandler.init();
        ServerStatusHandler.init();
    }

    public void saveConfig() {
        try {
            dcid2uuid.save(new File(getDataFolder(), "dcid2uuid.yml"));
            uuid2dcid.save(new File(getDataFolder(), "uuid2dcid.yml"));
            linkedUser.save(new File(getDataFolder(), "linkedUsers.yml"));
            MinecraftMessageHandler mmh = new MinecraftMessageHandler();
            mmh.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "discord":
                if (args.length < 1) return false;
                switch (args[0].toLowerCase()) {
                    case "start":
                        if (!sender.isOp()) {
                            sender.sendMessage("§c你沒有權限執行此命令!");
                            return true;
                        }
                        startBot(sender, false);
                        return true;
                    case "stop":
                        if (!sender.isOp()) {
                            sender.sendMessage("§c你沒有權限執行此命令!");
                            return true;
                        }
                        stopBot(sender);
                        return true;
                    case "delmsg":
                        if (!sender.isOp()) {
                            sender.sendMessage("§c你沒有權限執行此命令!");
                            return true;
                        }
                        if (args.length < 3) {
                            sender.sendMessage("§e/discord delmsg <channelID> <amount>");
                            return true;
                        }
                        if (!args[2].matches("[0-9]+")) return false;
                        else deleteAllMessages(args[1], sender.getName(), Integer.valueOf(args[2]));
                        return true;
                    case "setnick":
                        if (!sender.isOp()) {
                            sender.sendMessage("§c你沒有權限執行此命令!");
                            return true;
                        }
                        if (args.length < 2) {
                            sender.sendMessage("§e/discord setnick <Nickname>");
                            return true;
                        }
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            MinecraftMessageHandler mmh = new MinecraftMessageHandler();
                            StringBuilder tmp = new StringBuilder();
                            for (int i = 1; i < args.length; i++)
                                tmp.append(args[i]);
                            mmh.setOPNickname(player, tmp.toString());
                            player.sendMessage("§e暱稱已變更為: " + tmp.toString());
                        }
                        return true;
                    case "verify":
                        if (sender instanceof Player) verifyMinecraft((Player) sender);
                        return true;
                    case "unlink":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            unlink(player.getName(), true);
                            player.sendMessage("已解除綁定");
                        }
                        return true;
                    case "mute":
                        if (!(sender instanceof Player)) return false;
                        if (args.length < 2) {
                            sender.sendMessage("§e/discord mute on|off");
                            return true;
                        }
                        DiscordMessageHandler dmh = new DiscordMessageHandler();
                        switch (args[1]) {
                            case "on":
                                dmh.mute(((Player) sender));
                                break;
                            case "off":
                                dmh.unmute(((Player) sender));
                                break;
                            default:
                                sender.sendMessage("§e/discord mute on|off");
                                break;
                        }
                        return true;
                    default:
                        return false;
                }
            case "color": {
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i < 10; i++)
                    tmp.append("§").append(i).append(i);
                for (char ch : new char[]{'a', 'b', 'c', 'd', 'e'})
                    tmp.append("§").append(ch).append(ch);
                tmp.append("\n");
                for (char ch : new char[]{'k', 'l', 'm', 'n', 'o'})
                    tmp.append("§").append(ch).append(ch).append("§r ");
                sender.sendMessage(tmp.toString());
                return true;
            }
            case "dcpm": {
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                if (args.length < 2) return false;
                StringBuilder tmp = new StringBuilder();
                tmp.append("[Minecraft私訊] ").append(player.getName()).append(" > ");
                for (int i = 1; i < args.length; i++) tmp.append(args[i]).append(" ");
                User user = jda.getUsersByName(args[0], false).get(0);
                user.openPrivateChannel().queue((channel) -> channel.sendMessage(tmp.toString()).queue());
                return true;
            }
        }
        return false;
    }

    public void toDiscordMainTextChannel(Message message) {
        if (jda == null || jda.getStatus() == JDA.Status.SHUTTING_DOWN || jda.getStatus() == JDA.Status.SHUTDOWN)
            return;
        mainText.sendMessage(message).queue();
    }

    public void toDiscordMainTextChannel(String msg) {
        if (jda == null || jda.getStatus() == JDA.Status.SHUTTING_DOWN || jda.getStatus() == JDA.Status.SHUTDOWN)
            return;
        mainText.sendMessage(msg).queue();
    }

    public void toSendMessageToMultilayers(String authorName, String msg, List<String> uuids) {
        for (String uuid : uuids) {
            getServer().getPlayer(UUID.fromString(uuid)).sendMessage("§8[§r§bDiscord§r§8]§r §e" + authorName + "§r > " + msg);
        }
    }

    public void toSendMessageToPlayer(String msg, String author, Player player) {
        player.sendMessage("[§bDiscord§r] §e" + author + " §b私訊你:§r " + msg);
    }

    public void deleteAllMessages(String channelID, String name, int max) {
        log("DeleteAllMessages");
        TextChannel channel = jda.getTextChannelById(channelID);
        int count = 0;
        while (count < max) {
            List<Message> toDel = new ArrayList<>();
            for (Message msg : channel.getIterableHistory()) {
                toDel.add(msg);
                count++;
                if (count == max) break;
                if (toDel.size() == 99) break;
                //Discord.Log("To delete message: "+msg.getContentDisplay());
            }
            if (toDel.size() < 2) break;
            channel.deleteMessages(toDel).queue();
            if (channel.getIterableHistory() == null) break;
        }
        channel.sendMessage("All messages was DELETE by " + name).queue();
    }

    public int verifyStart(User author, String minecraftName) {
        if (linkedUser.getString(author.getId()) != null) return 3; //已被綁定
        if (getServer().getPlayer(minecraftName) == null) return 2; //角色不在線
        if (verify.get(minecraftName) != null) return 1; //已有人在對同角色進行驗證
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
            verify.remove(name, dcid);
            linkedUser.set(dcid, name);
            uuid2dcid.set(player.getUniqueId().toString(), dcid);
            dcid2uuid.set(dcid, player.getUniqueId().toString());
            Role playerRole = jda.getRoleById(config.getString("PlayerRole"));
            Role opRole = jda.getRoleById(config.getString("OPRole"));
            Member member = guild.getMemberById(dcid);
            if (player.isOp()) gc.addRolesToMember(member, opRole).queue();
            else gc.addRolesToMember(member, playerRole).queue();
            player.sendMessage("Sucessful linked to " + member.getUser().getName());
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
        if (dcid == null) {
            log("dcid is null");
            return;
        }
        if (uuid == null) {
            log("uuid is null");
            return;
        }
        log("dcid: " + dcid + ", uuid: " + uuid);
        dcid2uuid.set(dcid, null);
        uuid2dcid.set(uuid, null);
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
