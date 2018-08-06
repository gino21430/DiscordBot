package me.monica.cat.discordbot.handler;

import com.sun.management.OperatingSystemMXBean;
import me.monica.cat.discordbot.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ServerStatusHandler {

    public static void runTimerTask() {
        Main main = Main.getPlugin();
        TextChannel channel = main.getJda().getTextChannelById(main.config.getString("StatusChannel"));
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<Message> toDel = new ArrayList<>();
                for (Message msg : channel.getIterableHistory()) {
                    toDel.add(msg);
                    if (toDel.size() == 99) break;
                }
                if (toDel.size() == 1) channel.deleteMessageById(toDel.get(0).getId()).queue();
                else if (toDel.size() > 1) channel.deleteMessages(toDel).queue();
                Main.log("Update server info");
                channel.sendMessage(getData()).queue();
            }
        };
        timer.schedule(task, 10 * 1000, 30 * 1000);
    }


    private static String getData() {
        Runtime lRuntime = Runtime.getRuntime();
        long freeM = lRuntime.freeMemory() / 1024 / 1024;
        long maxM = lRuntime.maxMemory() / 1024 / 1024;
        long tM = lRuntime.totalMemory() / 1024 / 1024;
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        java.lang.management.ThreadMXBean t = ManagementFactory.getThreadMXBean();

        DecimalFormat df = new DecimalFormat("#.##");
        String cpu = df.format(osmb.getSystemCpuLoad() * 100);
        Date date = new Date();
        int chunks = 0;
        for (World world : getServer().getWorlds())
            chunks += world.getLoadedChunks().length;
        int players = Bukkit.getOnlinePlayers().size();
        Main.log("System.currentTimeMillis=" + System.currentTimeMillis());

        long startTime = System.currentTimeMillis();
        getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
            public void run() {
                long endTime = System.currentTimeMillis();

            }
        }, 20L); //This is the delay, in ticks, until the thread is executed, since the main threads ticks 20 times per second, 60 ticks is 3 seconds.


        int tps = (int) (System.currentTimeMillis() / 50L);
        return "==========" + date.toString() + "==========" + "\n" +
                "spigot/jvm/max : " + t.getThreadCount() + "/" + t.getDaemonThreadCount() + "/" + t.getPeakThreadCount() + "\n" +
                "Memory | total/available/max：" + tM + "/" + freeM + "/" + maxM + " MB" + "\n" +
                "CPU      | " + cpu + " %" + "\n" +
                "Players | " + players + "\n" +
                "Chunks  | " + chunks + "\n" +
                "TPS      | " + tps + "\n" +
                "==========================================";
    }
}
