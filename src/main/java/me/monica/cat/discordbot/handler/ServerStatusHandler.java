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

    private static double tps;
    private static int updatePeriod;
    public static TimerTask task;
    private static final long startUpTime = System.currentTimeMillis();


    public static void init() {
        updatePeriod = Main.getPlugin().config.getInt("UpdatePeriod");
        Main.log("updatePeriod2: " + updatePeriod);
        tps = 0;
        if (updatePeriod < 10) updatePeriod = 10;
        Main.log("updatePeriod2: " + updatePeriod);
    }

    public void runTimerTask() {
        Timer timer = new Timer();
        TextChannel channel = Main.getPlugin().getJda().getTextChannelById(Main.getPlugin().config.getString("StatusChannel"));
        task = new TimerTask() {
            @Override
            public void run() {
                List<Message> toDel = new ArrayList<>();
                for (Message msg : channel.getIterableHistory()) {
                    toDel.add(msg);
                    if (toDel.size() == 99) break;
                }
                if (toDel.size() == 1) channel.deleteMessageById(toDel.get(0).getId()).queue();
                else if (toDel.size() > 1) channel.deleteMessages(toDel).queue();
                channel.sendMessage(getData()).queue();
            }
        };
        Main.log("updatePeriod: " + updatePeriod);
        timer.schedule(task, 5 * 1000, updatePeriod * 1000);
    }


    private String getData() {
        Runtime lRuntime = Runtime.getRuntime();
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        java.lang.management.ThreadMXBean t = ManagementFactory.getThreadMXBean();
        Date date = new Date();

        //Memory
        int aM = (int) lRuntime.freeMemory() / 1024 / 1024;
        int mM = (int) lRuntime.maxMemory() / 1024 / 1024;
        int tM = (int) lRuntime.totalMemory() / 1024 / 1024;

        //cpu
        DecimalFormat df = new DecimalFormat("#.##");
        String cpu = df.format(osmb.getSystemCpuLoad() * 100);

        //chunks
        int chunks = 0;
        for (World world : getServer().getWorlds())
            chunks += world.getLoadedChunks().length;
        int players = Bukkit.getOnlinePlayers().size();

        //tps
        long startTime = System.currentTimeMillis();
        getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
            synchronized (this) {
                double tmp = System.currentTimeMillis() - startTime;
                tps = 10.0 * 1000 / tmp;
                if (tps > 20.00) tps = 20.00;
            }
        }, 10L);

        //runningTime
        int second = (int) ((System.currentTimeMillis() - startUpTime) / 1000);
        int minute = second / 60;
        second = second % 60;
        int hour = minute / 60;
        minute = minute % 60;


        return "==========" + date.toString() + "==========" + "\n" +
                "Running time : " + hour + "小時" + minute + "分" + second + "秒" + "\n" +
                "Thread (spigot/jvm/max) : " + t.getThreadCount() + "/" + t.getDaemonThreadCount() + "/" + t.getPeakThreadCount() + "\n" +
                "Memory (total/available/max)：" + tM + "/" + aM + "/" + mM + " MB" + "\n" +
                "CPU : " + cpu + " %" + "\n" +
                "Players : " + players + "\n" +
                "Chunks : " + chunks + "\n" +
                "TPS : " + tps + "\n" +
                "==========================================";
    }

}
