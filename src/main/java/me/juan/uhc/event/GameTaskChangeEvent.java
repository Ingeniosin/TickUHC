package me.juan.uhc.event;

import lombok.Getter;
import me.juan.uhc.manager.game.task.GameTaskStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTaskChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final GameTaskStatus gameTaskStatus;

    public GameTaskChangeEvent(GameTaskStatus gameTaskStatus) {
        this.gameTaskStatus = gameTaskStatus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
