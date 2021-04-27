package me.juan.uhc.configuration.nametag;

import lombok.Getter;
import me.juan.uhc.configuration.ConfigurationFile;

import static me.juan.uhc.utils.PluginUtil.getElement;

public enum NameTagConfiguration {

    TEAM(get("team")),
    ENEMY(get("enemy")),
    SPECTATOR(get("spectator")),
    ASSIGNED(get("assigned")),
    NO_CLEAN(get("noclean"));

    @Getter
    private final String tag;

    NameTagConfiguration(String tag) {
        this.tag = tag;
    }

    private static String get(String path) {
        return getElement(String.class, ConfigurationFile.MAIN.getConfig().getConfigCursor("Nametag"), path, true, true);
    }

}