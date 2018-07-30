package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.handler.DiscordMessageHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class DiscordPrivateMessageListener extends ListenerAdapter {
    @SubscribeEvent
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        Message message = e.getMessage();
        DiscordMessageHandler discordMessageHandler = new DiscordMessageHandler();
        discordMessageHandler.handlePrivateMessage(message, e.getAuthor());
    }
}
