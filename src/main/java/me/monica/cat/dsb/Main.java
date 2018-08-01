package me.monica.cat.dsb;

import me.monica.cat.dsb.handler.DiscordMessageHandler;
import me.monica.cat.dsb.handler.MinecraftMessageHandler;
import me.monica.cat.dsb.listener.*;
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
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {

    public JDA jda;
    private Guild guild;
    private TextChannel mainText;
    public FileConfiguration config;
    private FileConfiguration dcid2uuid;
    private FileConfiguration uuid2dcid;
    private Map<String, String> verify;
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
        if (!startBot(Bukkit.getConsoleSender())) return;
        mainText.sendMessage("**Server Start Running**").queue();
        DiscordMessageHandler.init();
        MinecraftMessageHandler.init();
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            mainText.sendMessage("**Server Stop Running**").queue();
            stopBot(Bukkit.getConsoleSender());
        }
    }

    private boolean startBot(CommandSender sender) {

        if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN) {
            sender.sendMessage("Its status is not SHUTDOWN!");
            return true;
        }

        try {
            //1服BOT
            String token = config.getString("Token");
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addEventListener(new DiscordGuildMessageListener())
                    .addEventListener(new DiscordPrivateMessageListener())
                    .buildBlocking(JDA.Status.CONNECTED);
            mainText = jda.getTextChannelById(getConfig().getString("Channel"));
            guild = mainText.getGuild();
            //logText = jda.getTextChannelById("471136766204575756");
            toDiscordMainTextChannel(":white_check_mark: Bot Start Running!");
            return true;
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            return false;
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

    private void init() {
        ConfigUtil configUtil = new ConfigUtil();
        config = configUtil.loadConfig("config.yml");
        verify = new HashMap<>();
        uuid2dcid = configUtil.loadConfig("uuid2dcid.yml");
        dcid2uuid = configUtil.loadConfig("dcid2uuid.yml");
        linkedUser = configUtil.loadConfig("linkedUser.yml");
    }

    public void saveConfig() {
        try {
            uuid2dcid.save(new File(getDataFolder(), "uuid2dcid.yml"));
            uuid2dcid.save(new File(getDataFolder(), "dcid2uuid.yml"));
            linkedUser.save(new File(getDataFolder(), "linkedUser.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        log("sender: " + sender.getName() + " , cmd: " + command.getName());
        try {
            if (!command.getName().equals("discord")) return false;
            if (args.length < 1) return false;
            log("args[0]: " + args[0]);
            switch (args[0].toLowerCase()) {
                case "start":
                    startBot(sender);
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
                                sender.sendMessage("/discord mute on|off");
                                break;
                        }
                    }
                    return true;
                default:
                    return false;
            }
        } catch (InsuffcientArgumentsException e) {
            e.warn(sender);
            return true;
        }
    }

    public void toDiscordMainTextChannel(String msg) {
        mainText.sendMessage(msg).queue();
    }

    public void toBroadcastToMinecraft(String msg) {
        getServer().broadcastMessage("[§c系統公告§r] " + msg);
    }

    public void toSendMessageToMultilayers(String authorName, String msg, List<String> uuids) {
        for (String uuid : uuids) {
            getServer().getPlayer(UUID.fromString(uuid)).sendMessage("[§aDiscord§r] §6" + authorName + "§r : " + msg);
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

    public void verifyStart(User author, String minecraftName) {
        verify.put(minecraftName, author.getId());
    }

    private void verifyMinecraft(Player player) {
        String name = player.getName();
        String dcid = verify.get(name);
        if (dcid != null) {
            linkedUser.set(dcid, name);
            Guild guild = jda.getGuildById("quietpond");
            GuildController gc = new GuildController(guild);
            Role playerRole = jda.getRoleById("roleid");
            Role opRole = jda.getRoleById("roleid");
            Member member = guild.getMemberById(dcid);
            if (player.isOp()) gc.addRolesToMember(member, opRole).queue();
            else gc.addRolesToMember(member, playerRole).queue();
            player.sendMessage("Linked to " + member.getUser().getName());
        } else
            player.sendMessage("You have not link any discord user.");
    }

    public void unlink(String uuid) {
        String dcid = uuid2dcid.getString(uuid);
        uuid2dcid.set(uuid, null);
        dcid2uuid.set(dcid, null);
        linkedUser.set(dcid, null);
        Guild guild = jda.getGuildById("quietpondId");
        GuildController gc = new GuildController(guild);
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