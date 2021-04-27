package me.juan.uhc.menu;

import me.juan.uhc.manager.game.PremadeGame;
import me.juan.uhc.manager.gamemode.GameMode;
import me.juan.uhc.menu.buttons.ConfigButton;
import me.juan.uhc.utils.ItemCreator;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.PaginatedMenu;
import me.juan.uhc.utils.jmenu.buttons.Button;
import me.juan.uhc.utils.jmenu.buttons.GlassButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ConfigurationMenu {

    public static void configurationMenu(Player player) {
        new Menu(player, 9 * 3, true, "§eConfiguration Menu", ConfigButton.getConfigsButtons(9 * 3), true).open();
    }

    public static void scenarioManageMenu(Player player) {
        PaginatedMenu.create(player, 9 * 4, true, "§6GameModes: ", GameMode.getGameModeButtons()).open();
    }

    public abstract static class ModifierMenu {
        private static final ArrayList<ModifierMenu> modifierMenus = new ArrayList<>();

        public ModifierMenu() {
            modifierMenus.add(this);
        }

        public void unregister() {
            modifierMenus.remove(this);
        }

        public void updateAll() {
            modifierMenus.parallelStream().forEach(ModifierMenu::onValueUpdate);
        }

        public abstract void onValueUpdate();

    }

    public static class IntegerModifierMenu extends ModifierMenu{

        private final static int[] positions = new int[]{10, 11, 15, 16};
        private final String valueName;
        private final PremadeGame.IntConfig gameConfiguration;
        private final Menu menu;
        private final ValueModifier[] buttons;
        private final Button centerButton;

        public IntegerModifierMenu(Player player, ConfigButton button) {
            final ItemStack itemStack = button.getItemStack();
            final ItemMeta itemMeta = itemStack.getItemMeta();
            this.valueName = itemMeta.getDisplayName();
            this.gameConfiguration = (PremadeGame.IntConfig) button.getConfigurationValue().get();
            this.centerButton = new Button(itemStack) {
                @Override
                public List<String> cacheLore() {
                    return button.cacheLore();
                }
            };
            this.buttons = new ValueModifier[]{
                    new ValueModifier(false, gameConfiguration.getIncrement2(), gameConfiguration.getLimitNegative()),
                    new ValueModifier(false, gameConfiguration.getIncrement(), gameConfiguration.getLimitNegative()),
                    new ValueModifier(true, gameConfiguration.getIncrement(), gameConfiguration.getLimitPositive()),
                    new ValueModifier(true, gameConfiguration.getIncrement2(), gameConfiguration.getLimitPositive())
            };
            this.menu = new Menu(player, 9 * 3, true, "§eModify value: " + valueName, new TreeMap<Integer, Button>() {{ put(13, IntegerModifierMenu.this.centerButton);}}, true) {

                @Override
                public void onClose() { unregister(); }

            };
            this.onValueUpdate();
            this.menu.open();
        }


        @Override
        public void onValueUpdate() {
            TreeMap<Integer, Button> buttonHashMap = menu.getButtonHashMap();
            for (int i = 0; i < this.buttons.length; i++) {
                ValueModifier button = this.buttons[i];
                buttonHashMap.put(positions[i], !button.isInvalid() && !gameConfiguration.getDisabledModify().disabled() ? button : GlassButton.getRed());
            }
        }

        public class ValueModifier extends Button {

            private final boolean isIncrement;
            private final int incrementer;
            private final int limit;

            public ValueModifier(boolean isIncrement, int incrementer, int limit) {
                super(new ItemCreator(Material.INK_SACK, 0, isIncrement ? 10 : 13).setName((isIncrement ? "§a+" : "§c-") + incrementer).get());
                this.isIncrement = isIncrement;
                this.incrementer = incrementer;
                this.limit = limit;
            }

            @Override
            public List<String> cacheLore() {
                return centerButton.cacheLore();
            }

            public boolean isInvalid() {
                return isInvalid(gameConfiguration.getValue() + (isIncrement ? incrementer : -incrementer));
            }

            public boolean isInvalid(int newValue) {
                return limit != -1 && isIncrement ? newValue > limit : newValue < limit;
            }

            @Override
            public void onClick(Player player, Menu menu) {
                int value = gameConfiguration.getValue() + (isIncrement ? incrementer : -incrementer);
                if (isInvalid(value)) return;
                player.sendMessage("§eThe value §f'" + valueName + "§f' §ewas modified, now it is: §6" + gameConfiguration.setValue(value));
                updateAll();
            }
        }
    }

    public static class SpecificModifierMenu extends ModifierMenu{

        private final ArrayList<Integer> positions = new ArrayList<>();
        public final Menu menu;
        private final ArrayList<Button> configButtons;

        public SpecificModifierMenu(Player player, ArrayList<Button> configButtons, Button button) {
            this.configButtons = configButtons;
            this.menu = new Menu(player, 9, true, button.getItemStack().getItemMeta().getDisplayName(), configButtons, true) {
                @Override
                public void onLoad() {
                    for (int i = 0; i < getButtonHashMap().size(); i++) if (getButtonHashMap().get(i) instanceof ConfigButton) positions.add(i);
                }

                @Override
                public void onClose() {
                    unregister();
                }

                @Override
                public void click(Button button, int index) {
                    if (configButtons.contains(button)) updateAll();
                }
            };
            this.onValueUpdate();
            this.menu.open();
        }

        @Override
        public void onValueUpdate() {
            TreeMap<Integer, Button> buttonHashMap = menu.getButtonHashMap();
            for (int i = 0; i < positions.size(); i++) {
                ConfigButton button = (ConfigButton) configButtons.get(i);
                int index = positions.get(i);
                buttonHashMap.put(index, !button.getConfigurationValue().get().getDisabledModify().disabled() ? button : GlassButton.getRed());
            }
        }
    }

}
