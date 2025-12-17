package me.sentryozvn.simpleKingdom.commands;

import me.sentryozvn.simpleKingdom.SimpleKingdom;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KingdomTabCompleter implements TabCompleter {

    private final SimpleKingdom plugin;

    public KingdomTabCompleter(SimpleKingdom plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("join", "leave", "list", "info"), new ArrayList<>());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("info"))) {
            return StringUtil.copyPartialMatches(args[1], plugin.getKingdomManager().getKingdoms().keySet(), new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
