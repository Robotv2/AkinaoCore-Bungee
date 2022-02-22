package fr.robotv2.akinaocore.manager;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.PlayerSession;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerSessionManager implements Listener {

    private final AkinaoCore plugin;
    private final Map<ProxiedPlayer, PlayerSession> playerSessions = new HashMap<>();
    public PlayerSessionManager(AkinaoCore instance) {
        this.plugin = instance;
    }

    public PlayerSession getSession(ProxiedPlayer player) {
        return playerSessions.get(player);
    }

    public boolean hasSession(ProxiedPlayer player) {
        return playerSessions.containsKey(player);
    }

    public void register(ProxiedPlayer player) {
        playerSessions.put(player, new PlayerSession(plugin, player));
        getSession(player).sendDataToServer();
    }

    public void unregister(ProxiedPlayer player) {
        getSession(player).saveToDatabase();
        playerSessions.remove(player);
    }

    public void unregisterAllPlayers() {
        plugin.getProxy().getPlayers().forEach(this::unregister);
    }

    public boolean isFirstJoin(ProxiedPlayer player) {
        return plugin.getDatabase().getConfiguration().get(player.getUniqueId() + ".coins") == null;
    }

    @EventHandler
    public void onConnect(PostLoginEvent event) {
        plugin.getProxy().getScheduler().schedule(plugin,() -> {

            ProxiedPlayer player = event.getPlayer();
            if(isFirstJoin(player))
                plugin.getWelcomeManager().onNewPlayerJoin(player);
            this.register(player);

        }, 500, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        this.unregister(event.getPlayer());
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if(this.hasSession(player))
            getSession(event.getPlayer()).sendDataToServer();
        else
            this.register(player);
    }
}
