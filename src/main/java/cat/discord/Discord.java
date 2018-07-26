package cat.discord;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Discord extends JavaPlugin implements Listener {

    static boolean MCmute = false;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bot.StartBot();
        Bot.MC2DC(":white_check_mark: Server Start!");
    }

    @Override
    public void onDisable() {
        Bot.MC2DC(":no_entry: Server Stop!");
        Bot.StopBot();
    }

    static void Log(String msg) {
        Bukkit.getLogger().info("[Discord] "+msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bot bot = new Bot();
        Player player;

        String name = "Console";
        if (sender instanceof Player) {
            player = (Player) sender;
            name = player.getName();
        }
        if(command.getName().equalsIgnoreCase("DCstart")) {
            Bot.StartBot();
            Bot.MC2DC("Bot has been turn on by "+name);
            return true;
        }
        else if (command.getName().equalsIgnoreCase("DCstop")) {
            Bot.MC2DC("Bot has been shutdown by "+name);
            Bot.StopBot();
            return true;
        }else if (command.getName().equalsIgnoreCase("deldc")) {
            bot.DeleteAllMessages(args[0]);
            return true;
        }else if (command.getName().equalsIgnoreCase("say")) {
            StringBuilder tmpStr = new StringBuilder();
            for (String str : args)
                tmpStr.append(str);
            Bot.MC2DC(tmpStr.toString());
            return true;
        }else if (command.getName().equalsIgnoreCase("dcmute")) {
            if (args[0].equals("1")) bot.DCmute = true;
            else if (args[0].equals("0")) bot.DCmute = false;
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (MCmute) return;
        String msg = e.getMessage();
        Bot.MC2DC(e.getPlayer().getDisplayName()+" > "+msg);
    }

}
