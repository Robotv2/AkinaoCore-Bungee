package fr.robotv2.akinaocore.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.impl.PlayerSession;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageListener implements Listener {

    private final AkinaoCore plugin;
    public PluginMessageListener(AkinaoCore instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        String tag = event.getTag();
        if(!tag.equalsIgnoreCase(AkinaoCore.CORE_CHANNEL)
                && !tag.equalsIgnoreCase(AkinaoCore.LOBBY_CHANNEL)) return;

        final ProxiedPlayer player = plugin.getProxy().getPlayer(event.getReceiver().toString());
        final ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        final String sub = in.readUTF();

        switch(event.getTag().toLowerCase()) {
            case AkinaoCore.CORE_CHANNEL:
                mainChannelMessage(player, in, sub);
                break;
            case AkinaoCore.LOBBY_CHANNEL:
                lobbyChannelMessage(player, in, sub);
                break;
        }
    }

    private void mainChannelMessage(ProxiedPlayer player, ByteArrayDataInput in, String sub) {
        PlayerSession session = plugin.getPlayerSessionManager().getSession(player);
        switch (sub.toLowerCase()) {
            case "player-session-save":
                double coins = in.readDouble();
                double akinaoPoints = in.readDouble();
                session.setCoins(coins);
                session.setAkinaoPoints(akinaoPoints);
                break;
            case "reward-player":
                MiniGame miniGame = MiniGame.valueOf(in.readUTF().toUpperCase());
                Currency currency = Currency.valueOf(in.readUTF().toUpperCase());
                double value = in.readDouble();
                session.rewardPlayer(miniGame, currency, value);
                break;
        }
    }

    private void lobbyChannelMessage(ProxiedPlayer player, ByteArrayDataInput in, String sub) {
        switch (sub.toLowerCase()) {
            case "ask-lobbies-gui":
                plugin.getLobbyChanger().sendLobbies(player);
                break;
            case "ask-booster-gui":
                plugin.getBoosterManager().sendBoosters(player);
                break;
        }
    }
}
