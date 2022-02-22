package fr.robotv2.akinaocore.offlinePlayers;

import fr.robotv2.akinaocore.AkinaoCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OfflinePlayersHandler implements Listener {

    private final DatabaseOfflinePlayerHandler databaseHandler;
    private final Map<String, UUID> uuids = new HashMap<>();

    public OfflinePlayersHandler(AkinaoCore instance) {
        this.databaseHandler = new DatabaseOfflinePlayerHandler(instance);
        instance.getProxy().getPluginManager().registerListener(instance, this);
    }

    @EventHandler
    public void postLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        System.out.println(player.getUniqueId() + " " + this.getUUID(player.getName()));

        uuids.put(player.getName(), player.getUniqueId());
    }

    @NotNull
    public UUID getUUID(String playerName) {
        if(uuids.containsKey(playerName)) {
            return uuids.get(playerName);
        } else {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName)
                    .getBytes(StandardCharsets.UTF_8));
        }
    }

    @Nullable
    public String getName(UUID playerUUID) {
        if(!uuids.containsValue(playerUUID))
            return null;

        Optional<Map.Entry<String, UUID>> optional =
                uuids.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().equals(playerUUID))
                        .findFirst();

        return optional.map(Map.Entry::getKey).orElse(null);
    }

    public DatabaseOfflinePlayerHandler getDatabaseHandler() {
        return databaseHandler;
    }
}
