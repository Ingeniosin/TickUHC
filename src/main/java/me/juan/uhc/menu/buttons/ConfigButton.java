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

    @Getter
    private static TreeMap<Integer, Button> configsButtons;
    @Getter
    private static ArrayList<Button> netherConfigsButtons, autoScatterConfigsButtons;
    @Getter
    private final ConfigurationValue configurationValue;
    private final String name;


    public static void loadButtons() {
        PremadeGame premadeGame = GameManager.getGameManager().getGame().getPremadeGame();
        configsButtons = new TreeMap<>();
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
                new ConfigButton(Material.BEDROCK, "Border Management", () -> null),
                new ConfigButton(Material.GOLD_CHESTPLATE, "Team Management", () -> null),
                new ConfigButton(Material.EYE_OF_ENDER, "Auto Scatter Management", () -> null),
                new ConfigButton(Material.BOW, "GameModes", () -> null)
        ));
        int element = 0;
        for (int i = 0; i < 27; i++) {
            while (i % 9 == 6  || i % 9 == 7 || i % 9 == 8) i++;
            if (element < configsArray.size()) configsButtons.put(i, configsArray.get(element++));
        }
        netherConfigsButtons = new ArrayList<>(Arrays.asList(new ConfigButton(Material.NETHERRACK, "Nether Status", premadeGame::getNether), new ConfigButton(Material.NETHER_BRICK, "Nether Size", premadeGame::getNetherSize), new ConfigButton(Material.NETHER_BRICK_ITEM, "Nether Close", premadeGame::getNetherClose)));
        autoScatterConfigsButtons = new ArrayList<>(Arrays.asList(new ConfigButton(Material.EYE_OF_ENDER, "Auto Scatter Status", premadeGame::getAutoScatter), new ConfigButton(Material.WATCH, "Auto Scatter Time", premadeGame::getAutoScatterTime)));
    }

    private ConfigButton(Material material, String name, ConfigurationValue configurationValue) {
        super(new ItemCreator(material).setName("§c" + name).get());
        this.name = name;
        this.configurationValue = configurationValue;
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
                case "Auto Scatter Management":
                    new ConfigurationMenu.SpecificModifierMenu(player, getAutoScatterConfigsButtons(), this);
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
