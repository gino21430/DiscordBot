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


    public static void init() {
        tps = 0;
        updatePeriod = Main.getPlugin().config.getInt("UpdatePeriod");
        if (updatePeriod < 10) updatePeriod = 10;
    }

    public void runTimerTask() {
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
                channel.sendMessage(getData()).queue();
            }
        };
        timer.schedule(task, 20 * 1000, updatePeriod * 1000);
    }


    private String getData() {
        Runtime lRuntime = Runtime.getRuntime();
        int aM = (int) lRuntime.freeMemory() / 1024 / 1024;
        int mM = (int) lRuntime.maxMemory() / 1024 / 1024;
        int tM = (int) lRuntime.totalMemory() / 1024 / 1024;
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        java.lang.management.ThreadMXBean t = ManagementFactory.getThreadMXBean();
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
                if (tps > 20.0) tps = 20.0;
            }
        }, 10L);

        return color(t.getThreadCount(), t.getDaemonThreadCount(), t.getPeakThreadCount(),
                tM, aM, mM, cpu, players, chunks);
    }

    private String color(int t1, int t2, int t3, int tM, int aM, int mM, String cpu, int players, int chunks) {
        Date date = new Date();
        String info = "==========" + date.toString() + "==========" + "\n" +
                "spigot/jvm/max : %s/%s/%s\n" +
                "Memory (total/available/max)：%s/%s/%s MB" + "\n" +
                "CPU : %s %" + "\n" +
                "Players : %s\n" +
                "Chunks : %s\n" +
                "TPS : %s\n" +
                "==========================================";
        //if (tM > mM * 0.8)
        return String.format(info, t1, t2, t3);
    }

}
