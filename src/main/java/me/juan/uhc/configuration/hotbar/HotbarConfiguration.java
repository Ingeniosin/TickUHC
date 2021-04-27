package me.juan.uhc.configuration.hotbar;

import lombok.Getter;
import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.utils.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static me.juan.uhc.utils.PluginUtil.getElement;

public enum HotbarConfiguration {

    LOBBY(new Hotbar("lobby")),
    SPECTATOR(new Hotbar("spectator"));

    private static ItemStack[] practiceHotbar, practiceArmor;
    private final Hotbar hotbar;

    HotbarConfiguration(Hotbar hotbar) {
        this.hotbar = hotbar;
    }

    public static ItemStack[] getPracticeHotbar() {
        if (practiceHotbar == null)
            practiceHotbar = InventoryUtil.deserializeInventory(getElement(String.class, ConfigurationFile.MAIN.getConfig().getConfigCursor("practice"), "inventory", true));
        return practiceHotbar;
    }

    public static ItemStack[] getPracticeArmor() {
        if (practiceArmor == null)
            practiceArmor = InventoryUtil.deserializeInventory(getElement(String.class, ConfigurationFile.MAIN.getConfig().getConfigCursor("practice"), "armor", true));
        return practiceArmor;
    }

    public Hotbar get() {
        return hotbar;
    }

    @Getter
    public static class Hotbar {

        private final HashMap<String, Items> ItemList = new HashMap<>();
        private final FileConfig fileConfig;

        public Hotbar(String path) {
            fileConfig = ConfigurationFile.HOTBAR.getConfig();
            fileConfig.getConfigCursor(path).getKeys().forEach(element -> ItemList.put(element, new Items(fileConfig.getConfigCursor(path + "." + element))));
        }

        public void setHotbar(Player player, GameMode gameMode) {
            new PluginUtil.ResetPlayer(player, gameMode, () -> {
                ItemList.forEach((s, items) -> items.setItem(player));
                player.updateInventory();
            }, true);
        }


        public ItemStack getItem(String id) {
            return ItemList.get(id).getItemStack();
        }

        @Getter
        public static class Items {
            private final ItemStack itemStack;
            private final int slot;

            public Items(ConfigCursor configCursor) {
                List<String> lore = getElement(List.class, configCursor, "lore", false, false);
                this.itemStack = new ItemCreator(
                        Material.valueOf(getElement(String.class, configCursor, "item.material", true)),
                        getElement(int.class, configCursor, "item.amount", true),
                        getElement(int.class, configCursor, "item.data", true))
                        .setName(getElement(String.class, configCursor, "name", true))
                        .setLore(lore).get();
                this.slot = getElement(int.class, configCursor, "slot", true);
            }

            public void setItem(Player player) {
                player.getInventory().setItem(slot, itemStack);
            }

        }


    }

}
