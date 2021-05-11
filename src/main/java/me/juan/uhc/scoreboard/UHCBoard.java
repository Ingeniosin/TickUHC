package me.juan.uhc.scoreboard;

import me.juan.uhc.configuration.scoreboard.GeneralScoreboard;
import me.juan.uhc.configuration.scoreboard.ScoreboardConfiguration;
import me.juan.uhc.manager.GameManager;
import me.juan.uhc.manager.game.premade.PremadeGame;
import me.juan.uhc.player.UHCPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class UHCBoard extends Board {

    private final Player player;
    private final UHCPlayer uhcPlayer;
    private final ArrayList<String> lines = new ArrayList<>();
    private final PremadeGame premadeGame;

    public UHCBoard(Player player) {
        super(player);
        this.player = player;
        this.uhcPlayer = UHCPlayer.getPlayerByUUID(player.getUniqueId());
        this.setTitle(ScoreboardConfiguration.getTitle());
        this.premadeGame = GameManager.getGameManager().getGame().getPremadeGame();
    }

    @Override
    public void update() {
        if (this.player == null || !this.player.isOnline()) return;
        lines.clear();
        switch (GameManager.getGameManager().getGameStatus()) {
            case GENERATING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, GeneralScoreboard.GENERATION);
                break;
            case WAITING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, GeneralScoreboard.LOBBY);
                break;
            case SCATTERING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, GeneralScoreboard.SCATTER);
                break;
            case PLAYING:
                ScoreboardConfiguration.inject(lines, uhcPlayer, premadeGame.isTeams() ? GeneralScoreboard.GAME_TEAM_NORMAL : GeneralScoreboard.GAME_FFA_NORMAL);
                break;
            case END:
                ScoreboardConfiguration.inject(lines, uhcPlayer, premadeGame.isTeams() ? GeneralScoreboard.GAME_TEAM_WINNER : GeneralScoreboard.GAME_FFA_WINNER);
                break;
        }
        this.setSlotsFromList(lines);
    }

}
