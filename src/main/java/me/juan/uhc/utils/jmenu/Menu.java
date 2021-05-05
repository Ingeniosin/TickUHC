package me.juan.uhc.utils.jmenu;

import lombok.Data;
import me.juan.uhc.Main;
import me.juan.uhc.utils.jmenu.buttons.AirButton;
import me.juan.uhc.utils.jmenu.buttons.BackButton;
import me.juan.uhc.utils.jmenu.buttons.Button;
import me.juan.uhc.utils.jmenu.buttons.GlassButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.IntStream;

@Data
public class Menu {

    public static MenuListener menuListener;
    public static HashMap<UUID, Menu> menuHashMap = new HashMap<>();
    private Player player;
    private int size;
    private boolean autoUpdate;
    private Inventory inventory;
    private String title;
    private BukkitTask bukkitTask;
    private TreeMap<Integer, Button> buttonHashMap;

    private Menu previousMenu;

    public Menu(Player player, int size, boolean autoUpdate, String title) {
        this(player, size, autoUpdate, title, (TreeMap<Integer, Button>) null);
    }

    public Menu(Player player, int size, boolean autoUpdate, String title, ArrayList<Button> arrayListButton) {
        this(player, size, autoUpdate, title, arrayListButton, false);
    }

    public Menu(Player player, int size, boolean autoUpdate, String title, TreeMap<Integer, Button> buttonHashMap) {
        this(player, size, autoUpdate, title, buttonHashMap, false);
    }

    public Menu(Player player, int size, boolean autoUpdate, String title, ArrayList<Button> arrayListButton, boolean glassFill) {
        if (menuListener == null) menuListener = new MenuListener();
        Menu lastMenu = getMenu(player);
        if (lastMenu != null) previousMenu = lastMenu;
        this.player = player;
        this.size = size;
        this.autoUpdate = autoUpdate;
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, (title.length() > 32) ? title.substring(0, 32) : title);
        TreeMap<Integer, Button> buttonHashMap = new TreeMap<>();
        arrayListButton.forEach(button -> buttonHashMap.put(buttonHashMap.size(), button));
        this.setButtonHashMap(buttonHashMap, glassFill);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), this::onLoad);
    }

    public Menu(Player player, int size, boolean autoUpdate, String title, TreeMap<Integer, Button> buttonHashMap, boolean glassFill) {
        if (menuListener == null) menuListener = new MenuListener();
        Menu lastMenu = getMenu(player);
        if (lastMenu != null) previousMenu = lastMenu;
        this.player = player;
        this.size = size;
        this.autoUpdate = autoUpdate;
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, (title.length() > 32) ? title.substring(0, 32) : title);
        this.setButtonHashMap(buttonHashMap, glassFill);
        this.onLoad();
    }

    private static Menu getMenu(Player player) {
        return menuHashMap.getOrDefault(player.getUniqueId(), null);
    }

    public void setTitle(String title) {
        this.title = title;
        Main.getMain().getNms().sendTittle(player, title);
    }

    public void update() {
        if (buttonHashMap == null) return;
        inventory.setContents(buttonHashMap.values().stream().map(Button::getItemStack).toArray(ItemStack[]::new));
        player.updateInventory();
    }

    public void setButtonHashMap(TreeMap<Integer, Button> buttonHashMap) {
        setButtonHashMap(buttonHashMap, false);
    }

    public void setButtonHashMap(TreeMap<Integer, Button> buttonHashMap, boolean glassFill) {
        if (buttonHashMap == null) buttonHashMap = new TreeMap<>();
        else buttonHashMap = new TreeMap<>(buttonHashMap);
        final TreeMap<Integer, Button> ButtonHashMap = buttonHashMap;
        IntStream.range(0, size).filter(value -> ButtonHashMap.getOrDefault(value, null) == null).forEach(value -> ButtonHashMap.put(value, glassFill ? GlassButton.get() : AirButton.get()));
        if (previousMenu != null) ButtonHashMap.put(size - 1, BackButton.getBackButton());
        this.buttonHashMap = ButtonHashMap;
    }

    public void open() {
        // Menu lastMenu = getMenu(player);
        // if (lastMenu != null) lastMenu.onClose();

        if (autoUpdate)
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMain(), this::update, 0L, 2L);
        else Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMain(), this::update, 1L);
        player.openInventory(inventory);
        onOpen();
        menuHashMap.put(player.getUniqueId(), this);
        menuListener.setRegistered(true);

    }

    private void remove() {
        menuHashMap.remove(player.getUniqueId(), this);
        if (menuHashMap.isEmpty()) menuListener.setRegistered(false);
        if (this.bukkitTask != null) this.bukkitTask.cancel();
        this.bukkitTask = null;
    }

    public void onLoad() {
    }

    public void onOpen() {
    }

    public void click(Button button, int index) {

    }

    public void onClose() {
    }

    public static class MenuListener implements Listener {

        private boolean registered;

        public void setRegistered(boolean registered) {
            if (this.registered == registered) return;
            if (registered) Bukkit.getPluginManager().registerEvents(this, Main.getMain());
            else HandlerList.unregisterAll(this);
            this.registered = registered;
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClickEvent(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            Menu openMenu = menuHashMap.getOrDefault(player.getUniqueId(), null);
            if (openMenu == null) return;
            int slot = event.getSlot();
            if (slot != event.getRawSlot()) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
                    event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            if (event.getClick() == ClickType.DOUBLE_CLICK) return;
            Button button = openMenu.getButtonHashMap().getOrDefault(slot, null);
            if (button == null) return;
            if (button instanceof GlassButton && button.getItemStack().getDurability() == GlassButton.get().getItemStack().getDurability()) {
                openMenu.getButtonHashMap().put(slot, GlassButton.getRed());
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMain(), () -> {
                    openMenu.getButtonHashMap().put(slot, GlassButton.get());
                    if (!openMenu.isAutoUpdate()) openMenu.update();
                }, 20L);
            }
            button.onClick(player, openMenu);
            openMenu.click(button, slot);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMain(), openMenu::update, 1L);

        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryCloseEvent(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();
            Menu openMenu = menuHashMap.getOrDefault(player.getUniqueId(), null);
            if (openMenu != null) {
                openMenu.onClose();
                openMenu.remove();
            }
        }
    }
}
