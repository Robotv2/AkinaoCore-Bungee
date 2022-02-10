package fr.robotv2.akinaocore;

import fr.robotv2.akinaocore.config.BungeeConfig;
import fr.robotv2.akinaocore.manager.PlayerSessionManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public final class AkinaoCore extends Plugin {

    public static final String CHANNEL = "akinaocore:points";
    private static AkinaoCore instance;

    private PlayerSessionManager playerSessionManager;
    private BungeeConfig database;

    @Override
    public void onEnable() {
        instance = this;
        playerSessionManager = new PlayerSessionManager(this);

        setupFiles();
        setupListeners();
    }

    @Override
    public void onDisable() {
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

    //<-- setup -->

    public void setupFiles() {
        database = new BungeeConfig(this, "data.yml");
    }

    public void setupListeners() {
        getProxy().registerChannel(CHANNEL);

        PluginManager pm = getProxy().getPluginManager();
        pm.registerListener(this, playerSessionManager);
    }
}
