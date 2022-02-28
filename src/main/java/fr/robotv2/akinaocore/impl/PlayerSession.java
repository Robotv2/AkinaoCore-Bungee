package fr.robotv2.akinaocore.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.event.MiniGameEndEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerSession {

    private final ProxiedPlayer player;
    private final AkinaoCore plugin;
    private double coins;
    private double akinaoPoints;

    public PlayerSession(AkinaoCore instance, ProxiedPlayer player, double coins, double akinaoPoints) {
        this.plugin = instance;
        this.player = player;
        this.coins = coins;
        this.akinaoPoints = akinaoPoints;
    }

    public PlayerSession(AkinaoCore instance, ProxiedPlayer player) {
        this.plugin = instance;
        this.player = player;
        this.coins = instance.getDatabase().getConfiguration().getDouble(player.getUniqueId() + ".coins");
        this.akinaoPoints = instance.getDatabase().getConfiguration().getDouble(player.getUniqueId() + ".akinao-points");
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double value) {
        coins = value;
        saveToDatabase();
    }

    public double getAkinaoPoints() {
        return akinaoPoints;
    }

    public void setAkinaoPoints(double value) {
        akinaoPoints = value;
        saveToDatabase();
    }

    public void rewardPlayer(MiniGame miniGame, Currency currency, double value) {
        value = AkinaoCore.getInstance()
                .getBoosterManager().applyBoosters(miniGame, player, value);
        switch (currency) {
            case AKINAOPOINTS:
                this.setAkinaoPoints(akinaoPoints + value);
                break;
            case COINS:
                this.setCoins(coins + value);
                break;
        }
        plugin.getProxy().getPluginManager().callEvent(new MiniGameEndEvent(player, miniGame));
        sendDataToServer();
    }

    public void giveCurrency(Currency currency, double value) {
        switch (currency) {
            case COINS:
                this.setCoins(coins + value);
                break;
            case AKINAOPOINTS:
                this.setAkinaoPoints(akinaoPoints + value);
                break;
        }
        sendDataToServer();
    }

    public void takeCurrency(Currency currency, double value) {
        switch (currency) {
            case COINS:
                this.setCoins(coins - value);
                break;
            case AKINAOPOINTS:
                this.setAkinaoPoints(akinaoPoints - value);
                break;
        }
        sendDataToServer();
    }

    public void saveToDatabase() {
        plugin.getDatabase().getConfiguration().set(player.getUniqueId() + ".coins", coins);
        plugin.getDatabase().getConfiguration().set(player.getUniqueId() + ".akinao-points", akinaoPoints);
        plugin.getDatabase().save();
    }

    public void sendDataToServer() {
        if(!player.isConnected() || player.getServer() == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("register-player");
        out.writeDouble(coins);
        out.writeDouble(akinaoPoints);
        player.getServer().sendData(AkinaoCore.CORE_CHANNEL, out.toByteArray());
    }
}
