package fr.robotv2.akinaocore.utilities;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerUtil {

    public static void broadcast(ServerInfo info, String message) {
        for(ProxiedPlayer player : info.getPlayers()) {
            StringUtil.sendMessage(player, message, false);
        }
    }
}
