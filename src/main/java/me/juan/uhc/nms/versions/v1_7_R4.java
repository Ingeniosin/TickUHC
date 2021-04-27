package me.juan.uhc.nms.versions;

import lombok.Getter;
import me.juan.uhc.Main;
import me.juan.uhc.nms.NMS;
import me.juan.uhc.utils.PluginUtil;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;

public class v1_7_R4 implements NMS {

    @Getter
    private HashMap<Player, Integer> vehicles = new HashMap<>();

    @Override
    public void sendTittle(Player player, String title) {
        EntityPlayer entityPlayer = getEntity(player);
        int windowId = entityPlayer.activeContainer.windowId;
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowId, 0, (title.length() > 32) ? title.substring(0, 32) : title, player.getOpenInventory().getTopInventory().getSize(), true);
        entityPlayer.playerConnection.sendPacket(packet);
    }

    @Override
    public void removeArrows(Player player) {
        getEntity(player).getDataWatcher().watch(9, (byte) 0);
    }

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

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bat);
        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, getEntity(player), bat);

        getEntity(player).playerConnection.sendPacket(packet);
        getEntity(player).playerConnection.sendPacket(attach);

        vehicles.put(player, bat.getId());
    }

    @Override
    public void removeVehicle(Player player) {
        if (vehicles.get(player) != null) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(vehicles.get(player));
            getEntity(player).playerConnection.sendPacket(packet);

            vehicles.put(player, null);
        }
    }


//    @Override
//    public void summonFakePlayer(Player player) {
//        UHCPlayer uhcPlayer = UHCPlayer.getByUuid(player.getUniqueId());
//        Location location = player.getLocation();
//
//        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
//        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
//
//        EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(uhcPlayer.getUuid(), uhcPlayer.getName()), new PlayerInteractManager(world));
//        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
//
//        Common.getOnlinePlayers().forEach(players -> {
//            PlayerConnection connection = ((CraftPlayer) players).getHandle().playerConnection;
//            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
//        });
//
//        Bukkit.getScheduler().runTaskLaterAsynchronously(UHC.getInstance(), () -> Common.getOnlinePlayers().forEach(players -> {
//            PlayerConnection connection = ((CraftPlayer) players).getHandle().playerConnection;
//            connection.sendPacket(new PacketPlayOutEntityStatus(npc, (byte) 3));
//        }), 2L);
//    }

    @Override
    public void removeMovement(Entity entity) {
        if (entity instanceof Item || entity instanceof Slime || entity instanceof Ghast || entity instanceof Bat || entity instanceof Spider || entity instanceof CaveSpider || entity instanceof Horse || entity instanceof Blaze || entity instanceof Squid)
            return;

        EntityCreature creature = (EntityCreature) ((CraftEntity) entity).getHandle();
        creature.getAttributeInstance(GenericAttributes.d).setValue(0.0D);
        creature.getAttributeInstance(GenericAttributes.b).setValue(0.0D);
        creature.getNavigation().h();
    }

    @Override
    public void removePlayer(Player player, Player target) {
        Packet packet = PacketPlayOutPlayerInfo.removePlayer(getEntity(player));
        getEntity(target).playerConnection.sendPacket(packet);
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
    public void firework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.PURPLE)
                .build());
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
        ((CraftFirework) fw).getHandle().setInvisible(true);

        Bukkit.getServer().getScheduler().runTaskLater(Main.getMain(), () ->
        {
            WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
            EntityFireworks fireworks = ((CraftFirework) fw).getHandle();
            world.broadcastEntityEffect(fireworks, (byte) 17);
            fireworks.die();
        }, 1);
    }

    @Override
    public boolean isItem(Material mat) {
        return Item.getById(mat.getId()) != null;
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
