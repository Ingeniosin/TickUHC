package me.juan.uhc.menu.buttons;

import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.buttons.Button;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModeButton extends Button {

    private final GameMode gameMode;

    public GameModeButton(GameMode gameMode) {
        super(gameMode.getItemStack());
        this.gameMode = gameMode;
    }

    @Override
    public List<String> cacheLore() {
        String status = gameMode.isEnabled() ? "§aEnabled" : "§cDisabled";
        return new ArrayList<>(Arrays.asList(lines(), "§6Status: " + status, lines()));
    }

    private String lines() {
        return "§7§m---------------------";
    }

    @Override
    public void onClick(Player player, Menu menu) {
        if (gameMode.isEnabled()) gameMode.disable();
        else gameMode.enable();
        player.sendMessage(gameMode.isEnabled() ? "§eThe §f'§6" + gameMode.getName() + "§f' §escenario has been successfully §aenabled§e." : "§eThe §f'§6" + gameMode.getName() + "§f' §escenario has been successfully §cdisabled§e.");
    }
}
