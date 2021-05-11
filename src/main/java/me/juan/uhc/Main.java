package me.juan.uhc;

import lombok.Getter;
import me.juan.uhc.commands.FreezeCommand;
import me.juan.uhc.commands.PracticeCommand;
import me.juan.uhc.commands.UHCCommand;
import me.juan.uhc.commands.WhitelistCommand;
import me.juan.uhc.configuration.ConfigurationFile;
import me.juan.uhc.configuration.hotbar.HotbarConfiguration;
import me.juan.uhc.configuration.lang.LangConfiguration;
import me.juan.uhc.configuration.nametag.NameTagConfiguration;
import me.juan.uhc.configuration.permissions.PermissionsConfiguration;
import me.juan.uhc.configuration.scoreboard.ScoreboardConfiguration;
import me.juan.uhc.configuration.worlds.WorldsConfiguration;
import me.juan.uhc.listener.GlobalListener;
import me.juan.uhc.listener.PlayerListener;
import me.juan.uhc.manager.FrozenManager;
import me.juan.uhc.manager.PracticeManager;
import me.juan.uhc.manager.WorldManager;
import me.juan.uhc.manager.GameManager;
import me.juan.uhc.manager.game.GameStatus;
import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.manager.gamemode.modes.NoClean;
import me.juan.uhc.menu.buttons.ConfigButton;
import me.juan.uhc.nms.NMS;
import me.juan.uhc.nms.versions.v1_7_R4;
import me.juan.uhc.nms.versions.v1_8_R3;
import me.juan.uhc.player.UHCPlayer;
import me.juan.uhc.task.ScoreboardTask;
import me.juan.uhc.utils.ConfigCursor;
import me.juan.uhc.utils.TpsMeter;
import me.juan.utils.database.MongoDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main main;
    private MongoDB.MongoDatabase mongoDatabase;
    private boolean meetup;
    private NMS nms;

    @Override
    public void onEnable() {
        main = this;
        long time = System.currentTimeMillis();
        loadConfigurations();
        if (!initDB()) return;
        this.nms = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].contains("1_8") ? new v1_8_R3() : new v1_7_R4();

        new GameManager();
        new WorldManager();             //<- CONTIENE UN LISTENER AUTO REGULABLE
        if (GameManager.getGameManager().getGameStatus() != GameStatus.GENERATING && WorldsConfiguration.PRACTICE.get().isEnabled())
            new PracticeManager();      //<- CONTIENE UN LISTENER AUTO REGULABLE
        new FrozenManager();

        initListeners();
        initCommands();
        initTasks();
        initScenarios();
        ConfigButton.loadButtons();

        this.getLogger().info(" ");
        this.getLogger().info("The plugin has started correctly, loading time: " + (System.currentTimeMillis() - time) + "ms.");
        this.getLogger().info(" ");
    }

    @Override
    public void onDisable() {
        UHCPlayer.getUhcPlayers().values().forEach(uhcPlayer -> uhcPlayer.getPlayerData().save());
        WorldManager.saveWorlds();
    }

    private void initTasks() {
        new ScoreboardTask().runTaskTimerAsynchronously(this, 0, 2L);
        new TpsMeter().runTaskTimerAsynchronously(this, 0, 1L);
    }

    private boolean initDB() {
        this.getLogger().info("Starting connection to the database...");
        ConfigCursor configCursor = ConfigurationFile.MAIN.getConfig().getConfigCursor("Database.mongodb");
        MongoDB mongoDB = new MongoDB(configCursor.getString("url"));
        mongoDB.connect();
        if (mongoDB.isConnected()) {
            mongoDatabase = mongoDB.getDatabase(configCursor.getString("database"), configCursor.getString("collection"));
            this.getLogger().info("The connection to the database was successful.");
            return true;
        }
        this.getLogger().severe("Unable to connect to the database, verify credentials!");
        Bukkit.shutdown();
        return false;
    }

    private void initScenarios() {
        this.getLogger().info("Loading default scenarios.");
        int val = 50;
        for (int i = 0; i < val; i++) {
            new NoClean(i);
        }
        this.getLogger().info(GameMode.getAvailableGameModes().size() + " scenarios loaded!");
    }

    private void loadConfigurations() {
        ConfigurationFile.values();
        HotbarConfiguration.values();
        LangConfiguration.values();
        NameTagConfiguration.values();
        PermissionsConfiguration.values();
        ScoreboardConfiguration.init();
        WorldsConfiguration.values();
        this.meetup = ConfigurationFile.MEETUP.getConfig().getConfigCursor("meetup").getBoolean("enable");
    }

    private void initListeners() {                                  //<- VER Listener.java
        this.getLogger().info("Loading listeners...");
        PluginManager plm = Bukkit.getPluginManager();
        plm.registerEvents(new GlobalListener(), this);
        plm.registerEvents(new PlayerListener(), this);
        this.getLogger().info("The listeners were loaded correctly.");
    }

    private void initCommands() {
        this.getLogger().info("Loading Commands...");
        this.getCommand("uhc").setExecutor(new UHCCommand());
        this.getCommand("practice").setExecutor(new PracticeCommand());
        this.getCommand("freeze").setExecutor(new FreezeCommand());
        this.getCommand("whitelist").setExecutor(new WhitelistCommand());
        this.getLogger().info("The Commands were loaded correctly.");
    }

}
