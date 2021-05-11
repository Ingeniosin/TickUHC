package me.juan.uhc.utils;

import com.google.gson.internal.Primitives;
import me.juan.uhc.Main;
import me.juan.uhc.manager.WorldManager;
import me.juan.uhc.manager.game.Game;
import me.juan.uhc.manager.GameManager;
import me.juan.uhc.manager.game.premade.PremadeGame;
import me.juan.uhc.nms.versions.v1_8_R3;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PluginUtil {

    public static Random random;
    public static DecimalFormat decimalFormat, decimalFormatTwo;

    /*
        AQUÍ SE ENCUENTRAN UTILIDADES EL PLUGIN, ENCONTRAMOS A:

        'setGlobalPlaceholder' DONDE SE REEMPLAZAN LAS VARIABLES A LOS PLACEHOLDERS NETAMENTE GLOBALES (ESTO ES PARA MENSAJES Y SCOREBOARD)

        'setScoreboardPlaceholder' LO MISMO QUE 'setGlobalPlaceholder' SOLO QUE ÚNICAMENTE A COSAS MAS ESPECIFICAS DE LA SCOREBOARD, COMO
        LAS KILLS DE UN USUARIO.

     */

    public static String setGlobalPlaceholder(String var) {
        return setGlobalPlaceholder(var, null, null);
    }

    public static String setGlobalPlaceholder(String var, String playerName) {
        return setGlobalPlaceholder(var, playerName, null);
    }

    public static String setGlobalPlaceholder(String var, String playerName, String[] customVar) {
        if (customVar != null) {
            String finalVar = var;
            var = IntStream.range(0, customVar.length).mapToObj(index -> finalVar.replaceAll(index == 0 ? "<var>" : "<var" + (index + 1) + ">", customVar[index])).collect(Collectors.joining());
        }
        GameManager gameManager = GameManager.getGameManager();
        Game game = gameManager != null ? gameManager.getGame() : null;
        PremadeGame premadeGame = game != null ? game.getPremadeGame() : null;
        return var
                .replaceAll("<playerName>", getOrNull(playerName))
                .replaceAll("<teams>", premadeGame != null ? getOrNull(premadeGame.getType()) : "")
                .replaceAll("<players>", "" + Bukkit.getOnlinePlayers().size())
                .replaceAll("<tps>", TpsMeter.getTps())
                .replaceAll("<percent>", getDecimalFormat().format(WorldManager.getPercentComplete()))
                .replaceAll("<memory>", TpsMeter.getMemory())
                .replaceAll("&", "§");
    }


    private static String getOrNull(String var) {           //<- MÉTODO PARA AHORRAR UN 'NullPointerException'
        return var == null ? "" : var;
    }

    public static Random getRandom() {
        if (random == null) random = new Random();
        return random;
    }

    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) decimalFormat = new DecimalFormat("##.#");
        return decimalFormat;
    }

    public static DecimalFormat getDecimalFormatTwo() {
        if (decimalFormatTwo == null) decimalFormatTwo = new DecimalFormat("##.##");
        return decimalFormatTwo;
    }

    public static Location randomLocation(World world, int radius) {
        Random random = getRandom();

        boolean calculating = true;
        int x = 0;
        int z = 0;
        while (calculating) {
            x = random.nextBoolean() ? random.nextInt(radius) : -random.nextInt(radius);
            z = random.nextBoolean() ? random.nextInt(radius) : -random.nextInt(radius);
            if (world.getHighestBlockAt(x, z).getType() != Material.STATIONARY_LAVA
                    || world.getHighestBlockAt(x, z).getType() != Material.LAVA) {
                calculating = false;
            }
        }
        return new Location(world, x + 0.5, world.getHighestBlockYAt(x, z), z + 0.5);
    }

    public static <T> T getElement(Class<T> classT, ConfigCursor configCursor, String value, boolean stopOnException) {
        return getElement(classT, configCursor, value, stopOnException, true);
    }

    public static String lines() {
        return "§7§m---------------------";
    }

    public static <T> T getElement(Class<T> classT, ConfigCursor configCursor, String value, boolean stopOnException, boolean printException) {    //<- VER ConfigurationFile.java
        try {
            if (classT.equals(String.class)) {
                String message = configCursor.getString(value);
                if (message == null) throw new Exception();
                return Primitives.wrap(classT).cast(message);
            } else if (classT.equals(boolean.class)) {
                if (!configCursor.exists(value))
                    return Primitives.wrap(classT).cast(true);
                return Primitives.wrap(classT).cast(configCursor.getBoolean(value));
            } else if (classT.equals(int.class)) {
                int size = configCursor.getInt(value);
                if (!configCursor.exists(value)) throw new Exception();
                return Primitives.wrap(classT).cast(size);
            } else {
                List<String> message = configCursor.getStringList(value);
                if (message == null || message.isEmpty()) throw new Exception();
                return Primitives.wrap(classT).cast(message);
            }
        } catch (Exception e) {
            if (printException)
                Main.getMain().getLogger().severe("The element with id '" + configCursor.getPath() + "." + value + "' in '" + configCursor.getFileConfig().getFileName() + "' does not exist.");
            if (stopOnException) Bukkit.shutdown();
        }
        return Primitives.wrap(classT).cast(null);
    }

    public static class ResetPlayer {

        public ResetPlayer(Player player, GameMode gameMode, Runnable runnable, boolean runAsync) {
            if (is_1_8()) {
                player.setAllowFlight(gameMode == GameMode.CREATIVE);                  //ESTO EN 1.8 NO PUEDE USARSE asíncrono!
                player.setFlying(gameMode == GameMode.CREATIVE);
            }
            player.setGameMode(gameMode);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> {
                if (!is_1_8()) {
                    player.setAllowFlight(gameMode == GameMode.CREATIVE);
                    player.setFlying(gameMode == GameMode.CREATIVE);
                }
                Main.getMain().getNms().removeArrows(player);
                player.setHealth(20.0D);
                player.setMaxHealth(20.0D);
                player.setSaturation(20.0F);
                player.setFallDistance(0.0F);
                player.setFoodLevel(20);
                player.setFireTicks(0);
                player.setMaximumNoDamageTicks(20);
                player.setTotalExperience(0);
                player.setLevel(0);
                player.setExp(0);
                player.setExp(0.0F);
                player.setLevel(0);
                player.setCanPickupItems(gameMode != GameMode.CREATIVE);
                player.setItemOnCursor(null);
                player.spigot().setCollidesWithEntities(gameMode != GameMode.CREATIVE);
                player.getInventory().setArmorContents(new ItemStack[4]);
                player.getInventory().setContents(new ItemStack[36]);
                player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
                if (runAsync) runnable.run();
                else Bukkit.getScheduler().runTask(Main.getMain(), runnable);
            });

        }

        private boolean is_1_8() {
            return Main.getMain().getNms() instanceof v1_8_R3;
        }
    }

}
