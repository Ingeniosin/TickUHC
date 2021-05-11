package me.juan.uhc.manager;

import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import com.wimbli.WorldBorder.Events.WorldBorderFillStartEvent;
import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.configuration.worlds.WorldsConfiguration;
import me.juan.uhc.manager.game.GameStatus;
import me.juan.uhc.utils.PluginUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class WorldManager {

    @Getter
    private static double percentComplete = 0;
    private final Set<Integer> materialBypass = new HashSet<>(Arrays.asList(17, 162, 18, 161, 0, 81, 175, 31, 37, 38, 175, 39, 40));
    private final String path;
    private final GameManager gameManager;

    private final WorldsConfiguration.WorldMap NORMAL = WorldsConfiguration.NORMAL.get();
    private final WorldsConfiguration.WorldMap NETHER = WorldsConfiguration.NETHER.get();
    private final WorldsConfiguration.WorldMap PRACTICE = WorldsConfiguration.PRACTICE.get();


    public WorldManager() {
        String absolutePath = Main.getMain().getServer().getWorldContainer().getAbsolutePath();
        this.path = absolutePath.substring(0, absolutePath.length() - 2);
        this.gameManager = GameManager.getGameManager();
        WorldsConfiguration.LOBBY.get().setWorld(setAttributes(Bukkit.getWorlds().get(0), false));             //<- Se inicializa el mundo Lobby
        initWorlds();
    }

    public static void saveWorlds() {
        Arrays.stream(WorldsConfiguration.values()).forEach(worldsConfiguration -> {
            World world = worldsConfiguration.get().getWorld();
            if (world != null) world.save();
        });
    }

    public static void makeScatterSpawns(List<Location> list, WorldsConfiguration.WorldMap worldMap) {
        list.clear();
        Bukkit.getScheduler().runTask(Main.getMain(), () -> IntStream.range(0, 900).forEach(value -> {
            Location location = PluginUtil.randomLocation(worldMap.getWorld(), worldMap.getMaxSize() - 5);
            if (location.getBlockY() > 55) list.add(location.add(0, 1, 0));
        }));
    }

    private boolean onUhcWorldGenerated() {
        shrinkBorder(NORMAL.getMaxSize(), 5);
        saveWorlds();
        if (!NETHER.isEnabled()) finishGen(false);
        return NETHER.isEnabled();
    }

    private void initWorlds() {                                         //<- No es necesario entenderlo, dejalo asi, que asi corre bien melo
        if (!isUsed()) {
            if (isWorldGen()) {
                initWorld(NORMAL, World.Environment.NORMAL, false);
                initWorld(NETHER, World.Environment.NETHER, false);
                loadWorld(PRACTICE, false);
                makeScatterSpawns(gameManager.getGame().getScatterLocations(), NORMAL);
                gameManager.setGameStatus(GameStatus.WAITING);
                return;
            }
            new WorldGenerator(() -> initWorld(NORMAL, World.Environment.NORMAL, true), () -> {
                if (onUhcWorldGenerated())
                    new WorldGenerator(() -> initWorld(NETHER, World.Environment.NETHER, true), () -> finishGen(true));
            });
        } else {
            try {
                deleteWorlds();
                initWorlds();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.shutdown();
            }
        }

    }

    private void finishGen(boolean nether) {
        Bukkit.getConsoleSender().sendMessage("§a------------------------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§c" + NORMAL.getName() + ": §aGenerated and loaded correctly.");
        if (nether)
            Bukkit.getConsoleSender().sendMessage("§c" + NETHER.getName() + ": §aGenerated and loaded correctly.");
        Bukkit.getConsoleSender().sendMessage("§a------------------------------------------------------");
        Bukkit.shutdown();
    }

    private void setSize(WorldsConfiguration.WorldMap worldMap, boolean gen) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + worldMap.getName() + " set " + worldMap.getMaxSize() + " " + worldMap.getMaxSize() + " 0 0");
        if (gen) genWorld(worldMap);
    }

    private void genWorld(WorldsConfiguration.WorldMap worldMap) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + worldMap.getName() + " fill 1000");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
    }

    private void initWorld(WorldsConfiguration.WorldMap worldMap, World.Environment environment, boolean gen) {
        if (worldMap.isEnabled()) {
            worldMap.setWorld(setAttributes(new WorldCreator(worldMap.getName()).environment(environment).type(WorldType.NORMAL).createWorld(), true));
            setSize(worldMap, gen);
        }
    }

    private void loadWorld(WorldsConfiguration.WorldMap worldMap, boolean gen) {
        if (worldMap.isEnabled()) {
            worldMap.setWorld(setAttributes(new WorldCreator(worldMap.getName()).createWorld(), true));
            setSize(worldMap, gen);
        }
    }

    private boolean isUsed() {
        return new File(path + "/" + NORMAL.getName()).exists() && new File(path + "/" + NORMAL.getName() + "/used").exists();
    }

    public void setUsed(boolean used) {
        File file = new File(path + "/" + NORMAL.getName() + "/used");
        try {
            if (used) FileUtils.forceMkdir(file);
            else FileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isWorldGen() {
        return new File(path + "/" + NORMAL.getName()).exists() && (!NETHER.isEnabled() || new File(path + "/" + NETHER.getName()).exists());
    }

    private void deleteWorlds() throws IOException {
        FileUtils.forceDelete(new File(path + "/" + NORMAL.getName()));
        FileUtils.forceDelete(new File(path + "/" + NETHER.getName()));
    }

    private World setAttributes(World world, boolean pvp) {
        world.setTime(0L);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setWeatherDuration(0);
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.setPVP(pvp);
        world.setAutoSave(false);
        return world;
    }

    private void figureBlock(int x, int z) {
        World world = NORMAL.getWorld();
        Block block = world.getHighestBlockAt(x, z);
        Block below = block.getRelative(BlockFace.DOWN);
        while (materialBypass.contains(below.getTypeId()) && below.getY() > 5)
            below = below.getRelative(BlockFace.DOWN);
        Material material = Material.BEDROCK;
        Block up = below.getRelative(BlockFace.UP);
        up.setType(material);
        up.getState().update(false);
    }

    public void shrinkBorder(int radius, int high) {
        for (int i = 0; i < high; i++)
            Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> addBorder(radius), i);
    }

    private void addBorder(int radius) {
        new BukkitRunnable() {
            int count = -radius - 1;
            int maxCounter;
            int x;
            boolean phase1 = false;
            boolean phase2 = false;
            boolean phase3 = false;

            @Override
            public void run() {
                if (!phase1) {
                    maxCounter = count + 500;
                    x = -radius - 1;
                    for (int z = count; z <= radius && count <= maxCounter; z++, count++) figureBlock(x, z);
                    if (count >= radius) {
                        count = -radius - 1;
                        phase1 = true;
                    }
                    return;
                }
                if (!phase2) {
                    maxCounter = count + 500;
                    x = radius;
                    for (int z = count; z <= radius && count <= maxCounter; z++, count++) figureBlock(x, z);
                    if (count >= radius) {
                        count = -radius - 1;
                        phase2 = true;
                    }
                    return;
                }
                if (!phase3) {
                    maxCounter = count + 500;
                    int z = -radius - 1;
                    for (int x = count; x <= radius && count <= maxCounter; x++, count++) {
                        if (x == radius || x == -radius - 1) continue;
                        figureBlock(x, z);
                    }
                    if (count >= radius) {
                        count = -radius - 1;
                        phase3 = true;
                    }
                    return;
                }

                maxCounter = count + 500;
                for (int x = count; x <= radius && count <= maxCounter; x++, count++) {
                    if (x == radius || x == -radius - 1) continue;
                    figureBlock(x, radius);
                }
                if (count >= radius) this.cancel();
            }
        }.runTaskTimer(Main.getMain(), 0, 15);
    }


    private static class WorldGenerator implements Listener {

        private final Runnable onComplete;
        private BukkitTask worldBorderTask;

        public WorldGenerator(Runnable runnable, Runnable onComplete) {
            this.onComplete = onComplete;
            Bukkit.getPluginManager().registerEvents(this, Main.getMain());
            Bukkit.getScheduler().runTaskLater(Main.getMain(), runnable, 2 * 20L);
        }

        @EventHandler
        public void onWorldBorderFillStart(WorldBorderFillStartEvent e) {
            worldBorderTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMain(), () -> percentComplete = e.getFillTask().getPercentageCompleted(), 0, 1);
        }

        @EventHandler
        public void onWorldBorderFillFinished(WorldBorderFillFinishedEvent e) {
            HandlerList.unregisterAll(this);
            worldBorderTask.cancel();
            percentComplete = 0.0;
            Bukkit.getScheduler().runTaskLater(Main.getMain(), onComplete, 2 * 20L);
        }
    }
}
