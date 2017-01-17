package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.tenebrae.AttackSpeedRemover.Main;

public final class Logout implements Listener {

	private Main plugin;
	
	public Logout(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public final void onlogout(final PlayerQuitEvent evt) {
		plugin.resetAttackSpeed(evt.getPlayer());
		plugin.resetDamage(evt.getPlayer());
	}
}
