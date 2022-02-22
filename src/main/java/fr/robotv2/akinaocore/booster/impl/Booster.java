package fr.robotv2.akinaocore.booster.impl;

import fr.robotv2.akinaocore.booster.BoosterUtilities;
import fr.robotv2.akinaocore.config.BungeeConfig;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.impl.MiniGame;
import net.md_5.bungee.config.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Booster {

    private final UUID id;
    private final UUID owner;

    private final MiniGame miniGame;
    private final Currency currency;
    private final BoosterType type;

    private final double pourcentage;
    private final int delay; // delay in minutes

    private long start = 0L;

    public Booster(UUID boosterId, UUID owner, MiniGame miniGame, double pourcentage, int delay, Currency currency, BoosterType type) {
        this.id = boosterId;
        this.owner = owner;
        this.miniGame = miniGame;
        this.currency = currency;
        this.type = type;
        this.pourcentage = pourcentage;
        this.delay = delay;
    }

    //<-- IDS -->

    public UUID getUniqueId() {
        return id;
    }

    public UUID getOwnerUniqueId() {
        return owner;
    }

    //<-- ENUMS -->

    public MiniGame getMiniGame() {
        return miniGame;
    }

    public BoosterType getType() {
        return type;
    }

    public Currency getCurrency() {
        return currency;
    }

    //<-- VALUE -->

    public double getPourcentage() {
        return pourcentage;
    }

    public int getDelay() {
        return delay;
    }


    //<-- START/END RELATED -->

    public void start() {
        start = System.currentTimeMillis();
    }

    public boolean hasStarted() {
        return start != 0;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        if(!hasStarted())
            return 0L;

        return (getDelay() * 1000L) + start;
    }

    public Long getRemainingMilli() {
        if(!hasStarted())
            return 0L;
        return getEnd() - System.currentTimeMillis();
    }

    public LocalDateTime getEndDate() {
        if(!hasStarted())
            return null;
        return Instant.ofEpochMilli(getEnd())
                .atZone(ZoneId.of("Europe/Paris")).toLocalDateTime();
    }

    //<-- DATABASE -->

    public void saveToDatabase() {
        BungeeConfig bungeeConfig = BoosterUtilities.getBoosterDatabase();
        Configuration configuration = bungeeConfig.getConfiguration();

        String path = "boosters." + id + ".";

        configuration.set(path + "owner", owner.toString());
        configuration.set(path + "type", type.toString());
        configuration.set(path + "mini-game", miniGame.toString());
        configuration.set(path + "currency", currency.toString());
        configuration.set(path + "pourcentage", pourcentage);
        configuration.set(path + "delay", delay);

        bungeeConfig.save();
    }

    public void removeFromDatabase() {
        BungeeConfig bungeeConfig = BoosterUtilities.getBoosterDatabase();
        Configuration configuration = bungeeConfig.getConfiguration();

        String path = "boosters." + id + ".";

        configuration.set(path + "owner", null);
        configuration.set(path + "type", null);
        configuration.set(path + "mini-game", null);
        configuration.set(path + "currency", null);
        configuration.set(path + "pourcentage", null);
        configuration.set(path + "delay", null);
        configuration.set("boosters." + id, null);

        bungeeConfig.save();
    }

    public String serialize() {
        return id.toString() + ";" +
                type.toString() + ";" +
                miniGame.toString() + ";" +
                pourcentage + ";" +
                delay;
    }
}
