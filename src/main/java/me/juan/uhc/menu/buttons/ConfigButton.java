package me.juan.uhc.menu.buttons;

import lombok.Getter;
import me.juan.uhc.manager.game.GameManager;
import me.juan.uhc.manager.game.PremadeGame;
import me.juan.uhc.menu.ConfigurationMenu;
import me.juan.uhc.utils.ItemCreator;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

import static me.juan.uhc.configuration.permissions.PermissionsConfiguration.ADMIN;
import static me.juan.uhc.configuration.permissions.PermissionsConfiguration.HOST;
import static me.juan.uhc.configuration.worlds.WorldsConfiguration.NETHER;

public class ConfigButton extends Button {

    private static TreeMap<Integer, Button> configsButtons;
    private static ArrayList<Button> netherConfigsButtons;
    @Getter
    private final ConfigurationValue configurationValue;
    private final String name;

    private ConfigButton(Material material, String name, ConfigurationValue configurationValue) {
        super(new ItemCreator(material).setName("§c" + name).get());
        this.name = name;
        this.configurationValue = configurationValue;
    }

    public static TreeMap<Integer, Button> getConfigsButtons(int size) {
        if (configsButtons == null) {
            PremadeGame premadeGame = GameManager.getGameManager().getGame().getPremadeGame();
            ArrayList<Button> configsArray = new ArrayList<>(Arrays.asList(
                    new ConfigButton(Material.APPLE, "Apple Rate", premadeGame::getAppleRate),
                    new ConfigButton(Material.SKULL_ITEM, "Max Slots", premadeGame::getSlots),
                    new ConfigButton(Material.DIAMOND_SWORD, "PvP Time", premadeGame::getPvpTime),
                    new ConfigButton(Material.GOLDEN_APPLE, "Heal Time", premadeGame::getHealTime),
                    new ConfigButton(Material.ARROW, "Invincibility Time", premadeGame::getInvincibilityTime),
                    new ConfigButton(Material.REDSTONE, "Starting Time", premadeGame::getStartingTime),
                    new ConfigButton(Material.WATCH, "Chat Time", premadeGame::getChatTime),
                    new ConfigButton(Material.FLINT_AND_STEEL, "Death Kick", premadeGame::getDeathKick),
                    new ConfigButton(Material.SHEARS, "Shears", premadeGame::getShears),
                    new ConfigButton(Material.BED, "Bed Bombs", premadeGame::getBedBombs),
                    new ConfigButton(Material.FLINT, "Death Match Management", () -> null),
                    new ConfigButton(Material.NETHERRACK, "Nether Management", () -> null),
                    //   WorldsConfiguration.NETHER.get().isEnabled() ? new ConfigButton(Material.NETHERRACK, "Nether Management", () -> null) : GlassButton.getRed(),
                    new ConfigButton(Material.BEDROCK, "Border Management", () -> null),
                    new ConfigButton(Material.GOLD_CHESTPLATE, "Team Management", () -> null),
                    new ConfigButton(Material.BOW, "GameModes", () -> null)
            ));
            configsButtons = new TreeMap<>();
            int element = 0;
            for (int i = 0; i < size; i++) {
                while (i % 9 == 6  || i % 9 == 7 || i % 9 == 8) i++;
                Button button = configsArray.get(element++);
                configsButtons.put(i, button);
            }
        }
        return configsButtons;
    }

    public static ArrayList<Button> getNetherConfigsButtons() {
        if (netherConfigsButtons == null) {
            PremadeGame premadeGame = GameManager.getGameManager().getGame().getPremadeGame();
            netherConfigsButtons = new ArrayList<>(Arrays.asList(
                    new ConfigButton(Material.NETHERRACK, "Nether Status", premadeGame::getNether),
                    new ConfigButton(Material.NETHER_BRICK, "Nether Size", premadeGame::getNetherSize),
                    new ConfigButton(Material.NETHER_BRICK_ITEM, "Nether Close", premadeGame::getNetherClose)
            ));
        }
        return netherConfigsButtons;
    }

    private String lines() {
        return "§7§m---------------------";
    }

    @Override
    public List<String> cacheLore() {
        return configurationValue.get() == null ? null : new ArrayList<>(Arrays.asList(lines(), "§eCurrent value: §6" + configurationValue.get().getValue().toString(), lines()));
    }

    @Override
    public void onClick(Player player, Menu menu) {
        PremadeGame.ValueConfiguration valueConfiguration = configurationValue.get();
        if (valueConfiguration == null) {
            switch (name) {
                case "GameModes":
                    ConfigurationMenu.scenarioManageMenu(player);
                    return;
                case "Nether Management":
                    if (!NETHER.get().isEnabled()) {
                        player.sendMessage("§cThe nether is disabled in config.yml");
                        return;
                    }
                    new ConfigurationMenu.SpecificModifierMenu(player, getNetherConfigsButtons(), this);
                    return;
                case "Death Match Management":

                    return;
            }
            return;
        }
        boolean heCan = HOST.contains(player) || ADMIN.contains(player); //HAY QUE CAMBIARLO POR isHost?
        if (!heCan) return;
        if (valueConfiguration instanceof PremadeGame.IntConfig)
            new ConfigurationMenu.IntegerModifierMenu(player, this);
        else {
            PremadeGame.BolConfig gameBoolean = (PremadeGame.BolConfig) valueConfiguration;
            player.sendMessage("§eThe value §f'§c" + name + "§f' §ewas modified, now it is: §6" + gameBoolean.setValue(!gameBoolean.getValue()));
        }
    }


    public interface ConfigurationValue {

        PremadeGame.ValueConfiguration get();

    }
}
