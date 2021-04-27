package me.juan.uhc.manager.game;

import lombok.Getter;
import me.juan.uhc.configuration.permissions.PermissionsConfiguration;
import me.juan.uhc.event.GameStatusChangeEvent;
import me.juan.uhc.event.GameTaskChangeEvent;
import me.juan.uhc.utils.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

@Getter
public class GameManager {

    @Getter
    private static GameManager gameManager;
    private final Game game;
    private GameTask gameTask;
    private GameStatus gameStatus = GameStatus.GENERATING;

    public GameManager() {
        gameManager = this;
        this.game = new Game(new PremadeGame());
    }

    //TASKS
    public void setGameTask(GameTask gameTask) {
        if (this.gameTask != null) this.gameTask.cancel();
        this.gameTask = gameTask;
        Bukkit.getPluginManager().callEvent(new GameTaskChangeEvent());
    }

    private boolean isCountDownTask() {
        return hasGameTask() && gameTask instanceof GameTask.CountDownTask;
    }

    public boolean isNotScheduleTask() {
        return !hasGameTask() && isWaiting();
    }

    public boolean isOpeningTask() {
        return isWaiting() && isCountDownTask();
    }

    public boolean isStartingTask() {
        return isStarting() && isCountDownTask();
    }

    public boolean hasGameTask() {
        return gameTask != null;
    }


    //GAMESTATUS
    public void setGameStatus(GameStatus gameStatus) {
        if (!this.gameStatus.equals(gameStatus))
            Bukkit.getPluginManager().callEvent(new GameStatusChangeEvent(this.gameStatus, gameStatus));
        this.gameStatus = gameStatus;
    }

    public boolean isGenerating() {
        return gameStatus == GameStatus.GENERATING;
    }

    public boolean isStarting() {
        return gameStatus == GameStatus.STARTING;
    }

    public boolean isWaiting() {
        return gameStatus == GameStatus.WAITING;
    }

    public boolean isPlaying() {
        return gameStatus == GameStatus.PLAYING;
    }

    public boolean isScattering() {
        return gameStatus == GameStatus.SCATTERING;
    }

    public boolean isEnded() {
        return gameStatus == GameStatus.END;
    }


    //SCHEDULE
    public void scheduleGame(Date date) {
        Date currentDate = new Date();


    }


    //WAITING BOOLEANS

    public String isWhitelist(Player player) {
        String result = null;
        if (Bukkit.getOnlinePlayers().size() >= game.getPremadeGame().getSlots().getValue())
            return "§cThe player limit is full: " + game.getPremadeGame().getSlots() + "/" + game.getPremadeGame().getSlots() + "!";
        switch (gameStatus) {
            case WAITING:
            case STARTING:
            case GENERATING:
                boolean join = this.game.isWhitelist() && (this.game.getWhitelistNames().contains(player.getName().toLowerCase()) || PermissionsConfiguration.WHITELIST_BYPASS.contains(player));
                if (!join)
                    result = isGenerating() ? "§cThe world is being generated: " + PluginUtil.setGlobalPlaceholder("<percent>") + "%." : "§cYou are not on the whitelist.";
                break;
            case SCATTERING:
                result = "§cThey are teleporting to the players, wait a minute.";
                break;
            // case PLAYING:
            // case END:
        }
        return result;
    }

    //GAMETIMER
    public boolean isPvPEnabled() {
        return hasGameTask() && gameTask.getCounter() > game.getPremadeGame().getPvpTime().getValue();
    }

}
