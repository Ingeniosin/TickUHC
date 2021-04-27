package me.juan.uhc.manager.gamemode.modes;

import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.utils.ItemCreator;
import org.bukkit.Material;

public class NoClean extends GameMode {

    public NoClean(int id) {
        super("No Clean " + id, new ItemCreator(Material.APPLE).setName("§cNoClean " + id).get());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
