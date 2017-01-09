package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import fr.tenebrae.AttackSpeedRemover.Main;

public final class PusherLogin implements Listener {

	private Main plugin;
	public PusherLogin(Main plugin) { this.plugin = plugin; }
	
	@EventHandler
	public final void onJoin(final PlayerJoinEvent evt) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final Player p = evt.getPlayer();
				final Scoreboard board = p.getScoreboard();
				if (board.getTeams().contains(board.getEntryTeam(p.getName()))) board.getEntryTeam(p.getName()).setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
				else {
					final Team t = board.registerNewTeam("PusherModule."+p.getName());
					t.addEntry(p.getName());
					t.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
				}
			}
		}.runTaskLater(plugin, 3L);
	}
}
