package me.juan.uhc.manager;

import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.configuration.lang.LangConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class FrozenManager {

    @Getter
    public static FrozenManager frozenManager;
    @Getter
    private final ArrayList<UUID> frozenPlayers;
    private final FrozenListener frozenListener;

    public FrozenManager() {
        frozenManager = this;
        this.frozenPlayers = new ArrayList<>();
        this.frozenListener = new FrozenListener();
    }

    public boolean setFrozen(Player player) {
        boolean isFrozen = isFrozen(player);
        this.setFrozen(player, player.getUniqueId(), !isFrozen);
        return !isFrozen;
    }

    private void setFrozen(Player player, UUID uuid, boolean status) {
        if (status) {
            frozenPlayers.add(uuid);
            if (player != null && player.isOnline()) {
                Main.getMain().getNms().addVehicle(player);
                LangConfiguration.FREEZE_ON_FREEZE.get().sendPlayer(player);
                LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            }
        } else {
            frozenPlayers.remove(uuid);
            if (player != null && player.isOnline()) {
                Main.getMain().getNms().removeVehicle(player);
                LangConfiguration.FREEZE_ON_UN_FREEZE.get().sendPlayer(player);
            }
        }
        frozenListener.setEnable(!frozenPlayers.isEmpty());
    }

    private boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }

    public class FrozenListener implements Listener {

        @Getter
        private boolean enable;

        public void setEnable(boolean bol) {
            if (enable == bol) return;
            this.enable = bol;
            if (bol) Bukkit.getPluginManager().registerEvents(this, Main.getMain());
            else HandlerList.unregisterAll(this);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerPlayerQuitEvent(PlayerQuitEvent e) {
            Player player = e.getPlayer();
            if (!isFrozen(player)) return;
            FrozenManager.getFrozenManager().setFrozen(player, player.getUniqueId(), false);

            //MENSAJE PARA STAFFs - DESCONEXION.-.-..

        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInventoryClick(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            Player player = event.getPlayer();
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
            Player player = event.getPlayer();
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBlockBreakEvent(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBlockPlaceEvent(BlockPlaceEvent event) {
            Player player = event.getPlayer();
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onEntityDamageEvent(EntityDamageEvent event) {
            if (!(event.getEntity() instanceof Player)) return;
            Player player = (Player) event.getEntity();
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player)) return;
            Player player = (Player) event.getEntity(), target = (event.getDamager() instanceof Player) ? (Player) event.getDamager() : ((event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) ? (Player) ((Projectile) event.getDamager()).getShooter() : null);
            if (target == null) return;
            if (isFrozen(target)) {
                LangConfiguration.FREEZE_ALERT.get().sendPlayer(player);
                event.setCancelled(true);
                return;
            }
            if (!isFrozen(player)) return;
            LangConfiguration.FREEZE_ON_PVP.get().sendPlayer(target);
            event.setCancelled(true);
        }

    }

}
