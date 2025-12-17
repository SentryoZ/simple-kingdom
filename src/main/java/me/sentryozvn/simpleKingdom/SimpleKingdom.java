package me.sentryozvn.simpleKingdom;

import me.sentryozvn.simpleKingdom.commands.KingdomCommand;
import me.sentryozvn.simpleKingdom.commands.KingdomTabCompleter;
import me.sentryozvn.simpleKingdom.papi.KingdomExpansion;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class SimpleKingdom extends JavaPlugin {

    private KingdomManager kingdomManager;
    private Lang lang;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        lang = new Lang(this);
        kingdomManager = new KingdomManager(this);
        getCommand("kingdom").setExecutor(new KingdomCommand(this));
        getCommand("kingdom").setTabCompleter(new KingdomTabCompleter(this));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KingdomExpansion(this).register();
        }

        int autoSaveInterval = getConfig().getInt("settings.auto_save_interval", 20) * 20 * 60;
        new BukkitRunnable() {
            @Override
            public void run() {
                kingdomManager.saveData();
            }
        }.runTaskTimer(this, autoSaveInterval, autoSaveInterval);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        kingdomManager.saveData();
    }

    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }

    public Lang getLang() {
        return lang;
    }
}
