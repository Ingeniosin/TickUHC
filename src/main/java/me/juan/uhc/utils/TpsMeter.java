package me.juan.uhc.utils;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import static me.juan.uhc.utils.PluginUtil.getDecimalFormatTwo;

public class TpsMeter extends BukkitRunnable {

    private static final long[] TICKS = new long[600];
    private static int TICK_COUNT = 0;
    @Getter
    private static String tps, memory;


    private static String checkMemory() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + "/" + runtime.totalMemory() / 1048576L;
    }

    private static double checkTps() {
        try {
            if (TICK_COUNT < 100) return 20.0D;
            int target = (TICK_COUNT - 1 - 100) % TICKS.length;
            long elapsed = System.currentTimeMillis() - TICKS[target];
            return Math.min((100 / (elapsed / 1000.0D) + 0.05), 20.0);
        } catch (Exception ignored) {
        }
        return 20.0;
    }

    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;
        tps = getDecimalFormatTwo().format(checkTps());
        memory = checkMemory();
    }

}
