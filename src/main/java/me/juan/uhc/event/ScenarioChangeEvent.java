package me.juan.uhc.event;

import lombok.Getter;
import me.juan.uhc.manager.gamemode.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ScenarioChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final GameMode gameMode;
    private final boolean added;

    public ScenarioChangeEvent(GameMode gameMode, boolean added) {
        this.added = added;
        this.gameMode = gameMode;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
