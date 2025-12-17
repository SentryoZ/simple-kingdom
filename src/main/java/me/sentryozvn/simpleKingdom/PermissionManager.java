package me.sentryozvn.simpleKingdom;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionManager {

    private final SimpleKingdom plugin;
    private Permission perms = null;

    public PermissionManager(SimpleKingdom plugin) {
        this.plugin = plugin;
        setupPermissions();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            perms = rsp.getProvider();
        }
    }

    public void addPermission(Player player, String permission) {
        if (perms != null && !perms.playerHas(player, permission)) {
            perms.playerAdd(player, permission);
        }
    }

    public void removePermission(Player player, String permission) {
        if (perms != null && perms.playerHas(player, permission)) {
            perms.playerRemove(player, permission);
        }
    }
}
