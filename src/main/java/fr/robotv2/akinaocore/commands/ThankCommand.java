package fr.robotv2.akinaocore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import fr.robotv2.akinaocore.AkinaoCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("merci")
public class ThankCommand extends BaseCommand {

    @Default
    public void onThank(ProxiedPlayer player) {
        AkinaoCore.getInstance().getBoosterManager().getThanks().thanksAll(player);
    }
}
