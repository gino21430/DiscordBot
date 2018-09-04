package me.monica.cat.discordbot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Set;

public class CustomCommandSender implements ConsoleCommandSender {

    private Main main;
    private ConsoleCommandSender console;

    CustomCommandSender(Main main, ConsoleCommandSender console) {
        this.main = main;
        this.console = console;
    }

    @Override
    public void sendMessage(String message) {
        Main.log("Response: " + message);
        console.sendMessage(message);
        main.commandResponse(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        Main.log("Response: " + Arrays.toString(messages));
        console.sendMessage(messages);
        main.commandResponse(Arrays.toString(messages));
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return console.getName();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return console.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return console.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return console.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return console.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {

    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String input) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {

    }

    @Override
    public void sendRawMessage(String message) {
        Main.log("Response Raw: " + message);
        main.commandResponse(message);
    }
}
