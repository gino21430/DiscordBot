package me.monica.cat.dsb.listener;

import me.monica.cat.dsb.handler.DiscordMessageHandler;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class DiscordGuildMessageListener extends ListenerAdapter {
    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        DiscordMessageHandler discordMessageHandler = new DiscordMessageHandler();
        discordMessageHandler.handleGuildMessage(e.getAuthor(), e.getMessage().getContentDisplay());
    }
}
