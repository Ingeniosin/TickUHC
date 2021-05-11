package me.juan.uhc.utils.jmenu.buttons;

import lombok.Data;
import me.juan.uhc.utils.jmenu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Data
public class Button {

    private ItemStack item;
    private ValueInterface lore, name;

    public Button(ItemStack item) {
        this(item, null, null);
    }

    public Button(ItemStack item, ValueInterface lore, ValueInterface name) {
        this.lore = lore;
        this.name = name;
        this.item = item;
    }

    public ItemStack getItemStack() {
        String name = (String) (this.name == null ? null : this.name.value());
        List<String> lore = (List<String>) (this.lore == null ? null : this.lore.value());
        ItemMeta meta = this.item.getItemMeta();
        boolean isCache = name != null || meta != null;
        if (isCache) {
            if (name != null) meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void onClick(Player player, Menu menu) {
        onClick();
    }

    public void onClick() {
    }

    public interface ValueInterface {
        Object value();
    }


}
