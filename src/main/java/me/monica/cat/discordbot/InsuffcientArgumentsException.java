package me.monica.cat.discordbot;

import org.bukkit.command.CommandSender;

class InsuffcientArgumentsException extends Exception {

    private int num;

    InsuffcientArgumentsException(int num) {
        super("參數不足!");
        this.num = num;
    }

    void warn(CommandSender sender) {
        sender.sendMessage("§cPlease enter " + num + " arguments§r");
    }
}
