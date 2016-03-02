package fr.tenebrae.AttackSpeedRemover;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.tenebrae.AttackSpeedRemover.commands.Asr;
import fr.tenebrae.AttackSpeedRemover.listeners.Login;
import fr.tenebrae.AttackSpeedRemover.listeners.Logout;
import fr.tenebrae.AttackSpeedRemover.listeners.SwitchHeldItem;
import fr.tenebrae.AttackSpeedRemover.listeners.WorldSwitch;

public class Main extends JavaPlugin {
	
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		
		// Event registration
		this.getServer().getPluginManager().registerEvents(new Login(this), this);
		this.getServer().getPluginManager().registerEvents(new Logout(this), this);
		this.getServer().getPluginManager().registerEvents(new SwitchHeldItem(this), this);
		this.getServer().getPluginManager().registerEvents(new WorldSwitch(this), this);
		
		// Config setup
		this.config = getConfig().options().copyDefaults(true).copyHeader(true).configuration();
		this.saveDefaultConfig();
		
		// Command registration
		getCommand("asr").setExecutor(new Asr(this));
	}
	
	
	// UTILS - Start ----------------------------------------------------------------------
	public Attributable removeAttackSpeed(Attributable att) {
		att.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("AttackSpeedRemover", (config.getDouble("attackSpeed") == 0 ? 64 : config.getDouble("attackSpeed")), Operation.ADD_NUMBER));
		return att;
	}
	
	public Attributable resetAttackSpeed(Attributable att) {
		AttributeInstance instance = att.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		for (AttributeModifier modifier : new ArrayList<AttributeModifier>(instance.getModifiers()))
			if (modifier.getName().equalsIgnoreCase("AttackSpeedRemover")) instance.removeModifier(modifier);
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
		} else att = this.setAttackDamage(att, 1);
		return att;
	}
	
	public Attributable setAttackDamage(Attributable att, double newAtkDmg) {
		AttributeInstance instance = att.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		instance.setBaseValue(newAtkDmg);
		
		for (AttributeModifier modifier : new ArrayList<AttributeModifier>(instance.getModifiers()))
			if ((modifier.getName().contains("Weapon") || modifier.getName().contains("Tool")))
				instance.removeModifier(modifier);
		
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
	
	public boolean checkCondition(Entity p) {
		if (!config.getBoolean("enable_permission")) return true;
		if (p.hasPermission("attackspeed.bypass")) return true;
		else return false;
	}
	
	public Entity updateAttackSpeed(LivingEntity e) {
		if (!checkCondition(e.getWorld())) {
			resetAttackSpeed(e);
			return e;
		}
		if (!checkCondition(e)) {
			resetAttackSpeed(e);
			return e;
		}
		this.removeAttackSpeed(e);
		return e;
	}
	// UTILS - End ----------------------------------------------------------------------
}
