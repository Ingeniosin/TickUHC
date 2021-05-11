package me.juan.uhc.configuration.scoreboard;

import lombok.Getter;
import me.juan.uhc.configuration.ConfigurationFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static me.juan.uhc.utils.PluginUtil.getElement;

@Getter
public enum ListValueConfiguration {

    SCHEDULE_OPENING_STARTING(new ArrayList<>(Collections.singletonList(StringValueConfiguration.SCHEDULE.getValue())), "<schedule-opening-starting>"),

    GAMEMODE(new ArrayList<>(Collections.singletonList(StringValueConfiguration.NO_GAMEMODE.getValue())), "<gamemode>"),

    HOST_MOD(get("Values.host-mod"), "<host-mod>"),
    NO_CLEAN(get("Values.noclean"), "<noclean>"),
    DO_NOT_DISTURB(get("Values.donotdisturb"), "<donotdisturb>"),
    FROZEN(get("Values.frozen"), "<frozen>");

    @Getter
    private static final TreeMap<String, ListValueConfiguration> identifierHash = new TreeMap<>();
    private final String identifierValue;
    private final ArrayList<String> scoreboard;


    ListValueConfiguration(List<String> scoreboard, String identifierValue) {
        Collections.reverse(scoreboard);
        this.scoreboard = new ArrayList<>(scoreboard);
        this.identifierValue = identifierValue;
    }

    private static List<String> get(String path) {
        return path != null ? getElement(List.class, ConfigurationFile.SCOREBOARD.getConfig().getConfigCursor(""), path, true) : null;
    }
}
