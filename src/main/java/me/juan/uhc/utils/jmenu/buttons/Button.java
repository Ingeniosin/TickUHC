package me.juan.uhc.utils.jmenu.buttons;

import lombok.Data;
import me.juan.uhc.utils.jmenu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Data
public abstract class Button {

    public ItemStack item;

    public Button(ItemStack itemStack) {
        this.item = itemStack;
    }

    public ItemStack getItemStack() {
        String name = cacheName();
        List<String> lore = cacheLore();
        ItemMeta meta = this.item.getItemMeta();
        boolean isCache = name != null || meta != null;
        if (isCache) {
            if (name != null) meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public String cacheName() {
        return null;
    }

    public List<String> cacheLore() {
        return null;
    }

    public void onClick(Player player, Menu menu) {
        onClick();
    }

    public void onClick() {
    }


}
