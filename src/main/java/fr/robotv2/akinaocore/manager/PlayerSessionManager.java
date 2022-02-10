package fr.robotv2.akinaocore.manager;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.PlayerSession;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class PlayerSessionManager implements Listener {

    private final AkinaoCore plugin;
    private final Map<ProxiedPlayer, PlayerSession> playerSessions = new HashMap<>();
    public PlayerSessionManager(AkinaoCore instance) {
        this.plugin = instance;
    }

    public void setSessions(ProxiedPlayer player, PlayerSession session) {
        playerSessions.put(player, session);
    }

    public PlayerSession getSession(ProxiedPlayer player) {
        return playerSessions.get(player);
    }

    public boolean hasSession(ProxiedPlayer player) {
        return playerSessions.containsKey(player);
    }

    public void register(ProxiedPlayer player) {
        playerSessions.put(player, new PlayerSession(plugin, player));
    }

    public void unregister(ProxiedPlayer player) {
        playerSessions.remove(player);
    }

    @EventHandler
    public void onConnect(PostLoginEvent event) {
        this.register(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        this.unregister(event.getPlayer());
    }
}
