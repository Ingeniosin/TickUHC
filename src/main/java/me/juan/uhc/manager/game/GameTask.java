package me.juan.uhc.manager.game;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameTask extends BukkitRunnable {


    @Getter
    private int counter;

    public GameTask(int initialCounter) {
        this.counter = initialCounter;
        GameManager.getGameManager().setGameTask(this);
    }

    public void onEnd() {
    }

    public void onCheck() {
    }

    public void stop() {
        this.cancel();
        onEnd();
    }

    public class CountDownTask extends GameTask {

        public CountDownTask(int initialCounter) {
            super(initialCounter);
        }

        @Override
        public void run() {
            if (counter <= 0) {
                stop();
                return;
            }

            onCheck();
            counter--;
        }
    }

}
