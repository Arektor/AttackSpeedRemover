package fr.tenebrae.AttackSpeedRemover.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import fr.tenebrae.AttackSpeedRemover.Main;

public final class SweepAttack implements Listener {

	private Main plugin;
	public SweepAttack(Main plugin) { this.plugin = plugin; }
	private final List<Location> locs = new ArrayList<Location>();

	@EventHandler
	public final void onEntityDamaged(final EntityDamageByEntityEvent evt) {
		if (!plugin.checkCondition(evt.getDamager().getWorld())) return;
		if (!(evt.getDamager() instanceof Player)) return;
		if (Main.nmsver.contains("1_11")) if (evt.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) evt.setCancelled(true);
		else {
			final Player p = (Player)evt.getDamager();
			if (!p.getInventory().getItemInMainHand().getType().name().contains("SWORD")) return;
			final Location loc = p.getLocation();
			if (evt.getDamage() == 1.0D) { if (locs.contains(loc)) evt.setCancelled(true); }
			else {
				locs.add(loc);
				new BukkitRunnable() {
					@Override
					public void run() {
						locs.remove(loc);
					}
				}.runTaskLater(plugin, 10L);
			}
		}
	}
}
