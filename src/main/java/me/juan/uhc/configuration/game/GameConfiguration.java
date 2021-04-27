package me.juan.uhc.configuration.game;

import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.utils.PluginUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameConfiguration {

    private static List<Integer> borderShrinks;

    public static List<Integer> getBorderShrinks() {
        if (borderShrinks == null) {
            List<Integer> list = new ArrayList<>();
            PluginUtil.getElement(List.class, ConfigurationFile.MAIN.getConfig().getConfigCursor("Game"), "shrinks", true).stream().map(o -> Integer.parseInt(o.toString())).collect(Collectors.toCollection(() -> list));
            borderShrinks = list;
        }
        return borderShrinks;
    }

    public static int getLastBorderShrink() {
        return getBorderShrinks().get(getBorderShrinks().size() - 1);
    }

}
