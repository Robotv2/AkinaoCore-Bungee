package fr.robotv2.akinaocore.manager;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.impl.PlayerSession;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class CurrencyManager {

    private final AkinaoCore plugin;
    public CurrencyManager(AkinaoCore instance) {
        this.plugin = instance;
    }

    public void give(ProxiedPlayer player, Currency currency, Double value) {
        give(player.getName(), currency, value);
    }

    public void give(String playerName, Currency currency, Double value) {
        if(this.isAccessible(playerName)) {
            ProxiedPlayer target = plugin.getProxy().getPlayer(playerName);
            PlayerSession session = plugin.getPlayerSessionManager().getSession(target);
            session.giveCurrency(currency, value);
        } else {
            UUID playerUuid = plugin.getOfflinePlayerHandler().getUUID(playerName);
            plugin.getOfflinePlayerHandler().getDatabaseHandler()
                    .giveToOffline(playerUuid, currency, value);
        }
    }

    public void take(String playerName, Currency currency, Double value) {
        if(this.isAccessible(playerName)) {
            ProxiedPlayer target = plugin.getProxy().getPlayer(playerName);
            PlayerSession session = plugin.getPlayerSessionManager().getSession(target);
            session.takeCurrency(currency, value);
        } else {
            UUID playerUuid = plugin.getOfflinePlayerHandler().getUUID(playerName);
            plugin.getOfflinePlayerHandler().getDatabaseHandler()
                    .takeFromOffline(playerUuid, currency, value);
        }
    }

    public boolean isAccessible(String playerName) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);
        return player != null && player.isConnected()
                && plugin.getPlayerSessionManager().hasSession(player);
    }
}
