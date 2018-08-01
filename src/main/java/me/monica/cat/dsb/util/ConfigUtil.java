package me.monica.cat.dsb.util;

import com.google.common.base.Charsets;
import me.monica.cat.dsb.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;

public class ConfigUtil {

    public FileConfiguration loadConfig(String fileName) {
        File file = new File(Main.getPlugin().getDataFolder(), fileName);
        if (!Main.getPlugin().getDataFolder().exists()) Main.getPlugin().getDataFolder().mkdir();
        if (!file.exists()) {
            Main.getPlugin().saveResource(fileName, false);
            return YamlConfiguration.loadConfiguration(new InputStreamReader(Main.getPlugin().getResource(fileName), Charsets.UTF_8));
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
