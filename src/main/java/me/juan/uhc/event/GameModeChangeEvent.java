package me.juan.uhc.event;

import lombok.Getter;
import me.juan.uhc.manager.gamemode.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameModeChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final GameMode gameMode;

    public GameModeChangeEvent(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
