package me.juan.uhc.menu;

import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.menu.buttons.ConfigButton;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.PaginatedMenu;
import org.bukkit.entity.Player;

public class ConfigurationMenu {

    public static void configurationMenu(Player player) {
        new Menu(player, 9 * 3, true, "§eConfiguration Menu", ConfigButton.getConfigsButtons(), true).open();
    }

    public static void scenarioManageMenu(Player player) {
        PaginatedMenu.create(player, 9 * 4, true, "§6GameModes: ", GameMode.getGameModeButtons()).open();
    }


}

