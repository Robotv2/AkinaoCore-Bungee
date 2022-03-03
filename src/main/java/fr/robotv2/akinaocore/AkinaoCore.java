package fr.robotv2.akinaocore;

import co.aikar.commands.BungeeCommandManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.robotv2.akinaocore.booster.BoosterManager;
import fr.robotv2.akinaocore.booster.impl.BoosterType;
import fr.robotv2.akinaocore.commands.*;
import fr.robotv2.akinaocore.config.BungeeConfig;
import fr.robotv2.akinaocore.impl.Currency;
import fr.robotv2.akinaocore.impl.MiniGame;
import fr.robotv2.akinaocore.listeners.PluginMessageListener;
import fr.robotv2.akinaocore.manager.*;
import fr.robotv2.akinaocore.offlinePlayers.OfflinePlayersHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class AkinaoCore extends Plugin {

    public final static String LOBBY_CHANNEL = "akinaolobby:lobby";
    public static final String CORE_CHANNEL = "akinaocore:points";
    private static AkinaoCore instance;

    private PlayerSessionManager playerSessionManager;
    private OfflinePlayersHandler offlinePlayerHandler;
    private LobbyChanger lobbyChanger;
    private TopTenManager topTenManager;
    private BoosterManager boosterManager;
    private CurrencyManager currencyManager;
    private WelcomeManager welcomeManager;

    private BungeeConfig database;

    @Override
    public void onEnable() {
        instance = this;

        playerSessionManager = new PlayerSessionManager(this);
        offlinePlayerHandler = new OfflinePlayersHandler(this);
        lobbyChanger = new LobbyChanger(this);
        //topTenManager = new TopTenManager(this);
        currencyManager = new CurrencyManager(this);
        boosterManager = new BoosterManager(this);
        welcomeManager = new WelcomeManager(this);

        setupFiles();
        setupChannel();
        setupListeners();
        setupCommand();
    }

    @Override
    public void onDisable() {
        getPlayerSessionManager().unregisterAllPlayers();
        instance = null;
    }

    //<-- "Getters" ->>

    public static AkinaoCore getInstance() {
        return instance;
    }

    public BungeeConfig getDatabase() {
        return database;
    }

    public PlayerSessionManager getPlayerSessionManager() {
        return playerSessionManager;
    }

    public LobbyChanger getLobbyChanger() {
        return lobbyChanger;
    }

    public TopTenManager getTopTenManager() {
        return topTenManager;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    public OfflinePlayersHandler getOfflinePlayerHandler() {
        return offlinePlayerHandler;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public WelcomeManager getWelcomeManager() {
        return welcomeManager;
    }

    //<-- setup -->

    public void setupFiles() {
        database = new BungeeConfig(this, "data.yml");
    }

    public void setupListeners() {
        PluginManager pm = getProxy().getPluginManager();
        pm.registerListener(this, playerSessionManager);
    }

    public void setupChannel() {
        getProxy().registerChannel(CORE_CHANNEL);
        getProxy().registerChannel(LOBBY_CHANNEL);
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener(this));
    }

    public void setupCommand() {
        BungeeCommandManager manager = new BungeeCommandManager(this);
        manager.getLocales().setDefaultLocale(Locale.FRANCE);
        manager.registerCommand(new AkinaoBungeeCommand(this));
        manager.registerCommand(new BoosterAdminCommand(this));
        manager.registerCommand(new BoosterCommand(this));
        manager.registerCommand(new MsgCommand());
        manager.registerCommand(new ThankCommand());
        manager.registerCommand(welcomeManager);

        manager.getCommandCompletions().registerCompletion("currencies", c -> {
            return Arrays.stream(Currency.values()).map(Currency::toLowerCase).collect(Collectors.toList());
        });

        manager.getCommandCompletions().registerCompletion("minigame", c -> {
            return Arrays.stream(MiniGame.values()).map(MiniGame::toLowerCase).collect(Collectors.toList());
        });

        manager.getCommandCompletions().registerCompletion("booster", c -> {
            return Arrays.stream(BoosterType.values()).map(BoosterType::toLowerCase).collect(Collectors.toList());
        });
    }
}
