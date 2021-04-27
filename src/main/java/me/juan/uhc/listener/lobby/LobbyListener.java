package me.juan.uhc.listener.lobby;

import me.juan.uhc.Main;
import me.juan.uhc.configuration.lang.LangConfiguration;
import me.juan.uhc.configuration.worlds.WorldsConfiguration;
import me.juan.uhc.listener.Listener;
import me.juan.uhc.manager.PracticeManager;
import me.juan.uhc.menu.ConfigurationMenu;
import me.juan.uhc.player.PlayerState;
import me.juan.uhc.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import static me.juan.uhc.configuration.hotbar.HotbarConfiguration.LOBBY;

public class LobbyListener extends Listener {


    public static void handleLobby(Player player, boolean teleport) {
        if (teleport) player.teleport(WorldsConfiguration.LOBBY.get().getWorld().getSpawnLocation().add(0.5, 3, 0.5));
        LOBBY.get().setHotbar(player, GameMode.SURVIVAL);
    }

    public boolean isInLobby(Entity entity) {
        return entity.getLocation().getWorld().equals(WorldsConfiguration.LOBBY.get().getWorld());
    }

    @EventHandler
    public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent e) {
        e.setSpawnLocation(WorldsConfiguration.LOBBY.get().getWorld().getSpawnLocation().add(0.5, 3, 0.5));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void blockFormListener(BlockFormEvent event) {
        Material blocktype = event.getNewState().getBlock().getType();
        if (blocktype == Material.ICE || blocktype == Material.WATER) event.setCancelled(true);
    }

    @EventHandler
    public void meltEvent(BlockFadeEvent event) {
        Material blocktype = event.getBlock().getType();
        if (blocktype == Material.SNOW || blocktype == Material.ICE) event.setCancelled(true);

    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
        if (isInLobby(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler
    public void onHangingInteractByPlayer(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWitherChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Wither || event.getEntity() instanceof EnderDragon) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) event.setCancelled(true);

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && isInLobby(entity)) {
            event.setCancelled(true);
            if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;
            entity.teleport(entity.getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode() != GameMode.CREATIVE && isInLobby(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UHCPlayer.getPlayerByUUID(player.getUniqueId()).setPlayerState(PlayerState.DIED);
        LangConfiguration.WELCOME.get().sendPlayer(player);
        handleLobby(player, false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getItemMeta() == null || event.getItem().getItemMeta().getDisplayName() == null)
            return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !isInLobby(player)) return;
        event.setCancelled(true);
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        final ItemStack item = event.getItem();
        //ITEMS
        if (item.isSimilar(LOBBY.get().getItem("practice"))) PracticeManager.handle(player);
        else if (item.isSimilar(LOBBY.get().getItem("configuration"))) ConfigurationMenu.configurationMenu(player);

    }

    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        e.setCancelled(true);
        e.getPlayer().updateInventory();
        if (!isInLobby(player)) return;
        final Block block = e.getBlockClicked().getRelative(e.getBlockFace());
        block.setType(Material.STATIONARY_WATER);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getMain(), () -> block.setType(Material.AIR), 2L);
    }


}
