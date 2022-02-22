package fr.robotv2.akinaocore.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.AkinaoCore;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LobbyChanger {

    private final AkinaoCore plugin;
    public LobbyChanger(AkinaoCore instance) {
        this.plugin = instance;
    }

    public Set<ServerInfo> getLobbiesServers() {
        return plugin.getProxy().getServers().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("lobby-"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    public void sendLobbies(ProxiedPlayer player) {
        if(!player.isConnected()) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("get-lobbies");
        out.writeUTF(this.formatLobbies(getLobbiesServers()));
        player.getServer().sendData(AkinaoCore.LOBBY_CHANNEL, out.toByteArray());
    }

    private String formatLobbies(Set<ServerInfo> lobbies) {

        StringBuilder builder = new StringBuilder();
        int count = 0;
        int totalServer = lobbies.size();

        for (ServerInfo lobby : lobbies) {
            count++;
            builder.append(lobby.getName()).append("!").append(lobby.getPlayers().size());
            if(count != totalServer) {
                builder.append(";");
            }
        }
        return builder.toString();
    }
}
