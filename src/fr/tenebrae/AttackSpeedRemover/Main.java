package fr.tenebrae.AttackSpeedRemover;

import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	public FileConfiguration config;
	public AttributeModifier modifier = new AttributeModifier("AttackSpeedRemover", 64.0D, Operation.ADD_NUMBER);
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
		getConfig().options().copyDefaults();
		getConfig().options().copyHeader();
		this.config = getConfig();
	}
	
	public Attributable removeAttackSpeed(Attributable att) {
		att.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(modifier);
		return att;
	}
	
	public Attributable resetAttackSpeed(Attributable att) {
		att.getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(modifier);
		return att;
	}
	
	public boolean checkCondition(World world) {
		//getLogger().info("Checking condition for world "+world.getName());
		if (config.getInt("whitelist_type") == 0) {
			//getLogger().info("Whitelist type 0");
			if (config.getStringList("whitelist").contains(world.getName())) {
				//getLogger().info("Whitelist contains the world");
				return false;
			}
			else return true;
		} else {
			//getLogger().info("Whitelist type 1");
			if (config.getStringList("whitelist").contains(world.getName())) {
				//getLogger().info("Whitelist contains the world");
				return true;
			}
			else return false;
		}
	}
	
	public boolean checkCondition(HumanEntity p) {
		if (!config.getBoolean("enable_permission")) return true;
		if (p.hasPermission("attackspeed.bypass")) return true;
		else return false;
	}
	
	@EventHandler
	public void onlogin(PlayerLoginEvent evt) {
		if (!checkCondition(evt.getPlayer().getWorld())) return;
		if (!checkCondition(evt.getPlayer())) return;
		this.removeAttackSpeed(evt.getPlayer());
	}
	
	@EventHandler
	public void onswitch(PlayerChangedWorldEvent evt) {
		if (!checkCondition(evt.getPlayer().getWorld())) {
			this.resetAttackSpeed(evt.getPlayer());
			return;
		}
		if (!checkCondition(evt.getPlayer())) return;
		this.removeAttackSpeed(evt.getPlayer());
	}
}
