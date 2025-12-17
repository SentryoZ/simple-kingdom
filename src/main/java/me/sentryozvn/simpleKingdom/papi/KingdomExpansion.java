package me.sentryozvn.simpleKingdom.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sentryozvn.simpleKingdom.SimpleKingdom;
import me.sentryozvn.simpleKingdom.model.Kingdom;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KingdomExpansion extends PlaceholderExpansion {

    private final SimpleKingdom plugin;

    public KingdomExpansion(SimpleKingdom plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kingdom";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SentryoZVN";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("name")) {
            Kingdom kingdom = plugin.getKingdomManager().getPlayerKingdom(player.getUniqueId());
            return kingdom != null ? kingdom.getName() : "No Kingdom";
        }

        if (params.equalsIgnoreCase("tag")) {
            Kingdom kingdom = plugin.getKingdomManager().getPlayerKingdom(player.getUniqueId());
            return kingdom != null ? kingdom.getTag() : "";
        }

        if (params.startsWith("playercount_")) {
            String kingdomName = params.substring(12);
            Kingdom kingdom = plugin.getKingdomManager().getKingdom(kingdomName);
            return kingdom != null ? String.valueOf(kingdom.getMemberCount()) : "0";
        }

        return null;
    }
}
