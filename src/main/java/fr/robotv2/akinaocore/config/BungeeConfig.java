package fr.robotv2.akinaocore.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeeConfig {

    Plugin plugin;
    File file = null;
    Configuration configuration = null;

    public BungeeConfig(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);
        setupDefault();
    }

    public void setupDefault() {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
        if (!file.exists()) {
            try (InputStream in = plugin.getResourceAsStream(file.getName())) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reload();
    }

    public Configuration getConfiguration() {
        if(this.configuration == null)
            reload();
        return this.configuration;
    }

    public void reload() {
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
