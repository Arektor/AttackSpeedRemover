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
		final String s = "PM."+p.getName();
		try {
			if (board.getTeam(s.length() > 16 ? s.substring(0, 16) : s) != null) board.getTeam(s.length() > 16 ? s.substring(0, 16) : s).unregister();
			else board.getEntryTeam(p.getName()).setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
		} catch (NullPointerException npe) {}
	}
}
