package me.juan.uhc.configuration.worlds;

import lombok.Getter;
import lombok.Setter;
import me.juan.uhc.Main;
import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.utils.ConfigCursor;
import me.juan.uhc.utils.FileConfig;
import org.bukkit.World;

import static me.juan.uhc.utils.PluginUtil.getElement;

public enum WorldsConfiguration {

    NORMAL(new WorldMap("normal")),
    NETHER(new WorldMap("nether")),
    PRACTICE(new WorldMap("practice")),
    LOBBY(new WorldMap(""));

    private final WorldMap worldMap;

    WorldsConfiguration(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public WorldMap get() {
        return worldMap;
    }

    @Getter
    public static class WorldMap {

        @Setter
        private World world;
        private String name;
        private int maxSize;
        private boolean enabled;

        private WorldMap(String path) {
            FileConfig fileConfig = ConfigurationFile.MAIN.getConfig();
            ConfigCursor configCursor = fileConfig.getConfigCursor("Worlds." + path);
            if (path.equals("")) return;
            if (path.equalsIgnoreCase("nether") && Main.getMain().isMeetup()) return;
            if (getElement(boolean.class, configCursor, "enabled", true)) {
                this.name = getElement(String.class, configCursor, "name", true);
                this.maxSize = getElement(int.class, configCursor, "max-size", true);
                this.enabled = true;
            }
        }
    }

}
