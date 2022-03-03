package fr.robotv2.akinaocore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("message|msg")
public class MsgCommand extends BaseCommand {

    @Default
    public void onMsg(ProxiedPlayer player, OnlinePlayer target, String[] messages) {
        StringBuilder builder = new StringBuilder();
        for(String message : messages) {
            builder.append(message).append(" ");
        }

        String format = player.getName() + ": " + builder;

        player.sendMessage(format);
        target.player.sendMessage(format);
    }
}
