package fr.robotv2.akinaocore.booster;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.booster.impl.Booster;
import fr.robotv2.akinaocore.booster.impl.BoosterType;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.serializer.BoosterSerializer;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BoosterManager {

    private final AkinaoCore plugin;
    private final BoosterUtilities boosterUtilities;
    private final Map<MiniGame, Booster> activeGlobalBooster = new HashMap<>();
    private final Map<UUID, List<Booster>> activePersonalBooster = new HashMap<>();

    public BoosterManager(AkinaoCore instance) {
        this.plugin = instance;
        this.boosterUtilities = new BoosterUtilities(instance);
    }

    public BoosterUtilities getUtilities() {
        return boosterUtilities;
    }

    public void sendBoosters(ProxiedPlayer player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("get-booster");
        out.writeUTF(BoosterSerializer.serializePlayerBoosters(player));
        out.writeUTF(BoosterSerializer.serializeActiveBoosters(player));
        player.getServer().sendData(AkinaoCore.LOBBY_CHANNEL, out.toByteArray());
    }

    public List<Booster> getActiveBoosters(ProxiedPlayer player) {
        List<Booster> result = activePersonalBooster.get(player.getUniqueId());
        if(result == null)
            result = new ArrayList<>();
        result.addAll(activeGlobalBooster.values());
        return result;
    }

    public List<Booster> getActiveBoosters(ProxiedPlayer player, MiniGame miniGame) {
        List<Booster> activeBoosters = getActiveBoosters(player);
        return activeBoosters.stream()
                .filter(booster -> Objects.equals(booster.getMiniGame(), miniGame))
                .collect(Collectors.toList());
    }

    public boolean activeBooster(ProxiedPlayer player, Booster booster) {
        if(this.isPersonal(booster))
            return activatePersonalBooster(player, booster);
        else
            return activateGlobalBooster(booster);
    }

    private boolean activateGlobalBooster(Booster globalBooster) {
        Booster current = activeGlobalBooster.get(globalBooster.getMiniGame());
        if(current == null && !this.isPersonal(globalBooster)) {
            globalBooster.start();
            activeGlobalBooster.put(globalBooster.getMiniGame(), globalBooster);
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                activeGlobalBooster.remove(globalBooster.getMiniGame());
            }, globalBooster.getDelay(), TimeUnit.MINUTES);
            getUtilities().removeBooster(globalBooster);
            return true;
        } else {
            return false;
        }
    }

    private boolean activatePersonalBooster(ProxiedPlayer player, Booster current) {
        List<Booster> personalBoosters = activePersonalBooster.get(player.getUniqueId());

        if(personalBoosters == null) {
            activePersonalBooster.put(player.getUniqueId(), new ArrayList<>());
            return activatePersonalBooster(player, current);
        }

        Optional<Booster> boosterOptional = personalBoosters
                .stream()
                .filter(personalBooster -> Objects.equals(personalBooster.getMiniGame(), current.getMiniGame()))
                .findAny();

        if(boosterOptional.isPresent())
            return false;

        if(!this.isPersonal(current))
            return false;

        current.start();
        personalBoosters.add(current);
        activePersonalBooster.put(player.getUniqueId(), personalBoosters);

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            List<Booster> personalBoosters1 = activePersonalBooster.get(player.getUniqueId());
            personalBoosters1.remove(current);
            activePersonalBooster.put(player.getUniqueId(), personalBoosters1);
            if(player.isConnected())
                StringUtil.sendMessage(player, "&cL'un de vos boosters vient de s'arrêter.", true);
        }, current.getDelay(), TimeUnit.MINUTES);
        getUtilities().removeBooster(current);
        return true;
    }

    public double applyBoosters(MiniGame miniGame, ProxiedPlayer player, double start) {
        if(!player.isConnected())
            return start;

        double personalPourcentage = getPersonalPourcentage(player, miniGame);
        double globalPourcentage = getGlobalPourcentage(miniGame);

        double totalPourcentage = personalPourcentage + globalPourcentage;

        if(totalPourcentage == 0)
            return start;
        else
            return start * (1 * totalPourcentage / 100);
    }

    public double getPersonalPourcentage(ProxiedPlayer player, MiniGame miniGame) {
        List<Booster> personalBoosters = activePersonalBooster.get(player.getUniqueId());
        if(personalBoosters != null && !personalBoosters.isEmpty()) {
            Optional<Booster> boosterOptional = personalBoosters
                    .stream()
                    .filter(personalBooster -> personalBooster.getMiniGame().equals(miniGame))
                    .findAny();

            if(boosterOptional.isPresent()) {
                return boosterOptional.get().getPourcentage();
            }
        }
        return 0D;
    }

    public double getGlobalPourcentage(MiniGame miniGame) {
        //Add global pourcentage
        Booster globalBooster = activeGlobalBooster.get(miniGame);
        if(globalBooster != null)
            return globalBooster.getPourcentage();
        else
            return 0D;
    }

    public boolean isPersonal(Booster booster) {
        return booster.getType() == BoosterType.PERSONAL;
    }
}
