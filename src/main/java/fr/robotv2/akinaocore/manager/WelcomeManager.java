package fr.robotv2.akinaocore.manager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.utilities.ServerUtil;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@CommandAlias("bienvenue|bvn")
@CommandPermission("akinao.command.bienvenue")
public class WelcomeManager extends BaseCommand {

    private final AkinaoCore plugin;
    private final Map<UUID, List<ProxiedPlayer>> welcomes = new ConcurrentHashMap<>();

    public WelcomeManager(AkinaoCore instance) {
        this.plugin = instance;
    }

    @Default
    @CommandCompletion("@nothing")
    public void onWelcome(ProxiedPlayer player) {
        if(!getServersTarget().contains(player.getServer().getInfo())) {
            StringUtil.sendMessage(player, "&cVous ne pouvez souhaiter la bienvenue aux joueurs depuis ce serveur.", true);
            return;
        }
        welcomePlayers(player);
    }

    private Set<ServerInfo> getServersTarget() {
        return plugin.getLobbyChanger().getLobbiesServers();
    }

    private void broadcastJoinMessage(ProxiedPlayer player) {
        for (ServerInfo serverInfo : getServersTarget()) {
            ServerUtil.broadcast(serverInfo, "&7> &fLe joueur &e" + player.getName() + " &fvient de se connecter pour la &epremière fois !");
            ServerUtil.broadcast(serverInfo, "&7> &fN'oubliez pas de lui souhaiter la bienvenue ! &e/bienvenue");
        }
    }

    private List<ProxiedPlayer> getPlayersToWelcome(ProxiedPlayer sender) {
        return welcomes.keySet().stream()
                .map(uuid -> plugin.getProxy().getPlayer(uuid))
                .filter(Objects::nonNull)
                .filter(target -> !Objects.equals(target.getUniqueId(), sender.getUniqueId()))
                .filter(target -> hasWelcome(sender, target))
                .collect(Collectors.toList());

    }

    public void onNewPlayerJoin(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        welcomes.put(uuid, new ArrayList<>());
        broadcastJoinMessage(player);
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            welcomes.remove(uuid);
        }, 20, TimeUnit.SECONDS);
    }

    public boolean hasWelcome(ProxiedPlayer sender, ProxiedPlayer target) {
        return welcomes.get(target.getUniqueId()).contains(sender);
    }

    public void welcomePlayer(ProxiedPlayer sender, ProxiedPlayer target) {
        if(hasWelcome(sender, target))
            return;
        if(sender.getUniqueId().equals(target.getUniqueId()))
            return;

        List<ProxiedPlayer> players = welcomes.get(target.getUniqueId());
        players.add(sender);
        welcomes.put(target.getUniqueId(), players);

        plugin.getCurrencyManager().give(sender, Currency.COINS, 50D);
        plugin.getCurrencyManager().give(sender, Currency.AKINAOPOINTS, 0.5D);

        StringUtil.sendMessage(sender, "&fVous venez de souhaiter la &ebienvenue &fà &e" + target.getName(), true);
    }

    public void welcomePlayers(ProxiedPlayer sender) {
        List<ProxiedPlayer> playerToWelcome = this.getPlayersToWelcome(sender);

        if(playerToWelcome.isEmpty()) {
            StringUtil.sendMessage(sender, "&cVous n'avez personne à qui souhaiter la bienvenue.", true);
            return;
        }

        playerToWelcome.forEach(target -> welcomePlayer(sender, target));
    }
}
