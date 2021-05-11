package me.juan.uhc.menu.buttons;

import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.utils.PluginUtil;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.buttons.Button;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class GameModeButton extends Button {

    private final GameMode gameMode;

    public GameModeButton(GameMode gameMode) {
        super(gameMode.getItemStack(), () -> new ArrayList<>(Arrays.asList(PluginUtil.lines(), "§6Status: " + (gameMode.isEnabled() ? "§aEnabled" : "§cDisabled"), PluginUtil.lines())), null);
        this.gameMode = gameMode;
    }


    @Override
    public void onClick(Player player, Menu menu) {
        if (gameMode.isEnabled()) gameMode.disable();
        else gameMode.enable();
        player.sendMessage(gameMode.isEnabled() ? "§eThe §f'§6" + gameMode.getName() + "§f' §escenario has been successfully §aenabled§e." : "§eThe §f'§6" + gameMode.getName() + "§f' §escenario has been successfully §cdisabled§e.");
    }
}
