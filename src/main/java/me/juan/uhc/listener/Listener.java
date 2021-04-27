package me.juan.uhc.listener;

import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.manager.game.Game;
import me.juan.uhc.manager.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;


public abstract class Listener implements org.bukkit.event.Listener {

    /*

    ESTA CLASE SIRVE PARA MANEJAR LOS LISTENERS DE UNA FORMA INTELIGENTE.
    DETECTARA CUANDO SE CAMBIE EL 'GameStatus' Y ACTIVARA Y DESACTIVARA LOS LISTENERS
    NECESARIOS, HAY LISTENERS GLOBALES, QUE NO SE DEBEN BORRAR, ESTOS ESTAN EN Main.java

     */

    public final GameManager gameManager;
    public final Game game;
    @Getter
    private final ArrayList<Listener> currentListeners;

    public Listener() {
        this.gameManager = GameManager.getGameManager();
        this.game = gameManager.getGame();
        this.currentListeners = GlobalListener.getCurrentListeners();
        Bukkit.getPluginManager().registerEvents(this, Main.getMain());
        currentListeners.add(this);
    }

    public void disable() {
        currentListeners.remove(Listener.this);
        HandlerList.unregisterAll(this);
    }


}
