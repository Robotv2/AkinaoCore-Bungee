package fr.robotv2.akinaocore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.CommandSender;

@CommandAlias("akinao-bungee|akb|money")
@CommandPermission("akinao.command.main")
public class AkinaoBungeeCommand extends BaseCommand {

    private final AkinaoCore plugin;
    public AkinaoBungeeCommand(AkinaoCore instance) {
        this.plugin = instance;
    }

    @Default
    public void onDefault(CommandSender sender) {
        StringUtil.sendMessage(sender, "&c&lUSAGE: &c/money <coins/akinaopoints> <give/take> <joueur> <valeur>", false);
    }

    @Subcommand("coins take")
    @CommandCompletion("@players")
    public void onCoinTake(CommandSender sender, String joueur, double value) {
        plugin.getCurrencyManager().take(joueur, Currency.COINS, value);
        StringUtil.sendMessage(sender, "&fVous venez de retirer &e" + value + " coins &7à &e" + joueur, true);
    }

    @Subcommand("coins give")
    @CommandCompletion("@players")
    public void onCoinGive(CommandSender sender, String joueur, double value) {
        plugin.getCurrencyManager().give(joueur, Currency.COINS, value);
        StringUtil.sendMessage(sender, "&fVous venez de donner &e" + value + " coins &7à &e" + joueur, true);
    }

    @Subcommand("akinaopoints take")
    @CommandCompletion("@players")
    public void onAkinaoTake(CommandSender sender, String joueur, double value) {
        plugin.getCurrencyManager().take(joueur, Currency.AKINAOPOINTS, value);
        StringUtil.sendMessage(sender, "&fVous venez de retirer &e" + value + " akinaopoint(s) &7à &e" + joueur, true);
    }

    @Subcommand("akinaopoints give")
    @CommandCompletion("@players")
    public void onAkinaoGive(CommandSender sender, String joueur, double value) {
        plugin.getCurrencyManager().give(joueur, Currency.AKINAOPOINTS, value);
        StringUtil.sendMessage(sender, "&fVous venez de donner &e" + value + " akinaopoint(s) &7à &e" + joueur, true);
    }
}
