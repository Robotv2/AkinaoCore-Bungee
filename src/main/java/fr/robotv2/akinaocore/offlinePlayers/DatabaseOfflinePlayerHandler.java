package fr.robotv2.akinaocore.offlinePlayers;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.Currency;

import java.util.UUID;

public class DatabaseOfflinePlayerHandler {

    private final AkinaoCore plugin;
    public DatabaseOfflinePlayerHandler(AkinaoCore instance) {
        this.plugin = instance;
    }

    public void save() {
        plugin.getDatabase().save();
    }

    public double getFromOffline(UUID playerUUID, Currency currency) {
        return plugin.getDatabase().getConfiguration().getDouble(playerUUID.toString() + "." + currency.getPath());
    }

    public void setToOffline(UUID uuid, Currency currency, double value) {
        plugin.getDatabase().getConfiguration().set(uuid.toString() + "." + currency.getPath(), value);
        this.save();
    }

    public void giveToOffline(UUID uuid, Currency currency, double value) {
        double current = getFromOffline(uuid, currency);
        setToOffline(uuid, currency, value + current);
    }

    public void takeFromOffline(UUID uuid, Currency currency, double value) {
        double current = getFromOffline(uuid, currency);
        setToOffline(uuid, currency, value - current);
    }
}
