package fr.tenebrae.AttackSpeedRemover.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public final class PusherLogout implements Listener {

	@EventHandler
	public final void onQuit(final PlayerQuitEvent evt) {
		final Player p = evt.getPlayer();
		final Scoreboard board = p.getScoreboard();
		if (board.getTeams().contains("PusherModule."+p.getName())) board.getTeam("PusherModule."+p.getName()).unregister();
		else board.getEntryTeam(p.getName()).setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
	}
}
