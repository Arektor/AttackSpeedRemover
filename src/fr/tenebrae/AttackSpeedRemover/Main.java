package fr.tenebrae.AttackSpeedRemover;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.tenebrae.AttackSpeedRemover.commands.Asr;
import fr.tenebrae.AttackSpeedRemover.listeners.Login;
import fr.tenebrae.AttackSpeedRemover.listeners.Logout;
import fr.tenebrae.AttackSpeedRemover.listeners.PusherLogin;
import fr.tenebrae.AttackSpeedRemover.listeners.PusherLogout;
import fr.tenebrae.AttackSpeedRemover.listeners.SweepAttack;
import fr.tenebrae.AttackSpeedRemover.listeners.SwitchHeldItem;
import fr.tenebrae.AttackSpeedRemover.listeners.WorldSwitch;

public final class Main extends JavaPlugin {

	public static String nmsver;
	public FileConfiguration config;

	@Override
	public final void onEnable() {
		// NMS Version Detection
		nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

		// Config setup
		this.config = getConfig().options().copyDefaults(true).copyHeader(true).configuration();
		this.saveDefaultConfig();

		// Event registration
		if (this.config.getBoolean("enabled_modules.attackSpeedRemover")) {
			this.getServer().getPluginManager().registerEvents(new Login(this), this);
			this.getServer().getPluginManager().registerEvents(new Logout(this), this);
			this.getServer().getPluginManager().registerEvents(new SwitchHeldItem(this), this);
			this.getServer().getPluginManager().registerEvents(new WorldSwitch(this), this);
		}
		if (this.config.getBoolean("enabled_modules.playerCollisionRemover")) {
			this.getServer().getPluginManager().registerEvents(new PusherLogin(this), this);
			this.getServer().getPluginManager().registerEvents(new PusherLogout(), this);
		}
		if (this.config.getBoolean("enabled_modules.sweepAttackRemover")) this.getServer().getPluginManager().registerEvents(new SweepAttack(this), this);

		// Command registration
		getCommand("asr").setExecutor(new Asr(this));

		// Updating players attack speed
		for (Player p : Bukkit.getOnlinePlayers()) this.updateAttackSpeed(p);
	}


