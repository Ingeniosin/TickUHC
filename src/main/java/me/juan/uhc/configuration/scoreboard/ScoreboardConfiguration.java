package me.juan.uhc.configuration.scoreboard;

import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.event.GameTaskChangeEvent;
import me.juan.uhc.event.ScenarioChangeEvent;
import me.juan.uhc.manager.FrozenManager;
import me.juan.uhc.manager.game.GameManager;
import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.player.UHCPlayer;
import me.juan.uhc.utils.CountDown;
import me.juan.uhc.utils.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.stream.Collectors;

import static me.juan.uhc.configuration.scoreboard.ScoreboardConfiguration.GeneralScoreboard.ListValueConfiguration.StringValueConfiguration.*;
import static me.juan.uhc.utils.PluginUtil.getElement;


public class ScoreboardConfiguration {


    public static void init() {
        GeneralScoreboard.ListValueConfiguration.StringValueConfiguration.values();
        Arrays.stream(GeneralScoreboard.ListValueConfiguration.values()).forEach(listValueConfiguration -> GeneralScoreboard.ListValueConfiguration.identifierHash.put(listValueConfiguration.identifierValue, listValueConfiguration));
        GeneralScoreboard.values();
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), Main.getMain());
    }

    public static void inject(ArrayList<String> list, UHCPlayer uhcPlayer, GeneralScoreboard generalScoreboard) {
        generalScoreboard.getScoreboard().forEach(s -> GeneralScoreboard.ListValueConfiguration.setPlaceholder(list, s, uhcPlayer));
    }

    public static String getTitle() {
        return TITLE.value;
    }

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

        enum ListValueConfiguration {

            SCHEDULE_OPENING_STARTING(new ArrayList<>(Collections.singletonList(StringValueConfiguration.SCHEDULE.value)), "<schedule-opening-starting>"),

            GAMEMODE(new ArrayList<>(Collections.singletonList(StringValueConfiguration.NO_GAMEMODE.value)), "<gamemode>"),

            HOST_MOD(get("Values.host-mod"), "<host-mod>"),
            NO_CLEAN(get("Values.noclean"), "<noclean>"),
            DO_NOT_DISTURB(get("Values.donotdisturb"), "<donotdisturb>"),
            FROZEN(get("Values.frozen"), "<frozen>");

            private static final TreeMap<String, ListValueConfiguration> identifierHash = new TreeMap<>();
            private final String identifierValue;
            private final ArrayList<String> scoreboard;


            ListValueConfiguration(List<String> scoreboard, String identifierValue) {
                Collections.reverse(scoreboard);
                this.scoreboard = new ArrayList<>(scoreboard);
                this.identifierValue = identifierValue;
            }

            private static void setPlaceholder(ArrayList<String> outPut, String listString, UHCPlayer uhcPlayer) {
                ListValueConfiguration configuration = identifierHash.getOrDefault(listString, null);
                if (configuration == null) {
                    outPut.add(PluginUtil.setGlobalPlaceholder(listString, uhcPlayer.getName(), null));
                    return;
                }
                GameManager gameManager = GameManager.getGameManager();
                ArrayList<String> scoreboard = configuration.scoreboard;
                UHCPlayer.CurrentData currentData = uhcPlayer.getCurrentData();
                switch (configuration) {
                    case NO_CLEAN:
                    case DO_NOT_DISTURB:
                        boolean isDoNotDisturb = configuration == DO_NOT_DISTURB;
                        final CountDown doNotDisturb = currentData.getDoNotDisturb(), noClean = currentData.getNoClean();
                        if ((isDoNotDisturb && doNotDisturb.hasExpired()) || !isDoNotDisturb && noClean.hasExpired())
                            return;
                        addToList(scoreboard, outPut, "<count>", (isDoNotDisturb ? doNotDisturb : noClean).getTimeLeft());
                        return;
                    case SCHEDULE_OPENING_STARTING:
                        if (!gameManager.hasGameTask()) break;
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

            enum StringValueConfiguration {

                TITLE(PluginUtil.setGlobalPlaceholder(get("Title"), null, null)),

                SCHEDULE(get("Values.schedule")),
                OPENING(get("Values.opening")),
                STARTING(get("Values.starting")),

                GAMEMODE(get("Values.gamemode")),
                NO_GAMEMODE(get("Values.nogamemode")),

                BORDER(get("Values.border")),
                BORDER_COUNT(get("Values.bordercount")),
                DEATH_MATCH(get("Values.deathmatch"));

                private final String value;

                StringValueConfiguration(String value) {
                    this.value = value;
                }

                private static String get(String path) {
                    return path != null ? getElement(String.class, ConfigurationFile.SCOREBOARD.getConfig().getConfigCursor(""), path, true) : null;
                }

            }
        }
    }

    public static class ScoreboardListener implements Listener {

        @EventHandler
        public void onGameModeChangeEvent(ScenarioChangeEvent e) {
            ArrayList<String> scoreboard = GeneralScoreboard.ListValueConfiguration.GAMEMODE.scoreboard;
            ArrayList<String> currentGameModesNames = new ArrayList<>(GameMode.getCurrentGameModesNames());
            scoreboard.clear();
            if (currentGameModesNames.isEmpty()) scoreboard.add(NO_GAMEMODE.value);
            else {
                if (currentGameModesNames.size() > 4)
                    scoreboard.add(GAMEMODE.value.replace("<name>", "And " + (currentGameModesNames.size() - 4) + " more."));
                currentGameModesNames.stream().map(s -> GAMEMODE.value.replace("<name>", s)).limit(4).sorted(Comparator.reverseOrder()).collect(Collectors.toCollection(() -> scoreboard));
            }
        }

        @EventHandler
        public void onGameTaskChangeEvent(GameTaskChangeEvent e) {
            GameManager gameManager = GameManager.getGameManager();
            ArrayList<String> scoreboard = GeneralScoreboard.ListValueConfiguration.SCHEDULE_OPENING_STARTING.scoreboard;
            String scheduleString = SCHEDULE.value, openingString = OPENING.value, startingString = STARTING.value;
            if (gameManager.isNotScheduleTask() || gameManager.isStartingTask() || gameManager.isOpeningTask()) {
                scoreboard.clear();
                if (gameManager.isNotScheduleTask()) scoreboard.add(scheduleString);
                else if (gameManager.isStartingTask()) scoreboard.add(startingString);
                else if (gameManager.isOpeningTask()) scoreboard.add(openingString);
            }
        }
    }


}
