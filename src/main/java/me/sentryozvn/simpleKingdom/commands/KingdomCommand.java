package me.sentryozvn.simpleKingdom.commands;

import me.sentryozvn.simpleKingdom.KingdomManager;
import me.sentryozvn.simpleKingdom.Lang;
import me.sentryozvn.simpleKingdom.PvpManager;
import me.sentryozvn.simpleKingdom.SimpleKingdom;
import me.sentryozvn.simpleKingdom.model.Kingdom;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;

public class KingdomCommand implements CommandExecutor {

    private final SimpleKingdom plugin;
    private final KingdomManager kingdomManager;
    private final Lang lang;
    private final PvpManager pvpManager;

    public KingdomCommand(SimpleKingdom plugin) {
        this.plugin = plugin;
        this.kingdomManager = plugin.getKingdomManager();
        this.lang = plugin.getLang();
        this.pvpManager = plugin.getPvpManager();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("kingdom.reload")) {
                sender.sendMessage(lang.get("no_permission"));
                return true;
            }
            plugin.reload();
            sender.sendMessage(lang.get("reload_success"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.get("only_players"));
            return true;
        }

      if (args.length > 0 && args[0].equalsIgnoreCase("pvp")) {
            if (!pvpManager.isAllowToggle()) {
                player.sendMessage(lang.get("pvp_toggle_not_allowed"));
                return true;
            }
            pvpManager.togglePvp(player);
            if (pvpManager.canPvp(player)) {
                player.sendMessage(lang.get("pvp_enabled"));
            } else {
                player.sendMessage(lang.get("pvp_disabled"));
            }
            return true;
        }

        if (args.length == 0) {
            Kingdom playerKingdom = kingdomManager.getPlayerKingdom(player.getUniqueId());
            if (playerKingdom != null) {
                player.sendMessage(lang.get("in_kingdom", new HashMap<>() {{
                    put("kingdom", playerKingdom.getName());
                }}));
            } else {
                player.sendMessage(lang.get("no_kingdom"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                player.sendMessage(lang.get("join_usage"));
                return true;
            }
            if (kingdomManager.isPlayerInKingdom(player.getUniqueId())) {
                player.sendMessage(lang.get("already_in_kingdom"));
                return true;
            }
            Kingdom kingdom = kingdomManager.getKingdom(args[1]);
            if (kingdom == null) {
                player.sendMessage(lang.get("kingdom_not_exist"));
                return true;
            }
            if (kingdomManager.isKingdomFull(kingdom)) {
                player.sendMessage(lang.get("kingdom_full"));
                return true;
            }
            kingdomManager.joinKingdom(player.getUniqueId(), kingdom);
            player.sendMessage(lang.get("join_success", new HashMap<>() {{
                put("kingdom", kingdom.getName());
            }}));
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            if (!kingdomManager.isPlayerInKingdom(player.getUniqueId())) {
                player.sendMessage(lang.get("no_kingdom"));
                return true;
            }
            kingdomManager.leaveKingdom(player.getUniqueId());
            player.sendMessage(lang.get("leave_success"));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(lang.get("list_header"));
            for (Kingdom kingdom : kingdomManager.getKingdoms().values()) {
                player.sendMessage(lang.get("list_item", new HashMap<>() {{
                    put("kingdom", kingdom.getName());
                    put("members", String.valueOf(kingdom.getMemberCount()));
                }}));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                player.sendMessage(lang.get("info_usage"));
                return true;
            }
            Kingdom kingdom = kingdomManager.getKingdom(args[1]);
            if (kingdom == null) {
                player.sendMessage(lang.get("kingdom_not_exist"));
                return true;
            }
            player.sendMessage(lang.get("info_header", new HashMap<>() {{
                put("kingdom", kingdom.getName());
            }}));
            player.sendMessage(lang.get("info_tag", new HashMap<>() {{
                put("tag", kingdom.getTag());
            }}));
            player.sendMessage(lang.get("info_members", new HashMap<>() {{
                put("members", String.valueOf(kingdom.getMemberCount()));
            }}));
            return true;
        }

        return false;
    }
}
