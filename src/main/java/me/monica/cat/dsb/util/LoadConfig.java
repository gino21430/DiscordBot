package me.monica.cat.dsb.util;

import me.monica.cat.dsb.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LoadConfig {
    private FileConfiguration loadConfig(String fileName) {
        File file = new File(Main.getPlugin().getDataFolder(),fileName);
        if(!Main.getPlugin().getDataFolder().exists()){
            if (!Main.getPlugin().getDataFolder().mkdir()) Main.Log("Error mkdir");
        }
        if(!file.exists()) Main.getPlugin().saveResource(fileName, false);
        return YamlConfiguration.loadConfiguration(file);
    }
}
