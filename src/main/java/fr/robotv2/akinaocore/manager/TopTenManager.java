package fr.robotv2.akinaocore.manager;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.utilities.TopComparator;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TopTenManager {

    private final AkinaoCore plugin;
    private final Map<UUID, Double> TOP_TEN_COINS = new LinkedHashMap<>();
    private final Map<UUID, Double> TOP_TEN_AKINAO = new LinkedHashMap<>();

    public TopTenManager(AkinaoCore instance) {
        this.plugin = instance;
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            try {
                Arrays.stream(Currency.values()).forEach(this::actualizeTopTen);
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }, 0, 20, TimeUnit.MINUTES);
    }

    public UUID get(Currency currency, int index) {
        switch (currency) {
            case AKINAOPOINTS:
                return new ArrayList<>(TOP_TEN_AKINAO.keySet()).get(index);
            case COINS:
                return new ArrayList<>(TOP_TEN_COINS.keySet()).get(index);
            default:
                throw new IllegalArgumentException(currency + " isn't a valid currency");
        }
    }

    public Map<UUID, Double> getTopTen(Currency currency) {
        switch (currency) {
            case COINS:
                return TOP_TEN_COINS;
            case AKINAOPOINTS:
                return TOP_TEN_AKINAO;
            default:
                throw new IllegalArgumentException(currency + " isn't a valid currency");
        }
    }

    public void actualizeTopTen(Currency currency) {
        switch (currency) {
            case COINS:
                actualizeCoins();
                break;
            case AKINAOPOINTS:
                actualizeAkinaoPoints();
                break;
        }
    }

    private void actualizeAkinaoPoints() {
        Map<UUID, Double> fileValueCollector = new HashMap<>();
        Configuration configuration = plugin.getDatabase().getConfiguration();

        for(String playerUUIDStr : configuration.getKeys()) {
            if(Objects.isNull(playerUUIDStr) || playerUUIDStr.isEmpty()) continue;
            UUID playerUUID = UUID.fromString(playerUUIDStr);
            double playerPoints = configuration.getDouble(playerUUIDStr + ".akinao-points");
            if(playerPoints == 0) continue;
            fileValueCollector.put(playerUUID, playerPoints);
        }

        List<Map.Entry<UUID, Double>> topPointsList = new ArrayList<>(fileValueCollector.entrySet());
        topPointsList.sort(new TopComparator());

        for(int i = 0; i <= 9; i++) {
            try {
                Map.Entry<UUID, Double> entry = topPointsList.get(i);
                TOP_TEN_AKINAO.put(entry.getKey(), entry.getValue());
            } catch (IndexOutOfBoundsException e) {
                return;
            }
        }
    }

    private void actualizeCoins() {
        Map<UUID, Double> fileValueCollector = new HashMap<>();
        Configuration configuration = plugin.getDatabase().getConfiguration();

        for(String playerUUIDStr : configuration.getKeys()) {
            if(Objects.isNull(playerUUIDStr) || playerUUIDStr.isEmpty())
                continue;
            UUID playerUUID = UUID.fromString(playerUUIDStr);
            double playerCoins = configuration.getDouble(playerUUIDStr + ".coins");
            if(playerCoins == 0) continue;
            fileValueCollector.put(playerUUID, playerCoins);
        }

        List<Map.Entry<UUID, Double>> topCoinsList = new ArrayList<>(fileValueCollector.entrySet());
        topCoinsList.sort(new TopComparator());

        for(int i = 0; i <= 9; i++) {
            try {
                Map.Entry<UUID, Double> entry = topCoinsList.get(i);
                TOP_TEN_COINS.put(entry.getKey(), entry.getValue());
            } catch (IndexOutOfBoundsException e) {
                return;
            }
        }
    }
}
