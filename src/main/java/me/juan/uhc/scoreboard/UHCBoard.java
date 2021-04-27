package me.juan.uhc.scoreboard;

import me.juan.uhc.configuration.scoreboard.ScoreboardConfiguration;
import me.juan.uhc.manager.game.GameManager;
import me.juan.uhc.player.UHCPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class UHCBoard extends Board {

    private final Player player;
    private final UHCPlayer uhcPlayer;
    private final ArrayList<String> lines = new ArrayList<>();

    public UHCBoard(Player player) {
        super(player);
        this.player = player;
        this.uhcPlayer = UHCPlayer.getPlayerByUUID(player.getUniqueId());
        this.setTitle(ScoreboardConfiguration.getTitle());
    }

    @Override
    public void update() {
        if (this.player == null || !this.player.isOnline()) return;
        lines.clear();
        switch (GameManager.getGameManager().getGameStatus()) {
            case GENERATING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, ScoreboardConfiguration.GeneralScoreboard.GENERATION);
                break;
            case WAITING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, ScoreboardConfiguration.GeneralScoreboard.LOBBY);
                break;
            case SCATTERING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, ScoreboardConfiguration.GeneralScoreboard.SCATTER);
                break;
            case PLAYING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, ScoreboardConfiguration.GeneralScoreboard.GAME_FFA_NORMAL);
                break;
            case END:
                ScoreboardConfiguration.inject(lines, uhcPlayer, ScoreboardConfiguration.GeneralScoreboard.GAME_FFA_WINNER);
                break;
        }
        this.setSlotsFromList(lines);
    }

}
