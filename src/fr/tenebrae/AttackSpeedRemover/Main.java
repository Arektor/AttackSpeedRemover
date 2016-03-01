package fr.tenebrae.AttackSpeedRemover;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	
	public FileConfiguration config;
	//16 would be enough, but we set it to 64 to prevent any issue with plugins using custom attack speed
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.config = getConfig().options().copyDefaults(true).copyHeader(true).configuration();
		this.saveDefaultConfig();
		getCommand("asr").setExecutor(this);
	}
	
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
	
	@EventHandler
	public void onlogin(PlayerLoginEvent evt) {
		this.updateAttackSpeed(evt.getPlayer());
	}
	
	@EventHandler
	public void onlogout(PlayerQuitEvent evt) {
		this.resetAttackSpeed(evt.getPlayer());
	}
	
	@EventHandler
	public void onswitch(PlayerChangedWorldEvent evt) {
		this.updateAttackSpeed(evt.getPlayer());
	}
	
	@EventHandler
	public void itemHeld(PlayerItemHeldEvent evt) {
		if (!checkCondition(evt.getPlayer())) return;
		if (!checkCondition(evt.getPlayer().getWorld())) return;
		final Player p = evt.getPlayer();
		new BukkitRunnable() { 
			
			@Override
			public void run() {
				restoreOldDamage(p, p.getInventory().getItemInMainHand());
			}
			
		}.runTaskLaterAsynchronously(this, 2L);
		// Delaying the task with 2 ticks to avoid getting the old held item.
		// Running it asynchronously to avoid general server lags with weird people keeping switching held item.
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage("§9* §3Available commands:");
			sender.sendMessage("    §7- §9/asr reload");
			return true;
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("attackspeed.reload")) {
				try {
					this.reloadConfig();
					this.config = this.getConfig();
					for (Player p : Bukkit.getOnlinePlayers()) this.updateAttackSpeed(p);
				} catch (Exception e) {
					sender.sendMessage("§4* §cEww, an error occured while reloading configuration! ✖");
					e.printStackTrace();
					return true;
				}
				sender.sendMessage("§2* §aConfiguration reloaded! ✔");
				return true;
			} else {
				sender.sendMessage("§9* §3Available commands:");
				sender.sendMessage("    §7- §9/asr reload");
				return true;
			}
		}
		return false;
	}
}
