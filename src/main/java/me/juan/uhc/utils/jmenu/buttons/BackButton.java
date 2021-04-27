package me.juan.uhc.utils.jmenu.buttons;

import me.juan.uhc.utils.ItemCreator;
import me.juan.uhc.utils.jmenu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BackButton extends Button {

    private static BackButton backButton;

    private BackButton() {
        super(new ItemCreator(Material.ARROW).setName("§cGo back").get());
    }

    public static BackButton getBackButton() {
        if (backButton == null) backButton = new BackButton();
        return backButton;
    }

    @Override
    public void onClick(Player player, Menu menu) {
        Menu previousMenu = menu.getPreviousMenu();
        if (previousMenu != null) previousMenu.open();
    }


}
