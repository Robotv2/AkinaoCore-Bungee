package fr.robotv2.akinaocore.utilities;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.MiniGame;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class ServerUtil {

    public static void broadcast(ServerInfo info, String message) {
        for(ProxiedPlayer player : info.getPlayers()) {
            StringUtil.sendMessage(player, message, false);
        }
    }

    public static void broadcast(String message) {
        getServers().values().forEach(serverInfo -> broadcast(serverInfo, message));
    }

    public static Map<String, ServerInfo> getServers() {
        return AkinaoCore.getInstance().getProxy().getServers();
    }

    public static int getTotalPlayer(MiniGame miniGame) {
        return getServers().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(miniGame.getServerPrefix()))
                .mapToInt(entry -> entry.getValue().getPlayers().size())
                .sum();
    }
}
