package fr.robotv2.akinaocore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.booster.BoosterUtilities;
import fr.robotv2.akinaocore.booster.impl.Booster;
import fr.robotv2.akinaocore.booster.impl.BoosterType;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@CommandAlias("ba|boosteradmin|booster-admin")
@CommandPermission("akinao.command.booster-admin")
public class BoosterAdminCommand extends BaseCommand {

    private final AkinaoCore plugin;
    public BoosterAdminCommand(AkinaoCore instance) {
        this.plugin = instance;
    }

    @Subcommand("create")
    @CommandCompletion("@booster @minigame @range:0-100 @range:0-60")
    public void onCreate(CommandSender sender, BoosterType type, MiniGame miniGame, double pourcentage, int delay, @Optional OnlinePlayer onlinePlayer) {
        if(onlinePlayer == null && !(sender instanceof ProxiedPlayer)) {
            StringUtil.sendMessage(sender, "&cVous devez spécifier un joueur.", true);
            return;
        }

        ProxiedPlayer target;

        if(onlinePlayer == null)
            target = (ProxiedPlayer) sender;
        else
            target = onlinePlayer.getPlayer();

        BoosterUtilities.BoosterBuilder builder = new BoosterUtilities.BoosterBuilder()
                .setOwner(target.getUniqueId())
                .setType(type)
                .setMiniGame(miniGame)
                .setPourcentage(pourcentage)
                .setDelay(delay);

        Booster booster = builder.build();
        StringUtil.sendMessage(sender, "&7Vous venez de &ecréer &7et &edonner &7un booster à &e" + target.getName(), true);
    }

    @Subcommand("delete")
    public void onDelete(CommandSender sender, String booster_uuid) {
        UUID boosterUUID;
        try {
            boosterUUID = UUID.fromString(booster_uuid);
        } catch (IllegalArgumentException e) {
            StringUtil.sendMessage(sender, "&c" + booster_uuid + " n'est pas une UUID valide.", true);
            return;
        }

        if(!plugin.getBoosterManager().getUtilities().exist(boosterUUID)) {
            StringUtil.sendMessage(sender, "&cCe booster n'existe pas ou n'est plus disponible.", true);
            return;
        }

        Booster booster = plugin.getBoosterManager().getUtilities().getBooster(boosterUUID);
        plugin.getBoosterManager().getUtilities().removeBooster(booster);
        StringUtil.sendMessage(sender, "&cVous venez de supprimer un booster.", true);
    }
}
