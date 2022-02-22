package fr.robotv2.akinaocore.booster;

import fr.robotv2.akinaocore.AkinaoCore;
import fr.robotv2.akinaocore.booster.impl.Booster;
import fr.robotv2.akinaocore.booster.impl.BoosterType;
import fr.robotv2.akinaocore.config.BungeeConfig;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.utilities.StringUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoosterUtilities {

    private final AkinaoCore plugin;
    private static BungeeConfig boosterDatabase;
    private static final Map<UUID, Booster> boosters = new HashMap<>();

    public BoosterUtilities(AkinaoCore plugin) {
        this.plugin = plugin;
        boosterDatabase = new BungeeConfig(plugin, "boosters.yml");
        for (String key : boosterDatabase.getConfiguration().getSection("boosters").getKeys()) {
            try {
                UUID boosterUUID = UUID.fromString(key);
                Booster booster = this.loadBoosterFromConfig(boosterUUID);
                boosters.put(boosterUUID, booster);
            } catch (IllegalArgumentException | NullPointerException exception) {
                plugin.getLogger().warning(StringUtil.colorize("&cAn error occurred while trying to load the boosters"));
                plugin.getLogger().warning(StringUtil.colorize("&cError message: " + exception.getMessage()));
            }
        }
    }

    public static BungeeConfig getBoosterDatabase() {
        return boosterDatabase;
    }

    public List<Booster> getBoosters(ProxiedPlayer player) {
        return boosters.values().stream()
                .filter(booster -> booster.getOwnerUniqueId().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    public List<Booster> getBoosters(ProxiedPlayer player, MiniGame miniGame) {
        return boosters.values().stream()
                .filter(booster -> booster.getOwnerUniqueId().equals(player.getUniqueId()))
                .filter(booster -> booster.getMiniGame().equals(miniGame))
                .collect(Collectors.toList());
    }

    public Booster getBooster(UUID boosterUUID) {
        if(boosters.containsKey(boosterUUID))
            return boosters.get(boosterUUID);
        else if(exist(boosterUUID)) {
            Booster booster = this.loadBoosterFromConfig(boosterUUID);
            boosters.put(boosterUUID, booster);
            return booster;
        } else {
            throw new IllegalArgumentException("The booster with the id: " + boosterUUID + " doesn't exist.");
        }
    }

    public void removeBooster(Booster booster) {
        boosters.remove(booster.getUniqueId());
        booster.removeFromDatabase();
    }

    public boolean exist(UUID boosterUUID) {
        return boosters.containsKey(boosterUUID) && boosterDatabase.getConfiguration()
                .get("boosters." + boosterUUID.toString() + ".owner") != null;
    }

    private Booster loadBoosterFromConfig(UUID boosterUUID) {
        Configuration config = boosterDatabase.getConfiguration();
        String path = "boosters." + boosterUUID + ".";

        UUID owner = UUID.fromString(config.getString(path + "owner"));
        BoosterType boosterType = BoosterType.valueOf(config.getString(path + "type"));
        MiniGame miniGame = MiniGame.valueOf(config.getString(path + "mini-game"));
        Currency currency = Currency.valueOf(config.getString(path + "currency"));
        double pourcentage = config.getDouble(path + "pourcentage");
        int delay = config.getInt(path + "delay");

        return new Booster(boosterUUID, owner, miniGame, pourcentage, delay, currency, boosterType);
    }

    public static class BoosterBuilder {
        private UUID owner;
        private BoosterType type;
        private MiniGame miniGame;
        private Currency currency;
        private double pourcentage;
        private int delay;

        public BoosterBuilder setOwner(UUID owner) {
            this.owner = owner;
            return this;
        }

        public BoosterBuilder setType(BoosterType type) {
            this.type = type;
            return this;
        }

        public BoosterBuilder setMiniGame(MiniGame miniGame) {
            this.miniGame = miniGame;
            return this;
        }

        public BoosterBuilder setCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public BoosterBuilder setPourcentage(double pourcentage) {
            this.pourcentage = pourcentage;
            return this;
        }

        public BoosterBuilder setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public boolean validate(Object... objects) {
            for(Object obj : objects) {
                if(obj == null) return false;
            }
            return true;
        }

        @Nullable
        public Booster build() {
            if(validate(owner, type, miniGame, pourcentage, delay)) {
                UUID id = UUID.randomUUID();
                Booster booster = new Booster(id, owner, miniGame, pourcentage, delay, currency != null ? currency : Currency.COINS, type);
                booster.saveToDatabase();
                boosters.put(booster.getUniqueId(), booster);
                return booster;
            } else {
                return null;
            }
        }
    }
}
