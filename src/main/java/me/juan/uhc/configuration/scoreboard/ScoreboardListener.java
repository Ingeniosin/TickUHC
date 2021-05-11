package me.juan.uhc.configuration.scoreboard;

import me.juan.uhc.event.GameTaskChangeEvent;
import me.juan.uhc.event.ScenarioChangeEvent;
import me.juan.uhc.manager.GameManager;
import me.juan.uhc.manager.game.task.GameTaskStatus;
import me.juan.uhc.manager.gamemode.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ScoreboardListener implements Listener {

    @EventHandler
    public void onGameModeChangeEvent(ScenarioChangeEvent e) {
        ArrayList<String> scoreboard = ListValueConfiguration.GAMEMODE.getScoreboard();
        ArrayList<String> currentGameModesNames = new ArrayList<>(GameMode.getCurrentGameModesNames());
        scoreboard.clear();
        if (currentGameModesNames.isEmpty()) {
            scoreboard.add(StringValueConfiguration.NO_GAMEMODE.getValue());
            return;
        }
        if (currentGameModesNames.size() > 4)
            scoreboard.add(StringValueConfiguration.GAMEMODE.getValue().replace("<name>", "And " + (currentGameModesNames.size() - 4) + " more."));
        currentGameModesNames.stream().map(s -> StringValueConfiguration.GAMEMODE.getValue().replace("<name>", s)).limit(4).sorted(Comparator.reverseOrder()).collect(Collectors.toCollection(() -> scoreboard));
    }

    @EventHandler
    public void onGameTaskChangeEvent(GameTaskChangeEvent e) {
        if (e.getGameTaskStatus() == GameTaskStatus.NONE || e.getGameTaskStatus() == GameTaskStatus.OPENING || e.getGameTaskStatus() == GameTaskStatus.STARTING) {
             GameManager gameManager = GameManager.getGameManager();
              ArrayList<String> scoreboard = ListValueConfiguration.SCHEDULE_OPENING_STARTING.getScoreboard();
              String scheduleString = StringValueConfiguration.SCHEDULE.getValue(), openingString = StringValueConfiguration.OPENING.getValue(), startingString = StringValueConfiguration.STARTING.getValue();
            scoreboard.clear();
            scoreboard.add(e.getGameTaskStatus() == GameTaskStatus.NONE ? scheduleString : (e.getGameTaskStatus() == GameTaskStatus.STARTING ? startingString : openingString));
        }
    }
}
