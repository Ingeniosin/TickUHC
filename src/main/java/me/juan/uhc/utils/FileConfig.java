package me.juan.uhc.utils;

import lombok.Getter;
import me.juan.uhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileConfig {

    private static HashMap<String, ConfigCursor> cursorCache = new HashMap<>();
    private final File file;
    @Getter
    private final FileConfiguration config;
    @Getter
    private final String fileName;

    public FileConfig(String fileName) {
        this.fileName = fileName;
        JavaPlugin plugin = Main.getMain();
        this.file = new File(plugin.getDataFolder(), fileName);
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (plugin.getResource(fileName) == null) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().severe("Failed to create new file " + fileName);
                }
            } else {
                plugin.saveResource(fileName, false);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (this.config.options().header() == null) {
            Main.getMain().getLogger().severe("There was an error loading the '" + fileName + ".");
            Bukkit.shutdown();
        }
    }

    public ConfigCursor getConfigCursor(String path) {
        ConfigCursor configCursor = cursorCache.getOrDefault(path, null);
        if (configCursor != null) return configCursor;
        configCursor = new ConfigCursor(this);
        configCursor.setPath(path);
        cursorCache.put(path, configCursor);
        return configCursor;
    }


    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not save config file " + this.file.toString());
            e.printStackTrace();
        }
    }

}