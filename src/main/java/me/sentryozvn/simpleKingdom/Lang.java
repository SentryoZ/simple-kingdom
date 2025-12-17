package me.sentryozvn.simpleKingdom;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class Lang {

    private final SimpleKingdom plugin;
    private FileConfiguration langConfig;

    public Lang(SimpleKingdom plugin) {
        this.plugin = plugin;
        loadLang();
    }

    private void loadLang() {
        File langFile = new File(plugin.getDataFolder(), "language.yml");
        if (!langFile.exists()) {
            plugin.saveResource("language.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String get(String key) {
        return ChatColor.translateAlternateColorCodes('&', langConfig.getString("messages." + key, ""));
    }

    public String get(String key, Map<String, String> replacements) {
        String message = get(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
