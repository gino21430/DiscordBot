package cat.discord;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Discord extends JavaPlugin implements Listener {

    boolean MC2DCmute = false;
    boolean DC2MCmute = false;
    static FileConfiguration DCID2UUID;
    static FileConfiguration UUID2Name;

    static Discord getPlugin() {
        return getPlugin(Discord.class);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        DiscordGuildMessage.StartBot();
        DiscordGuildMessage.MC2DC(":white_check_mark: Server Start!");
        DCID2UUID = LoadConfig("DCID2UUID");
        UUID2Name = LoadConfig("UUID2Name");
    }

    @Override
    public void onDisable() {
        DiscordGuildMessage.MC2DC(":no_entry: Server Stop!");
        DiscordGuildMessage.StopBot();
        try {
            DCID2UUID.save("DCID2UUID");
            UUID2Name.save("UUID2Name");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void Log(String msg) {
        Bukkit.getLogger().info("[Discord] "+msg);
    }

    private FileConfiguration LoadConfig(String fileName) {
        File file = new File(getDataFolder(),fileName);
        if(!getDataFolder().exists()){
            if (!getDataFolder().mkdir()) Log("make a dir error");
        }
        if(!file.exists()) this.saveResource(fileName, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void setMC2DCmute(boolean MCmute) {
        this.MC2DCmute = MCmute;
    }

    private void setDC2MCmute(boolean DCmute) {
        this.DC2MCmute = DCmute;
    }

    boolean isDC2MCmute() {
        return DC2MCmute;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DiscordGuildMessage DiscordGuildMessage = new DiscordGuildMessage();

        String name = "Console";
        if (!command.getName().equalsIgnoreCase("discord"))
            return false;

        switch (args[0].toLowerCase()) {
            case "start":
                cat.discord.DiscordGuildMessage.StartBot();
                cat.discord.DiscordGuildMessage.MC2DC("DiscordGuildMessage has been turn on by "+name);
                return true;
            case "stop":
                cat.discord.DiscordGuildMessage.MC2DC("DiscordGuildMessage has been shutdown by "+name);
                cat.discord.DiscordGuildMessage.StopBot();
                return true;
            case "delmsg":
                DiscordGuildMessage.DeleteAllMessages(args[1]);
                return true;
            case "mute":
                if (args[1]==null) return false;
                else if (args[1].equals("1")) setDC2MCmute(true);
                else if (args[1].equals("0")) setDC2MCmute(false);
                return true;
            case "link":
                PrivateMessage pm = new PrivateMessage();
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    if (pm.vMap.get(args[1]) != null) {
                        UUID2Name.set(player.getUniqueId().toString(), player.getName());
                        DCID2UUID.set(pm.vMap.get(args[1]), player.getUniqueId().toString());
                        pm.VerifyPass();
                        player.sendMessage("驗證成功");
                    }else
                        player.sendMessage("驗證失敗");
                }
                return true;
            case "unlink":

                return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.getName().equals(UUID2Name.getString(player.getUniqueId().toString())))
            UUID2Name.set(player.getUniqueId().toString(),player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (MC2DCmute) return;
        String msg = e.getMessage();
        DiscordGuildMessage.MC2DC(e.getPlayer().getDisplayName()+" > "+msg);
    }

}
