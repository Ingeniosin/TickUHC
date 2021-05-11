package me.juan.uhc.listener;

import me.juan.uhc.configuration.lang.LangConfiguration;
import me.juan.uhc.manager.GameManager;
import me.juan.uhc.player.UHCPlayer;
import me.juan.uhc.scoreboard.UHCBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoinEvent(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        String name = e.getName();
        UHCPlayer uhcPlayer = UHCPlayer.getOrCreate(uuid, name);
        if (isValid(uuid, name)) return;
        uhcPlayer.remove();
        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        e.setKickMessage(LangConfiguration.FAILED_LOAD.get().toString());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        GameManager gameManager = GameManager.getGameManager();
        String result = gameManager.isNotWhitelist(player);
        if (result == null) return;
        e.setKickMessage(result);
        e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        if (!(gameManager.isEnded() || gameManager.isPlaying()))
            UHCPlayer.getOrCreate(player.getUniqueId(), player.getName()).remove();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        new UHCBoard(e.getPlayer());
        e.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

    private boolean isValid(UUID uuid, String name) {
        return uuid != null && name != null;
    }

}
