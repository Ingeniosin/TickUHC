package me.juan.uhc.configuration.scoreboard;

import lombok.Getter;
import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.utils.PluginUtil;

import static me.juan.uhc.utils.PluginUtil.getElement;

@Getter
public enum StringValueConfiguration {

    TITLE(PluginUtil.setGlobalPlaceholder(get("Title"), null, null)),

    SCHEDULE(get("Values.schedule")),
    OPENING(get("Values.opening")),
    STARTING(get("Values.starting")),

    GAMEMODE(get("Values.gamemode")),
    NO_GAMEMODE(get("Values.nogamemode")),

    BORDER(get("Values.border")),
    BORDER_COUNT(get("Values.bordercount")),
    DEATH_MATCH(get("Values.deathmatch"));

    @Getter
    private final String value;

    StringValueConfiguration(String value) {
        this.value = value;
    }

    private static String get(String path) {
        return path != null ? getElement(String.class, ConfigurationFile.SCOREBOARD.getConfig().getConfigCursor(""), path, true) : null;
    }

}
