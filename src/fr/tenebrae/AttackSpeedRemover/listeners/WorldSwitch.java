package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import fr.tenebrae.AttackSpeedRemover.Main;

public class WorldSwitch implements Listener {

	private Main plugin;
	
	public WorldSwitch(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onswitch(PlayerChangedWorldEvent evt) {
		plugin.updateAttackSpeed(evt.getPlayer());
	}
}
