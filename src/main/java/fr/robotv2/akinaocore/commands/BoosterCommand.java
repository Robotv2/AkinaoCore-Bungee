package fr.robotv2.akinaocore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.booster.impl.Booster;
import fr.robotv2.akinaocore.booster.impl.BoosterType;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

@CommandAlias("booster")
@CommandPermission("akinao.command.booster")
public class BoosterCommand extends BaseCommand {

    private final AkinaoCore plugin;
    public BoosterCommand(AkinaoCore instance) {
        this.plugin = instance;
    }

    @Private
    @Subcommand("active")
    public void onActive(ProxiedPlayer player, String booster_uuid) {
        UUID boosterUUID;
        try {
            boosterUUID = UUID.fromString(booster_uuid);
        } catch (IllegalArgumentException e) {
            StringUtil.sendMessage(player, "&c" + booster_uuid + " n'est pas une UUID valide.", true);
            return;
        }

        if(!plugin.getBoosterManager().getUtilities().exist(boosterUUID)) {
            StringUtil.sendMessage(player, "&cCe booster n'existe pas ou n'est plus disponible.", true);
            return;
        }

        Booster booster = plugin.getBoosterManager().getUtilities().getBooster(boosterUUID);
        boolean success = plugin.getBoosterManager().activeBooster(player, booster);
        if(success)
            StringUtil.sendMessage(player, "&aVous venez d'activer un booster avec succès", true);
        else
            StringUtil.sendMessage(player, "&cVous avez déjà un booster pour ce mini-jeu d'actif.", true);
    }

    @Subcommand("list")
    @CommandCompletion("@minigame")
    public void onList(ProxiedPlayer player, @Optional MiniGame miniGame) {
        List<Booster> boosters;
        if(miniGame == null)
            boosters = plugin.getBoosterManager().getUtilities().getBoosters(player);
        else
            boosters = plugin.getBoosterManager().getUtilities().getBoosters(player, miniGame);

        if(boosters == null || boosters.isEmpty()) {
            StringUtil.sendMessage(player, "&cVous n'avez aucun boosters.", true);
            return;
        }

        for (Booster booster : boosters) {
            this.sendBoosterMessage(player, booster);
        }
    }

    private void sendBoosterMessage(ProxiedPlayer player, Booster booster) {
        TextComponent upper = new TextComponent(StringUtil.colorize("&e&lBOOSTER &8- &f" + booster.getMiniGame().getFormattedName()));
        upper.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, booster.getUniqueId().toString()));
        upper.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(booster.getUniqueId().toString()).color(ChatColor.GRAY).create()));
        player.sendMessage(upper);

        String personalText = booster.getType() == BoosterType.PERSONAL ? "&3Personnel" : "&3Global";
        TextComponent type = new TextComponent(StringUtil.colorize("&7Type: " + personalText));
        player.sendMessage(type);

        TextComponent pourcentage = new TextComponent(StringUtil.colorize("&7Pourcentage: &e+" + booster.getPourcentage() + "%"));
        player.sendMessage(pourcentage);

        TextComponent delay = new TextComponent(StringUtil.colorize("&7Durée: &d" + booster.getDelay() + "m"));
        player.sendMessage(delay);

        TextComponent active = new TextComponent(StringUtil.colorize("&eCliquez-ici pour activer le booster."));
        active.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Cliquez-ici pour activer le booster").color(ChatColor.YELLOW).create()));
        active.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/booster active " + booster.getUniqueId().toString()));
        player.sendMessage(active);

        player.sendMessage(" ");
    }
}
