package me.juan.uhc.manager.game.task;

import lombok.Getter;
import lombok.Setter;
import me.juan.uhc.Main;
import me.juan.uhc.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameTask extends BukkitRunnable {


    @Getter
    public int counter;
    @Setter
    @Getter
    public VoidInterface onCheck, onEnd;

    public GameTask(int initialCounter, GameTaskStatus gameTaskStatus) {
        this.counter = initialCounter;
        GameManager.getGameManager().setGameTask(this, gameTaskStatus);
        this.runTaskTimerAsynchronously(Main.getMain(), 20L, 20L);

    }

    public void stop() {
        this.cancel();
        if (onEnd != null) Bukkit.getScheduler().runTask(Main.getMain(), () -> onEnd.run(counter));
    }

    public interface VoidInterface {
        void run(int count);
    }


}
