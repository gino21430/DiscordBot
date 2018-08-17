package me.monica.cat.discordbot.handler;

import com.sun.management.OperatingSystemMXBean;
import me.monica.cat.discordbot.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class ServerStatusHandler {

    private static final long startUpTime = System.currentTimeMillis();
    private static double tps = 0;
    private static int updatePeriod;
    //private static Set<String> opList = new HashSet<>();
    private static Set<String> playerList = new HashSet<>();


    public static void init() {
        updatePeriod = Main.getPlugin().config.getInt("UpdatePeriod");
        if (updatePeriod < 30) updatePeriod = 30;
    }

    public void runTimerTask() {
        Timer timer = new Timer();
        TextChannel channel = Main.getPlugin().getJda().getTextChannelById(Main.getPlugin().config.getString("StatusChannel"));
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
        Main.log("updatePeriod: " + updatePeriod);
        timer.schedule(task, 30 * 1000, updatePeriod * 1000);
    }


    private String getData() {
        Runtime lRuntime = Runtime.getRuntime();
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        java.lang.management.ThreadMXBean t = ManagementFactory.getThreadMXBean();
        Date date = new Date();

        //Memory
        long aM = lRuntime.freeMemory() / 1024 / 1024;
        long mM = lRuntime.maxMemory() / 1024 / 1024;
        long tM = lRuntime.totalMemory() / 1024 / 1024;

        //cpu
        DecimalFormat df = new DecimalFormat("#.##");
        String cpu = df.format(osmb.getSystemCpuLoad() * 100);

        //chunks
        int chunks = 0;
        boolean bl = true;
        while (bl) {
            try {
                for (World world : getServer().getWorlds())
                    chunks += world.getLoadedChunks().length;
                bl = false;
            } catch (NullPointerException e) {
                Main.log("Chunks are NULL .....");
            }
        }
        int players = Bukkit.getOnlinePlayers().size();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            if (!player.isOp()) playerList.add(player.getName());
        });
        //StringBuilder opListStr = new StringBuilder();
        StringBuilder playerListStr = new StringBuilder();
        //Iterator iter1 = opList.iterator();
        Iterator iter2 = playerList.iterator();
        /*
        while (true) {
            if (iter1.hasNext()) opListStr.append(String.format("%-16s", iter1.next()));
            else {
                opListStr.append("\n");
                break;
            }
            if (iter1.hasNext()) {
                opListStr.append(String.format("\t%-16s", iter1.next()));
                opListStr.append("\n");
            } else {
                opListStr.append("\n");
                break;
            }
        }
        */
        while (true) {
            if (iter2.hasNext()) playerListStr.append(String.format("%-16s", iter2.next()));
            else {
                playerListStr.append("\n");
                break;
            }
            if (iter2.hasNext()) {
                playerListStr.append(String.format("\t%-16s", iter2.next()));
                playerListStr.append("\n");
            } else {
                playerListStr.append("\n");
                break;
            }
        }

        //tps
        long startTime = System.currentTimeMillis();
        try {
            getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                synchronized (this) {
                    double tmp = System.currentTimeMillis() - startTime;
                    tps = 10.0 * 1000 / tmp;
                    if (tps > 20.00) tps = 20.00;
                }
            }, 10L);
        } catch (IllegalPluginAccessException e) {
            getLogger().info("Plugin is disabling!\nStop update status!");
        }

        //runningTime
        int second = (int) ((System.currentTimeMillis() - startUpTime) / 1000);
        int minute = second / 60;
        second = second % 60;
        int hour = minute / 60;
        minute = minute % 60;


        return "========" + date.toString() + "========" + "\n" +
                "Running time : " + hour + "小時" + minute + "分" + second + "秒" + "\n" +
                "Thread (spigot/jvm/max) : " + t.getThreadCount() + " / " + t.getDaemonThreadCount() + " / " + t.getPeakThreadCount() + "\n" +
                "Memory (total/available/max)：" + tM + "/" + aM + "/" + mM + " MB" + "\n" +
                "CPU : " + cpu + " %" + "\n" +
                "Chunks : " + chunks + "\n" +
                "TPS : " + tps + "\n" +
                "Players : " + players + "\n" +
                "======== Player : " + playerList.size() + " ========\n" +
                playerListStr.toString();
    }

}
