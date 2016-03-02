package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.tenebrae.AttackSpeedRemover.Main;

public class Logout implements Listener {

	private Main plugin;
	
	public Logout(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onlogout(PlayerQuitEvent evt) {
		plugin.resetAttackSpeed(evt.getPlayer());
	}
}
