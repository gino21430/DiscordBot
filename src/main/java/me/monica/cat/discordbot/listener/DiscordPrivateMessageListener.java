package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.Main;
import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.List;

public class DiscordPrivateMessageListener extends ListenerAdapter {
    @SubscribeEvent
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        Message message = e.getMessage();
        List<Message.Attachment> list = message.getAttachments();
        if (list != null) {
            for (Message.Attachment a : list)
                Main.log("FileName: " + a.getFileName() + ",size: " + a.getSize() + ",url: " + a.getUrl());
        }
        DiscordMessageHandler discordMessageHandler = new DiscordMessageHandler();
        discordMessageHandler.handlePrivateMessage(message, e.getAuthor(), e.getChannel());

    }
}
