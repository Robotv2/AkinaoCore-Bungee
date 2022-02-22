package fr.robotv2.akinaocore.serializer;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.booster.impl.Booster;
import fr.robotv2.akinaocore.booster.impl.BoosterType;
import fr.robotv2.akinaocore.impl.MiniGame;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class BoosterSerializer {

    public static String serializePlayerBoosters(ProxiedPlayer player) {
        List<Booster> boosters = AkinaoCore.getInstance().getBoosterManager().getUtilities().getBoosters(player);
        List<String> boostersStrings = boosters.stream()
                .map(Booster::serialize).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();

        int count = 0;
        int size = boostersStrings.size();

        for(String boosterStr : boostersStrings) {
            count++;
            builder.append(boosterStr);
            if(count != size)
                builder.append("!");
        }
        return builder.toString();
    }

    public static String serializeActiveBoosters(ProxiedPlayer player) {

        StringBuilder builder = new StringBuilder();
        int size = MiniGame.values().length;
        int count = 0;

        for(MiniGame miniGame : MiniGame.values()) {
            count++;
            double global = 0;
            double personal = 0;

            List<Booster> boosters = AkinaoCore.getInstance()
                    .getBoosterManager().getActiveBoosters(player, miniGame);

            for(Booster booster : boosters) {
                if(booster.getType() == BoosterType.PERSONAL)
                    personal = booster.getPourcentage();
                else
                    global = booster.getPourcentage();
            }

            String syntax = miniGame.toString() + ";" + personal + ";" + global;
            builder.append(syntax);
            if(count != size) builder.append("!");
        }

        return builder.toString();
    }
}
