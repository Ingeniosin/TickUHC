package me.juan.uhc.listener;

import lombok.Getter;
import me.juan.uhc.event.GameStatusChangeEvent;
import me.juan.uhc.listener.game.GameListener;
import me.juan.uhc.listener.game.ScatterListener;
import me.juan.uhc.listener.game.SpectatorListener;
import me.juan.uhc.listener.lobby.LobbyListener;
import me.juan.uhc.manager.GameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GlobalListener implements Listener {

    @Getter
    private static final ArrayList<me.juan.uhc.listener.Listener> currentListeners = new ArrayList<>();

    public GlobalListener() {
        onGameStatusChangeEvent(new GameStatusChangeEvent(null, GameManager.getGameManager().getGameStatus())); //default
    }

    private static void clearListeners() {
        new ArrayList<>(getCurrentListeners()).forEach(me.juan.uhc.listener.Listener::disable); //PREVENT -> 'ConcurrentModificationException'
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) event.setCancelled(true);
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onGameStatusChangeEvent(GameStatusChangeEvent e) {
        clearListeners();
        switch (e.getNewState()) {
            case GENERATING:
            case WAITING:
            case STARTING:
                new LobbyListener();
                break;
            case SCATTERING:
                new ScatterListener();
                break;
            case PLAYING:
            case END:
                new GameListener();
                new SpectatorListener();
                break;
        }
    }

    // @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {                          //<- ROD FIX
        if (!(e.getDamager() instanceof Projectile)) return;
        Projectile projectile = (Projectile) e.getDamager();
        if (!(projectile.getShooter() instanceof Player)) return;
        Player player = (Player) projectile.getShooter();
        ItemStack itemStack = player.getItemInHand();
        if (!itemStack.getType().equals(Material.FISHING_ROD)) return;
        player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() - 1));
    }

}
