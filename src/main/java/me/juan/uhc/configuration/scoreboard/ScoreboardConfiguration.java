package me.juan.uhc.configuration.scoreboard;

import me.juan.uhc.Main;
import me.juan.uhc.manager.FrozenManager;
import me.juan.uhc.manager.GameManager;
import me.juan.uhc.manager.game.task.GameTaskStatus;
import me.juan.uhc.player.UHCPlayer;
import me.juan.uhc.utils.CountDown;
import me.juan.uhc.utils.PluginUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static me.juan.uhc.configuration.scoreboard.StringValueConfiguration.TITLE;


public class ScoreboardConfiguration {


    public static void init() {
        StringValueConfiguration.values();
        Arrays.stream(ListValueConfiguration.values()).forEach(listValueConfiguration -> ListValueConfiguration.getIdentifierHash().put(listValueConfiguration.getIdentifierValue(), listValueConfiguration));
        GeneralScoreboard.values();
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), Main.getMain());
    }

    public static void inject(ArrayList<String> list, UHCPlayer uhcPlayer, GeneralScoreboard generalScoreboard) {
        generalScoreboard.getScoreboard().forEach(s -> setPlaceholder(list, s, uhcPlayer));
    }

    public static String getTitle() {
        return TITLE.getValue();
    }

    public static void setPlaceholder(ArrayList<String> outPut, String listString, UHCPlayer uhcPlayer) {
        ListValueConfiguration configuration = ListValueConfiguration.getIdentifierHash().getOrDefault(listString, null);
        if (configuration == null) {
            outPut.add(PluginUtil.setGlobalPlaceholder(listString, uhcPlayer.getName(), null));
            return;
        }
        GameManager gameManager = GameManager.getGameManager();
        ArrayList<String> scoreboard = configuration.getScoreboard();
        UHCPlayer.CurrentData currentData = uhcPlayer.getCurrentData();
        switch (configuration) { //Return = No aparece.
            case NO_CLEAN:
            case DO_NOT_DISTURB:
                boolean isDoNotDisturb = configuration == ListValueConfiguration.DO_NOT_DISTURB;
                final CountDown doNotDisturb = currentData.getDoNotDisturb(), noClean = currentData.getNoClean();
                if ((isDoNotDisturb && doNotDisturb.hasExpired()) || !isDoNotDisturb && noClean.hasExpired()) return;
                addToList(scoreboard, outPut, "<count>", (isDoNotDisturb ? doNotDisturb : noClean).getTimeLeft());
                return;
            case SCHEDULE_OPENING_STARTING:
                if (gameManager.getGameTaskStatus() == GameTaskStatus.NONE) break;
                addToList(scoreboard, outPut, "<count>", "" + gameManager.getGameTask().getCounter());
                return;
            case FROZEN:
                if (!FrozenManager.getFrozenManager().getFrozenPlayers().contains(uhcPlayer.getUuid())) return;
                break;
        }
        addToList(scoreboard, outPut);
    }

    private static void addToList(ArrayList<String> list, ArrayList<String> outPut) {
        addToList(list, outPut, null, null);
    }

    private static void addToList(ArrayList<String> list, ArrayList<String> outPut, String var, String replace) {
        boolean isValid = var != null && replace != null;
        list.stream().map(s -> PluginUtil.setGlobalPlaceholder(isValid ? s.replace(var, replace) : s)).collect(Collectors.toCollection(() -> outPut));
    }


}
