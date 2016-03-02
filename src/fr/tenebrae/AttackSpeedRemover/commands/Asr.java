package fr.tenebrae.AttackSpeedRemover.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.tenebrae.AttackSpeedRemover.Main;

public class Asr implements CommandExecutor {

	private Main plugin;
	
	public Asr(Main plugin) {
		this.plugin = plugin;
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
			if (args[0].equalsIgnoreCase("reload") && (sender.hasPermission("attackspeed.reload") || sender.isOp())) {
				try {
					plugin.reloadConfig();
					plugin.config = plugin.getConfig();
					for (Player p : Bukkit.getOnlinePlayers()) plugin.updateAttackSpeed(p);
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
