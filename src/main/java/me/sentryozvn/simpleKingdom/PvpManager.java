package me.sentryozvn.simpleKingdom;

import me.sentryozvn.simpleKingdom.model.Kingdom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PvpManager implements Listener {

  private final SimpleKingdom plugin;
  private final Set<UUID> pvpEnabled = new HashSet<>();
  private boolean friendlyFire;
  private boolean allowToggle;

  public PvpManager(SimpleKingdom plugin) {
    this.plugin = plugin;
    loadConfig();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void loadConfig() {
    friendlyFire = plugin.getConfig().getBoolean("settings.pvp.friendly_fire", false);
    allowToggle = plugin.getConfig().getBoolean("settings.pvp.allow_toggle", true);
  }

  public boolean canPvp(Player player) {
    return pvpEnabled.contains(player.getUniqueId());
  }

  public void togglePvp(Player player) {
    if (pvpEnabled.contains(player.getUniqueId())) {
      pvpEnabled.remove(player.getUniqueId());
    } else {
      pvpEnabled.add(player.getUniqueId());
    }
  }

  public boolean isAllowToggle() {
    return allowToggle;
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) {
      return;
    }
    if (friendlyFire){
      return;
    }
    Kingdom attackerKingdom = plugin.getKingdomManager().getPlayerKingdom(attacker.getUniqueId());
    Kingdom victimKingdom = plugin.getKingdomManager().getPlayerKingdom(victim.getUniqueId());

    if (attackerKingdom != null && attackerKingdom.equals(victimKingdom) && canPvp(attacker) && canPvp(victim)) {
      event.setCancelled(true);
    }
  }
}
