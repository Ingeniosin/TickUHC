package me.juan.uhc.configuration.scoreboard;

import lombok.Getter;
import me.juan.uhc.configuration.ConfigurationFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.juan.uhc.utils.PluginUtil.getElement;

@Getter
public enum GeneralScoreboard {

    GENERATION(get("Sidebars.generating")),
    LOBBY(get("Sidebars.lobby")),
    SCATTER(get("Sidebars.scatter")),

    GAME_FFA_NORMAL(get("Sidebars.game.ffa.normal")),
    GAME_FFA_WINNER(get("Sidebars.game.ffa.winner")),

    GAME_TEAM_NORMAL(get("Sidebars.game.team.normal")),
    GAME_TEAM_WINNER(get("Sidebars.game.team.winner"));

    private final ArrayList<String> scoreboard;

    GeneralScoreboard(List<String> scoreboard) {
        Collections.reverse(scoreboard);
        this.scoreboard = new ArrayList<>(scoreboard);
    }

    private static List<String> get(String path) {
        return path != null ? getElement(List.class, ConfigurationFile.SCOREBOARD.getConfig().getConfigCursor(""), path, true) : null;
    }

}
