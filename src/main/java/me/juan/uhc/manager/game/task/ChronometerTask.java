package me.juan.uhc.manager.game.task;

import me.juan.uhc.Main;
import org.bukkit.Bukkit;

public class ChronometerTask extends GameTask {


    public ChronometerTask(GameTaskStatus gameTaskStatus) {
        super(0, gameTaskStatus);
    }

    @Override
    public void run() {
        if (onCheck != null) Bukkit.getScheduler().runTask(Main.getMain(), () -> onCheck.run(counter));
        counter++;
    }
}
