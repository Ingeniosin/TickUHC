package me.juan.uhc.task;

import me.juan.uhc.scoreboard.Board;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardTask extends BukkitRunnable {

    @Override
    public void run() {
        if (Board.getBoards().keySet().isEmpty()) return;

        try {
            Board.getBoards().values().parallelStream().forEach(Board::update);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

}
