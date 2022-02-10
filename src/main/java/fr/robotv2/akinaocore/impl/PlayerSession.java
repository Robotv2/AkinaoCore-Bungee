package fr.robotv2.akinaocore.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.AkinaoCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerSession {

    private final ProxiedPlayer player;
    private double coins;
    private double akinaoPoints;

    public PlayerSession(ProxiedPlayer player, double coins, double akinaoPoints) {
        this.player = player;
        this.coins = coins;
        this.akinaoPoints = akinaoPoints;
    }

    public PlayerSession(AkinaoCore akinaocore, ProxiedPlayer player) {
        this.player = player;
        this.coins = akinaocore.getDatabase().getConfiguration().getDouble(player.getUniqueId() + ".coins");
        this.akinaoPoints = akinaocore.getDatabase().getConfiguration().getDouble(player.getUniqueId() + ".akinao-points");
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double value) {
        coins = value;
    }

    public double getAkinaoPoints() {
        return akinaoPoints;
    }

    public void setAkinaoPoints(double value) {
        akinaoPoints = value;
    }

    public void sendDataToServer() {
        if(!player.isConnected()) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeDouble(coins);
        out.writeDouble(akinaoPoints);
        player.getServer().sendData(AkinaoCore.CHANNEL, out.toByteArray());
    }
}
