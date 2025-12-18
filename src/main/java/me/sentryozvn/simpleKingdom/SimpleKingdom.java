package me.sentryozvn.simpleKingdom;

import me.sentryozvn.simpleKingdom.commands.KingdomCommand;
import me.sentryozvn.simpleKingdom.commands.KingdomTabCompleter;
import me.sentryozvn.simpleKingdom.model.Kingdom;
import me.sentryozvn.simpleKingdom.papi.KingdomExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class SimpleKingdom extends JavaPlugin {

    private KingdomManager kingdomManager;
    private Lang lang;
    private PermissionManager permissionManager;
    private PvpManager pvpManager;
    private BukkitTask autoSaveTask;
    private BukkitTask permissionCheckTask;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setup();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        kingdomManager.saveData();
    }

    public void reload() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        if (permissionCheckTask != null) {
            permissionCheckTask.cancel();
        }
        setup();
    }

    private void setup() {
        saveDefaultConfig();
        reloadConfig();
        lang = new Lang(this);
        permissionManager = new PermissionManager(this);
        kingdomManager = new KingdomManager(this);
        pvpManager = new PvpManager(this);
        getCommand("kingdom").setExecutor(new KingdomCommand(this));
        getCommand("kingdom").setTabCompleter(new KingdomTabCompleter(this));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KingdomExpansion(this).register();
        }

        int autoSaveInterval = getConfig().getInt("settings.auto_save_interval", 20) * 20 * 60;
        autoSaveTask = new BukkitRunnable() {
            @Override
            public void run() {
                kingdomManager.saveData();
            }
        }.runTaskTimer(this, autoSaveInterval, autoSaveInterval);

        int permissionCheckInterval = getConfig().getInt("settings.permission_check_interval", 5) * 20 * 60;
        permissionCheckTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    Kingdom playerKingdom = kingdomManager.getPlayerKingdom(player.getUniqueId());
                    for (Kingdom kingdom : kingdomManager.getKingdoms().values()) {
                        if (playerKingdom != null && playerKingdom.equals(kingdom)) {
                            permissionManager.addPermission(player, kingdom.getPermission());
                        } else {
                            permissionManager.removePermission(player, kingdom.getPermission());
                        }
                    }
                }
            }
        }.runTaskTimer(this, permissionCheckInterval, permissionCheckInterval);
    }

    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }

    public Lang getLang() {
        return lang;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public PvpManager getPvpManager() {
        return pvpManager;
    }
}
