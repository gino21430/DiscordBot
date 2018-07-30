package me.monica.cat.dsb.util;

import me.monica.cat.dsb.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LoadConfig {

    public FileConfiguration loadConfig(String fileName) {
        File file = new File(Main.getPlugin().getDataFolder(), fileName + ".yml");
        if (!Main.getPlugin().getDataFolder().exists()) {
            if (!Main.getPlugin().getDataFolder().mkdir()) Main.log("Error mkdir");
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
