package me.sentryozvn.simpleKingdom;

import me.sentryozvn.simpleKingdom.model.Kingdom;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KingdomManager {

    private final SimpleKingdom plugin;
    private final Map<String, Kingdom> kingdoms = new HashMap<>();
    private int maxDeviation;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final PermissionManager permissionManager;

    public KingdomManager(SimpleKingdom plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        this.permissionManager = plugin.getPermissionManager();
        loadKingdoms();
        loadData();
    }

    private void loadKingdoms() {
        ConfigurationSection kingdomsSection = plugin.getConfig().getConfigurationSection("kingdoms");
        if (kingdomsSection != null) {
            for (String key : kingdomsSection.getKeys(false)) {
                String name = kingdomsSection.getString(key + ".name");
                String tag = kingdomsSection.getString(key + ".tag");
                String permission = kingdomsSection.getString(key + ".permission");
                kingdoms.put(key.toLowerCase(), new Kingdom(name, tag, permission));
            }
        }
        maxDeviation = plugin.getConfig().getInt("settings.max_deviation", 5);
    }

    public void loadData() {
        if (dataFile.exists()) {
            ConfigurationSection kingdomsSection = dataConfig.getConfigurationSection("kingdoms");
            if (kingdomsSection != null) {
                for (String kingdomKey : kingdomsSection.getKeys(false)) {
                    Kingdom kingdom = kingdoms.get(kingdomKey);
                    if (kingdom != null) {
                        List<String> memberUUIDs = kingdomsSection.getStringList(kingdomKey + ".members");
                        for (String uuid : memberUUIDs) {
                            kingdom.addMember(UUID.fromString(uuid));
                        }
                    }
                }
            }
        }
    }

    public void saveData() {
        ConfigurationSection kingdomsSection = dataConfig.createSection("kingdoms");
        for (Map.Entry<String, Kingdom> entry : kingdoms.entrySet()) {
            List<String> memberUUIDs = new java.util.ArrayList<>();
            for (UUID uuid : entry.getValue().getMembers()) {
                memberUUIDs.add(uuid.toString());
            }
            kingdomsSection.set(entry.getKey() + ".members", memberUUIDs);
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data to " + dataFile.getName());
        }
    }


    public Kingdom getKingdom(String name) {
        return kingdoms.get(name.toLowerCase());
    }

    public Map<String, Kingdom> getKingdoms() {
        return kingdoms;
    }

    public Kingdom getPlayerKingdom(UUID player) {
        for (Kingdom kingdom : kingdoms.values()) {
            if (kingdom.getMembers().contains(player)) {
                return kingdom;
            }
        }
        return null;
    }

    public boolean isPlayerInKingdom(UUID player) {
        return getPlayerKingdom(player) != null;
    }

    public void joinKingdom(UUID playerUUID, Kingdom kingdom) {
        if (isPlayerInKingdom(playerUUID)) {
            return;
        }
        kingdom.addMember(playerUUID);
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            permissionManager.addPermission(player, kingdom.getPermission());
        }
        saveData();
    }

    public void leaveKingdom(UUID playerUUID) {
        Kingdom kingdom = getPlayerKingdom(playerUUID);
        if (kingdom != null) {
            kingdom.removeMember(playerUUID);
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                permissionManager.removePermission(player, kingdom.getPermission());
            }
            saveData();
        }
    }

    public boolean isKingdomFull(Kingdom targetKingdom) {
        int totalPlayers = 0;
        for (Kingdom kingdom : kingdoms.values()) {
            totalPlayers += kingdom.getMemberCount();
        }
        int averageMembers = kingdoms.isEmpty() ? 0 : totalPlayers / kingdoms.size();
        return targetKingdom.getMemberCount() > averageMembers + maxDeviation;
    }
}
