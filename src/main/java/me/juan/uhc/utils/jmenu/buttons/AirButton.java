package me.juan.uhc.utils.jmenu.buttons;

import me.juan.uhc.utils.ItemCreator;
import org.bukkit.Material;

public class AirButton extends Button {

    private static AirButton glassButton;

    private AirButton() {
        super(new ItemCreator(Material.AIR).get());
    }


    public static AirButton get() {
        if (glassButton == null) glassButton = new AirButton();
        return glassButton;
    }
}
