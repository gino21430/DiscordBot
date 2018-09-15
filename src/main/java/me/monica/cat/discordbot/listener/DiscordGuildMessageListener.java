package me.monica.cat.discordbot.listener;


import me.monica.cat.discordbot.Main;
import me.monica.cat.discordbot.handler.DiscordMessageHandler;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class DiscordGuildMessageListener extends ListenerAdapter {

    private OutputStreamWriter writer;

    public DiscordGuildMessageListener(OutputStreamWriter writer) {
        this.writer = writer;
    }

    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        DiscordMessageHandler discordMessageHandler = new DiscordMessageHandler();
        discordMessageHandler.handleGuildMessage(e.getMessage());
        try {
            writer.append(e.getMessage().getContentStripped()).append("\n");
        } catch (IOException e1) {
            Main.log("Error while write DiscordLog!");
        }
    }
}
