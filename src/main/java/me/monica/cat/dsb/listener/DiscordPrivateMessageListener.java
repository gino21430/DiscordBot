package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.handler.DiscordMessageHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import java.util.*;
import me.monica.cat.dsb.*;

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
