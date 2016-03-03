package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.tenebrae.AttackSpeedRemover.Main;

public class Login implements Listener {

	private Main plugin;
	
	public Login(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onlogin(final PlayerLoginEvent evt) {
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.updateAttackSpeed(evt.getPlayer());
			}
		}.runTaskLaterAsynchronously(plugin, 2L);
	}
}
