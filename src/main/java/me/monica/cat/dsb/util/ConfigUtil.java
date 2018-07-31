package me.monica.cat.dsb.util;

import me.monica.cat.dsb.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {

    public FileConfiguration loadConfig(File file) {
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

    public void saveLinkMap(Map<String, String> map, File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (Map.Entry entry : map.entrySet()) {
                bw.write(entry.getKey() + " " + entry.getValue());
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> loadMap(File file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String data;
            String[] pair;
            Map<String, String> map = new HashMap<>();
            while ((data = br.readLine()) != null) {
                pair = data.split(" ", 1);
                map.put(pair[0], pair[1]);
            }
            br.close();
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
