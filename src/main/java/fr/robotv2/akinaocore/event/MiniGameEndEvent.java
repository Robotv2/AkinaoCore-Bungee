package fr.robotv2.akinaocore.event;

import fr.robotv2.akinaocore.impl.MiniGame;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class MiniGameEndEvent extends Event {

    private final ProxiedPlayer player;
    private final MiniGame game;

    public MiniGameEndEvent(ProxiedPlayer player, MiniGame game) {
        this.player = player;
        this.game = game;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public MiniGame getGame() {
        return game;
    }
}
