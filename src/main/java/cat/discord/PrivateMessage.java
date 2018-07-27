package cat.discord;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

public class PrivateMessage extends ListenerAdapter {

    private String RanInt() {
        Random random = new Random();
        int i = random.nextInt(999);
        if (i>=100) return String.valueOf(i);
        if (i>=10) return "0"+i;
        return "00"+i;
    }

    Map<String,String> vMap = new HashMap<>();
    private Thread thread;
    private TimerTask timerTask;

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        String msg = e.getMessage().getContentStripped();
        User author = e.getAuthor();
        if (!msg.equals("verify")) return;
        String code = RanInt();
        vMap.put(code,author.getId());

        thread = new Thread(()-> e.getChannel().sendMessage("驗證成功").queue());
        timerTask = new TimerTask() {
            @Override
            public void run() {
                thread.interrupt();
                thread = null;
                e.getChannel().sendMessage("驗證失敗").queue();
            }
        };
        try {
            thread.wait();
            Timer timer = new Timer();
            timer.schedule(timerTask,60*1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    void VerifyPass() {
        timerTask.cancel();
        thread.notify();
    }

}
