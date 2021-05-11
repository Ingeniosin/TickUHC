package me.juan.uhc.manager;

import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.configuration.hotbar.HotbarConfiguration;
import me.juan.uhc.configuration.lang.LangConfiguration;
import me.juan.uhc.configuration.worlds.WorldsConfiguration;
import me.juan.uhc.utils.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

import static me.juan.uhc.listener.lobby.LobbyListener.handleLobby;

@Getter
public class PracticeManager {

    @Getter
    private static PracticeManager practiceManager;
    private final ArrayList<Location> scatterLocations;
    public boolean active;
    private PracticeListener practiceListener;
    @Getter
    private ArrayList<UUID> playersInPractice;

    public PracticeManager() {
        practiceManager = this;
        setActive(true, null);
        scatterLocations = new ArrayList<>();
        WorldManager.makeScatterSpawns(scatterLocations, WorldsConfiguration.PRACTICE.get());
    }

    public static void handle(Player player) {
        if (!canJoin()) {
            LangConfiguration.NOT_AVAILABLE.get().sendPlayer(player);
            return;
        }
        PracticeManager practiceManager = PracticeManager.getPracticeManager();
        if (!practiceManager.addPlayer(player)) practiceManager.deletePlayer(player);
    }

    public static void toggle(Player player) {
        if (practiceManager == null) {
            player.sendMessage(GameManager.getGameManager().isWaiting() ? "§cThe world 'practice' is disabled in config.yml" : LangConfiguration.NOT_AVAILABLE.get().toString());
            return;
        }
        practiceManager.setActive(!practiceManager.isActive(), player);
    }

    private static boolean isInPractice(Player player) {
        return practiceManager.playersInPractice != null && practiceManager.playersInPractice.contains(player.getUniqueId());
    }

    private static boolean canJoin() {
        return practiceManager != null && GameManager.getGameManager().isWaiting() && practiceManager.active;
    }

    private void setActive(boolean active, Player player) {
        if (active) enable();
        else disable();
        if (player != null && player.isOnline())
            player.sendMessage(active ? "§aThe practice has been successfully activated." : "§aThe practice has been deactivated correctly.");
    }

    private void deletePlayer(Player player) {
        if (!isInPractice(player)) return;
        practiceManager.playersInPractice.remove(player.getUniqueId());
        LangConfiguration.PRACTICE_LEAVE.get().sendPlayer(player);
        handleLobby(player, true);
    }

    private boolean addPlayer(Player player) {
        if (isInPractice(player)) return false;
        practiceManager.playersInPractice.add(player.getUniqueId());
        respawnPlayer(player);
        LangConfiguration.PRACTICE_JOIN.get().sendPlayer(player);
        return true;
    }

    private void respawnPlayer(Player player) {
        if (addPlayer(player)) return;
        player.teleport(scatterLocations.get((int) Math.floor(Math.random() * (scatterLocations.size() - 1))));
        setPracticeHotbar(player);
    }

    private void setPracticeHotbar(Player player) {
        new PluginUtil.ResetPlayer(player, GameMode.SURVIVAL, () -> {
            player.getInventory().setArmorContents(HotbarConfiguration.getPracticeArmor());
            player.getInventory().setContents(HotbarConfiguration.getPracticeHotbar());
            player.updateInventory();
        }, true);
    }

    private void enable() {
        playersInPractice = new ArrayList<>();
        practiceListener = new PracticeListener();
        this.active = true;
    }

    private void disable() {
        this.active = false;
        this.practiceListener.disable();
        if (playersInPractice != null)
            new ArrayList<>(playersInPractice).forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) deletePlayer(player);
            });
        this.playersInPractice = null;
    }


    private static class PracticeListener implements Listener {

        private final ItemStack itemOnKill;

        public PracticeListener() {
            this.itemOnKill = new ItemStack(Material.GOLDEN_APPLE, 2);
            Bukkit.getPluginManager().registerEvents(this, Main.getMain());
        }

        public void disable() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onPlayerQuitEvent(PlayerQuitEvent e) {
            PracticeManager.getPracticeManager().deletePlayer(e.getPlayer());
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDeathEvent(PlayerDeathEvent event) {
            final Player player = event.getEntity(), killer = player.getKiller();
            if (!PracticeManager.isInPractice(player)) return;

            event.getEntity().setHealth(event.getEntity().getMaxHealth());
            new PluginUtil.ResetPlayer(player, GameMode.SURVIVAL, () -> PracticeManager.getPracticeManager().respawnPlayer(player), false);

            event.setDeathMessage(null);
            event.getDrops().clear();
            event.setDroppedExp(0);

            if (killer != null) {
                LangConfiguration.PRACTICE_DEAD_BY_PLAYER.get().sendPlayer(player, new String[]{killer.getName()});
                LangConfiguration.PRACTICE_KILL.get().sendPlayer(killer, new String[]{player.getName()});
                killer.getInventory().addItem(itemOnKill);
            } else LangConfiguration.PRACTICE_DEAD_DEFAULT.get().sendPlayer(player);
        }
    }

}