	// UTILS - Start ----------------------------------------------------------------------
	public final void removeAttackSpeed(final Attributable att) {
		att.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("AttackSpeedRemover", (config.getDouble("attackSpeed") == 0 ? 64 : config.getDouble("attackSpeed")), Operation.ADD_NUMBER));
	}

	public final void resetAttackSpeed(final Attributable att) {
		final AttributeInstance instance = att.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		for (AttributeModifier modifier : new ArrayList<AttributeModifier>(instance.getModifiers())) if (modifier.getName().equalsIgnoreCase("AttackSpeedRemover")) instance.removeModifier(modifier);
	}

	public final void restoreOldDamage(final Attributable att, final ItemStack item) {
		final String type;
		if (item != null) type = item.getType().toString();
		else type = "Hand";

		if (type.contains("AXE")) {
			if (type.contains("DIAMOND")) {
				this.setAttackDamage(item.getType(), att, 6);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				this.setAttackDamage(item.getType(), att, 3);
			} else if (type.contains("STONE")) {
				this.setAttackDamage(item.getType(), att, 4);
			} else if (type.contains("IRON")) {
				this.setAttackDamage(item.getType(), att, 5);
			}
		} else if (type.contains("SWORD")) {
			if (type.contains("DIAMOND")) {
				this.setAttackDamage(item.getType(), att, 7);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				this.setAttackDamage(item.getType(), att, 4);
			} else if (type.contains("STONE")) {
				this.setAttackDamage(item.getType(), att, 5);
			} else if (type.contains("IRON")) {
				this.setAttackDamage(item.getType(), att, 6);
			}
		} else if (type.contains("SPADE")) {
			if (type.contains("DIAMOND")) {
				this.setAttackDamage(item.getType(), att, 4);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				this.setAttackDamage(item.getType(), att, 1);
			} else if (type.contains("STONE")) {
				this.setAttackDamage(item.getType(), att, 2);
			} else if (type.contains("IRON")) {
				this.setAttackDamage(item.getType(), att, 3);
			}
		} else if (type.contains("PICKAXE")) {
			if (type.contains("DIAMOND")) {
				this.setAttackDamage(item.getType(), att, 5);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				this.setAttackDamage(item.getType(), att, 2);
			} else if (type.contains("STONE")) {
				this.setAttackDamage(item.getType(), att, 3);
			} else if (type.contains("IRON")) {
				this.setAttackDamage(item.getType(), att, 4);
			}
		} else if (type.contains("HOE")) {
			if (type.contains("DIAMOND")) {
				this.setAttackDamage(item.getType(), att, 1);
			} else if (type.contains("GOLD") || type.contains("WOOD")) {
				this.setAttackDamage(item.getType(), att, 1);
			} else if (type.contains("STONE")) {
				this.setAttackDamage(item.getType(), att, 1);
			} else if (type.contains("IRON")) {
				this.setAttackDamage(item.getType(), att, 1);
			}
		} else this.setAttackDamage(item.getType(), att, 1);
	}

	public final void setAttackDamage(final Material m, final Attributable att, final double newAtkDmg) {
		final AttributeInstance instance = att.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

		for (final AttributeModifier modifier : instance.getModifiers()) {
			if ((modifier.getName().contains("Weapon") || modifier.getName().contains("Tool"))) {
				try {
					getLogger().info("Removing it");
					instance.removeModifier(modifier);
				} catch (ConcurrentModificationException e) {
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.removeModifier(modifier);
							} catch (ConcurrentModificationException e) {
								new BukkitRunnable() {
									@Override
									public void run() {
										instance.removeModifier(modifier);
									}
								}.runTaskLater(Main.this, 1L);
							}
						}
					}.runTaskLater(Main.this, 1L);
				}
			}
		}
		try {
			instance.addModifier(new AttributeModifier(getModifierForTool(m), newAtkDmg, Operation.ADD_NUMBER));
		} catch (ConcurrentModificationException e) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						instance.addModifier(new AttributeModifier(getModifierForTool(m), newAtkDmg, Operation.ADD_NUMBER));
					} catch (ConcurrentModificationException e) {
						new BukkitRunnable() {
							@Override
							public void run() {
								instance.addModifier(new AttributeModifier(getModifierForTool(m), newAtkDmg, Operation.ADD_NUMBER));
							}
						}.runTaskLater(Main.this, 1L);
					}
				}
			}.runTaskLater(Main.this, 1L);
		}
	}

	public final void resetDamage(final Attributable att) {
		final AttributeInstance instance = att.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		for (final AttributeModifier modifier : instance.getModifiers()) {
			if ((modifier.getName().contains("Weapon") || modifier.getName().contains("Tool"))) {
				try {
					instance.removeModifier(modifier);
				} catch (ConcurrentModificationException e) {
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.removeModifier(modifier);
							} catch (ConcurrentModificationException e) {
								new BukkitRunnable() {
									@Override
									public void run() {
										instance.removeModifier(modifier);
									}
								}.runTaskLater(Main.this, 1L);
							}
						}
					}.runTaskLater(Main.this, 1L);
				}
			}
		}

	}

	private final String getModifierForTool(final Material m) {
		final String n = m.name();
		if (n.contains("SWORD")) return "Weapon modifier";
		else return "Tool modifier";
	}

	public final boolean checkCondition(final World world) {
		if (config.getInt("whitelist_type") == 0) {
			if (config.getStringList("whitelist").contains(world.getName())) return false;
			else return true;
		} else {
			if (config.getStringList("whitelist").contains(world.getName())) return true;
			else return false;
		}
	}

	public final boolean checkCondition(final Entity p) {
		if (!config.getBoolean("enable_permission")) return true;
		if (p.hasPermission("attackspeed.bypass")) return true;
		else return false;
	}

	public final Entity updateAttackSpeed(final LivingEntity e) {
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
