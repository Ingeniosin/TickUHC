package me.juan.uhc.utils.jmenu.buttons;

import me.juan.uhc.utils.ItemCreator;
import org.bukkit.Material;

public class GlassButton extends Button {

    private static GlassButton glassButton;
    private static GlassButton glassButtonRed;


    private GlassButton() {
        super(new ItemCreator(Material.STAINED_GLASS_PANE).setName("§e ").get());
    }

    private GlassButton(int color) {
        super(new ItemCreator(Material.STAINED_GLASS_PANE, 0, color).setName("§e ").get());
    }

    public static GlassButton get() {
        if (glassButton == null) glassButton = new GlassButton();
        return glassButton;
    }

    public static GlassButton getRed() {
        if (glassButtonRed == null) glassButtonRed = new GlassButton(14);
        return glassButtonRed;
    }
}
