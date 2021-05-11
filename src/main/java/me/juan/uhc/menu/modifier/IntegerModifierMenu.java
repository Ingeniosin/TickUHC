package me.juan.uhc.menu.modifier;

import me.juan.uhc.manager.game.premade.PremadeGame;
import me.juan.uhc.menu.buttons.ConfigButton;
import me.juan.uhc.utils.ItemCreator;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.buttons.Button;
import me.juan.uhc.utils.jmenu.buttons.GlassButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.TreeMap;

public class IntegerModifierMenu extends ModifierMenu {

    private final static int[] positions = new int[]{10, 11, 15, 16};
    private final String valueName;
    private final PremadeGame.IntConfig gameConfiguration;
    private final Menu menu;
    private final Button centerButton;
    private ValueModifier[] buttons;

    public IntegerModifierMenu(Player player, ConfigButton button) {
        final ItemStack itemStack = button.getItemStack();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        this.valueName = itemMeta.getDisplayName();
        this.gameConfiguration = (PremadeGame.IntConfig) button.getConfigurationValue().get();
        final Button.ValueInterface lore = button.getLore();
        this.centerButton = new Button(itemStack, lore == null ? null : lore::value, null);
        this.inflateButtons();
        this.menu = new Menu(player, 9 * 3, true, "§eModify value: " + valueName, new TreeMap<>() {{
            put(13, IntegerModifierMenu.this.centerButton);
        }}, true) {

            @Override
            public void onClose() {
                unregister();
            }

        };
        this.onValueUpdate();
        this.menu.open();
    }


    @Override
    public void onValueUpdate() {
        if (gameConfiguration.getLimitNegative() != this.buttons[0].limit || gameConfiguration.getLimitPositive() != this.buttons[this.buttons.length - 1].limit)
            inflateButtons();
        TreeMap<Integer, Button> buttonHashMap = menu.getButtonHashMap();
        for (int i = 0; i < this.buttons.length; i++) {
            ValueModifier button = this.buttons[i];
            buttonHashMap.put(positions[i], !button.isInvalid() && !gameConfiguration.getDisabledModify().disabled() ? button : GlassButton.getRed());
        }
    }

    private void inflateButtons() {
        this.buttons = new ValueModifier[]{
                new ValueModifier(false, gameConfiguration.getIncrement2(), gameConfiguration.getLimitNegative()),
                new ValueModifier(false, gameConfiguration.getIncrement(), gameConfiguration.getLimitNegative()),
                new ValueModifier(true, gameConfiguration.getIncrement(), gameConfiguration.getLimitPositive()),
                new ValueModifier(true, gameConfiguration.getIncrement2(), gameConfiguration.getLimitPositive())
        };
    }

    public class ValueModifier extends Button {

        private final boolean isIncrement;
        private final int incrementer, limit;

        public ValueModifier(boolean isIncrement, int incrementer, int limit) {
            super(new ItemCreator(Material.INK_SACK, 0, isIncrement ? 10 : 13).get(), centerButton.getLore(), () -> (isIncrement ? "§a+" : "§c-") + incrementer);
            this.isIncrement = isIncrement;
            this.incrementer = incrementer;
            this.limit = limit;
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
