package fr.tenebrae.AttackSpeedRemover;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
	
	public FileConfiguration config;
	//16 would be enough, but we set it to 64 to prevent any issue with plugins using custom attack speed
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
	
	public Attributable restoreOldDamage(Attributable att, ItemStack item) {
		String type = "Hand";
		if (item != null) type = item.getType().toString();
		if (type.contains("AXE")) {
			if (type.contains("DIAMOND")) {
				att = this.setAttackDamage(att, 6);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				att = this.setAttackDamage(att, 3);
			} else if (type.contains("STONE")) {
				att = this.setAttackDamage(att, 4);
			} else if (type.contains("IRON")) {
				att = this.setAttackDamage(att, 5);
			}
		} else if (type.contains("SWORD")) {
			if (type.contains("DIAMOND")) {
				att = this.setAttackDamage(att, 7);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				att = this.setAttackDamage(att, 4);
			} else if (type.contains("STONE")) {
				att = this.setAttackDamage(att, 5);
			} else if (type.contains("IRON")) {
				att = this.setAttackDamage(att, 6);
			}
		} else if (type.contains("SPADE")) {
			if (type.contains("DIAMOND")) {
				att = this.setAttackDamage(att, 4);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				att = this.setAttackDamage(att, 1);
			} else if (type.contains("STONE")) {
				att = this.setAttackDamage(att, 2);
			} else if (type.contains("IRON")) {
				att = this.setAttackDamage(att, 3);
			}
		} else if (type.contains("PICKAXE")) {
			if (type.contains("DIAMOND")) {
				att = this.setAttackDamage(att, 5);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				att = this.setAttackDamage(att, 2);
			} else if (type.contains("STONE")) {
				att = this.setAttackDamage(att, 3);
			} else if (type.contains("IRON")) {
				att = this.setAttackDamage(att, 4);
			}
		} else if (type.contains("HOE")) {
			if (type.contains("DIAMOND")) {
				att = this.setAttackDamage(att, 1);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				att = this.setAttackDamage(att, 1);
			} else if (type.contains("STONE")) {
				att = this.setAttackDamage(att, 1);
			} else if (type.contains("IRON")) {
				att = this.setAttackDamage(att, 1);
			}
		} else {
			att = this.setAttackDamage(att, 1);
		}
		return att;
	}
	
	public Attributable setAttackDamage(Attributable att, double newAtkDmg) {
		AttributeInstance instance = att.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		instance.setBaseValue(newAtkDmg);
		
		for (AttributeModifier modifier : new ArrayList<AttributeModifier>(instance.getModifiers()))
			if (!modifier.getName().contains("potion")) instance.removeModifier(modifier);
		
		return att;
	}
	
	public boolean checkCondition(World world) {
		if (config.getInt("whitelist_type") == 0) {
			if (config.getStringList("whitelist").contains(world.getName())) return false;
			else return true;
		} else {
			if (config.getStringList("whitelist").contains(world.getName())) return true;
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
	
	@EventHandler
	public void itemHeld(final PlayerItemHeldEvent evt) {
		if (!checkCondition(evt.getPlayer())) return;
		if (!checkCondition(evt.getPlayer().getWorld())) return;
		new BukkitRunnable() { 
			
			@Override
			public void run() {
				restoreOldDamage(evt.getPlayer(), evt.getPlayer().getInventory().getItemInMainHand());
			}
			
		}.runTaskLaterAsynchronously(this, 2L);
		// Delaying the task with 2 ticks to avoid getting the old held item.
		// Running it asynchronously to avoid general server lags with weird people keeping switching held item.
	}
}
