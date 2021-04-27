package me.juan.uhc.event;

import lombok.Getter;
import me.juan.uhc.manager.game.GameStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStatusChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final GameStatus lastState, newState;

    public GameStatusChangeEvent(GameStatus lastState, GameStatus newState) {
        this.lastState = lastState;
        this.newState = newState;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
