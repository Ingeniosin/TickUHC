package me.juan.uhc.manager.gamemode;

import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.event.ScenarioChangeEvent;
import me.juan.uhc.menu.buttons.GameModeButton;
import me.juan.uhc.utils.jmenu.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Getter
public abstract class GameMode implements Listener {
    @Getter
    private static final ArrayList<GameMode> currentGameModes = new ArrayList<>();
    @Getter
    private static final ArrayList<String> currentGameModesNames = new ArrayList<>();
    @Getter
    private static final ArrayList<GameMode> availableGameModes = new ArrayList<>();
    @Getter
    private static final ArrayList<Button> gameModeButtons = new ArrayList<>();

    private final String name;
    private final ItemStack itemStack;

    public GameMode(String name, ItemStack itemStack) {
        this.name = name;
        this.itemStack = itemStack;
        gameModeButtons.add(new GameModeButton(this));
        availableGameModes.add(this);
    }

    public boolean isEnabled() {
        return currentGameModes.contains(this);
    }


    public void enable() {
        currentGameModes.add(this);
        currentGameModesNames.add(getName());
        register();
        Bukkit.getPluginManager().callEvent(new ScenarioChangeEvent(this, true));
        this.onEnable();
    }

    public void disable() {
        currentGameModes.remove(this);
        currentGameModesNames.remove(getName());
        unregister();
        Bukkit.getPluginManager().callEvent(new ScenarioChangeEvent(this, false));
        this.onDisable();
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.getMain());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public abstract void onEnable();

    public abstract void onDisable();

}
