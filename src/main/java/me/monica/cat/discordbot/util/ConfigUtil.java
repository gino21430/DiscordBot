package me.monica.cat.discordbot.util;

import com.google.common.base.Charsets;
import me.monica.cat.discordbot.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigUtil {

    public FileConfiguration loadConfig(String fileName) {
        File file = new File(Main.getPlugin().getDataFolder(), fileName);
        if (!Main.getPlugin().getDataFolder().exists()) {
            if (!Main.getPlugin().getDataFolder().mkdir())
                Main.log("Error while mkdir");
        }
        if (!file.exists()) {
            Main.getPlugin().saveResource(fileName, false);
            try {
                if (!file.exists()) {
                    if (file.createNewFile()) Main.log("Error while createFile");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return YamlConfiguration.loadConfiguration(new InputStreamReader(Main.getPlugin().getResource(fileName), Charsets.UTF_8));
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
