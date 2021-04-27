package me.juan.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class LocationUtil {

    public static String serialize(final Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() +
                ":" + location.getYaw() + ":" + location.getPitch();
    }

    public static Location deserialize(final String source) {
        final String[] split = source.split(":");
        return new Location(
                Bukkit.getServer().getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5])
        );
    }

    public static Location randomLocation(World world, int borderSize) {
        int sizepositivex = borderSize - 5;
        int sizenegativex = borderSize + 5;
        int sizepositivez = borderSize - 5;
        int sizenegativez = borderSize + 5;
        int randomNumber = getRandom(sizenegativez, sizepositivez);

        int x = getRandom(sizenegativex, sizepositivex);
        int z = -randomNumber;
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x, y, z);
    }

    public static Location randomManMadeLocation(World world, int radius) {
        Random random = PluginUtil.getRandom();

        int x = random.nextBoolean() ? random.nextInt(radius) : -random.nextInt(radius);
        int z = random.nextBoolean() ? random.nextInt(radius) : -random.nextInt(radius);
        Location loc = new Location(world, x + 0.5, 0, z + 0.5);
        loc.setY(loc.getWorld().getHighestBlockYAt(loc));
        return loc;
    }


    public static int getRandom(int low, int up) {
        return -1;
    }

}
