package me.juan.uhc.event;

import lombok.Getter;
import me.juan.uhc.player.PlayerState;
import me.juan.uhc.player.UHCPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerChangeStateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerState lastState, newState;
    private final UHCPlayer uhcPlayer;

    public PlayerChangeStateEvent(PlayerState lastState, PlayerState newState, UHCPlayer uhcPlayer) {
        this.lastState = lastState;
        this.newState = newState;
        this.uhcPlayer = uhcPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
