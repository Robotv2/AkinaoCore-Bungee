package fr.robotv2.akinaocore.booster;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.booster.impl.Booster;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.utilities.ServerUtil;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterThanks {

    private final AkinaoCore plugin;
    private final BoosterManager manager;

    //Player - Booster
    private final Map<UUID, List<UUID>> thanks = new ConcurrentHashMap<>();

    public BoosterThanks(AkinaoCore instance, BoosterManager manager) {
        this.plugin = instance;
        this.manager = manager;
    }

    public void activeThanks(ProxiedPlayer player, Booster booster) {
        ServerUtil.broadcast("&6&m&l---------------------------");
        ServerUtil.broadcast("&fLe joueur &e" + player.getName() + "&f vient d'activer un");
        ServerUtil.broadcast("&fbooster &e&l+" + booster.getPourcentage() + "&f pour le mini-jeu");
        ServerUtil.broadcast("&e" + booster.getMiniGame().getFormattedName() + " &fpendant &e"+ booster.getDelay() + "m");
        ServerUtil.broadcast("&6&m&l---------------------------");
    }

    public boolean canThank(ProxiedPlayer player, Booster booster) {
        if(!manager.getActiveGlobalBoosters().contains(booster))
            return false;
        if(booster.getOwnerUniqueId().equals(player.getUniqueId()))
            return false;
        if(!thanks.containsKey(player.getUniqueId()))
            return true;
        return !thanks.get(player.getUniqueId()).contains(booster.getUniqueId());
    }

    public void thank(ProxiedPlayer player, Booster booster) {
        if(this.canThank(player, booster)) {

            List<UUID> boosters = this.thanks.get(player.getUniqueId());
            if(boosters == null)
                boosters = new ArrayList<>();
            boosters.add(booster.getUniqueId());
            this.thanks.put(player.getUniqueId(), boosters);

            plugin.getCurrencyManager().give(player, Currency.COINS, 50D);

            ProxiedPlayer target = plugin.getProxy().getPlayer(booster.getOwnerUniqueId());
            if(target != null && target.isConnected()) {
                StringUtil.sendMessage(player, "&aVous venez de remercier " + target.getName() + " et vous avez gagné: &e+50 coins.", true);
                plugin.getCurrencyManager().give(target, Currency.COINS, 50D);
                StringUtil.sendMessage(target, "&a" + player.getName() + " vient de vous remercier et vous avez gagné: &e+50 coins", true);
            } else {
                String targetName = plugin.getOfflinePlayerHandler().getName(booster.getUniqueId());
                StringUtil.sendMessage(player, "&aVous venez de remercier " + targetName + " et vous avez gagné: &e+50 coins.", true);
            }
        }
    }

    public void thanksAll(ProxiedPlayer player) {
        if(manager.getActiveGlobalBoosters().isEmpty()) {
            StringUtil.sendMessage(player, "&cAucun booster global en cours.", true);
            return;
        }
        manager.getActiveGlobalBoosters().forEach(booster -> thank(player, booster));
        StringUtil.sendMessage(player, "&aVous venez de remercier toutes les personnes que vous pouviez remercier.", true);
    }
}
