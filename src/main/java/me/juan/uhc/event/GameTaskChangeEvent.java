package me.juan.uhc.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTaskChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public GameTaskChangeEvent() {
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
