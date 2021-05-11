package me.juan.uhc.manager.game.task;

import me.juan.uhc.Main;
import org.bukkit.Bukkit;

public class CountDownTask extends GameTask {

    public CountDownTask(int initialCounter, GameTaskStatus gameTaskStatus) {
        super(initialCounter, gameTaskStatus);
    }

    @Override
    public void run() {
        if (super.counter <= 0) stop();
        else {
            if (onCheck != null) Bukkit.getScheduler().runTask(Main.getMain(), () -> onCheck.run(counter));
            counter--;
        }
    }
}
