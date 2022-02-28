package fr.robotv2.akinaocore.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.utilities.ServerUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NPCmanager {

    public static void actualizeCounts(ProxiedPlayer player) {
        for (MiniGame value : MiniGame.values()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("actualize-count");
            out.writeUTF(value.toString());
            out.writeInt(ServerUtil.getTotalPlayer(value));
            player.getServer().sendData(AkinaoCore.LOBBY_CHANNEL, out.toByteArray());
        }
    }
}
