package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.tenebrae.AttackSpeedRemover.Main;

public class SwitchHeldItem implements Listener {

	private Main plugin;
	
	public SwitchHeldItem(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void itemHeld(PlayerItemHeldEvent evt) {
		if (!plugin.checkCondition(evt.getPlayer())) return;
		if (!plugin.checkCondition(evt.getPlayer().getWorld())) return;
		final Player p = evt.getPlayer();
		new BukkitRunnable() { 
			
			@Override
			public void run() {
				plugin.restoreOldDamage(p, p.getInventory().getItemInMainHand());
			}
			
		}.runTaskLaterAsynchronously(plugin, 2L);
		// Delaying the task with 2 ticks to avoid getting the old held item.
		// Running it asynchronously to avoid general server lags with weird people keeping switching held item.
	}
}
