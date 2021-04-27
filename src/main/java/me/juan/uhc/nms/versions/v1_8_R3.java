package me.juan.uhc.nms.versions;

import me.juan.uhc.nms.NMS;
import me.juan.uhc.utils.PluginUtil;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;

import java.util.HashMap;

public class v1_8_R3 implements NMS {


    private HashMap<Player, Integer> vehicles = new HashMap<>();

    @Override
    public void sendTittle(Player player, String title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        int windowId = entityPlayer.activeContainer.windowId;
        String s = (title.length() > 32) ? title.substring(0, 32) : title;
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowId, "minecraft:chest", new ChatMessage(s), player.getOpenInventory().getTopInventory().getSize());
        entityPlayer.playerConnection.sendPacket(packet);
    }

    @Override
    public void removeArrows(Player player) {
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
    }

//    @Override
//    public void addVehicle(Player player) {
//        player.setVelocity(new Vector(0, 0.1, 0));
//        player.setWalkSpeed(0.0F);
//        player.setFlySpeed(0.0F);
//        player.setFoodLevel(0);
//        player.setSprinting(false);
//        player.setAllowFlight(false);
//        player.setFlying(false);
//        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
//    }
//
//    @Override
//    public void removeVehicle(Player player) {
//        player.setWalkSpeed(0.2F);
//        player.setFlySpeed(0.0001F);
//        player.setFoodLevel(20);
//        player.setSprinting(true);
//        player.removePotionEffect(PotionEffectType.JUMP);
//    }

    @Override
    public void addVehicle(Player player) {
        Location location = player.getLocation();
        WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();

        EntityBat bat = new EntityBat(worldServer);
        bat.setLocation(location.getX() + 0.5, location.getY() + 2.0, location.getZ() + 0.5, 0.0f, 0.0f);
        bat.setHealth(bat.getMaxHealth());
        bat.setInvisible(true);
        bat.d(0);
        bat.setAsleep(true);
        bat.setAirTicks(10);
        bat.setSneaking(false);

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bat);
        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), bat);

        playerConnection.sendPacket(packet);
        playerConnection.sendPacket(attach);

        player.teleport(bat.getBukkitEntity().getLocation());
        vehicles.put(player, bat.getId());
    }

    @Override
    public void removeVehicle(Player player) {
        if (vehicles.get(player) != null) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(vehicles.get(player));
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(packet);
            vehicles.put(player, null);
        }
    }


    @Override
    public void removeMovement(Entity entity) {
        if (entity instanceof Item || entity instanceof Slime || entity instanceof Ghast || entity instanceof Bat || entity instanceof Spider || entity instanceof CaveSpider || entity instanceof Horse || entity instanceof Blaze || entity instanceof Squid)
            return;

        EntityCreature creature = (EntityCreature) ((CraftEntity) entity).getHandle();
        creature.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
        creature.getAttributeInstance(GenericAttributes.c).setValue(0.0);
        creature.getNavigation().n();
    }

    @Override
    public void removePlayer(Player player, Player target) {
        Packet packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, getEntity(player));
        getEntity(target).playerConnection.sendPacket(packet);
    }

    @Override
    public boolean isItem(Material mat) {
        return false;
    }

    @Override
    public void firework(Location location) {

    }


    public EntityPlayer getEntity(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public void sendPacket(Player player, Packet packet) {
        getEntity(player).playerConnection.sendPacket(packet);
    }

    public void broadcastWorld(Packet packet, Player player) {
        for (Player online : player.getWorld().getPlayers()) {
            if (online != null && online.getEntityId() != player.getEntityId() && online.canSee(player)) {
                sendPacket(online, packet);
            }
        }
    }

    public void broadcastServer(Packet packet, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != null && online.canSee(player)) {
                sendPacket(online, packet);
            }
        }
    }


    @Override
    public double getHealth(Player player) {
        return ((CraftPlayer) player).getHealth();
    }

    @Override
    public int getRandomFortuneDrops(Block block, int level) {
        return CraftMagicNumbers.getBlock(block).getDropCount(level, PluginUtil.getRandom());
    }
}

