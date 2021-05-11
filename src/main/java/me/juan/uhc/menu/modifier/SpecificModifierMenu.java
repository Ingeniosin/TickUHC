package me.juan.uhc.menu.modifier;

import me.juan.uhc.menu.buttons.ConfigButton;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.buttons.Button;
import me.juan.uhc.utils.jmenu.buttons.GlassButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.TreeMap;

public class SpecificModifierMenu extends ModifierMenu {

    public final Menu menu;
    private final ArrayList<Integer> positions = new ArrayList<>();
    private final ArrayList<Button> configButtons;

    public SpecificModifierMenu(Player player, ArrayList<Button> configButtons, Button button) {
        this.configButtons = configButtons;
        this.menu = new Menu(player, 9, true, button.getItemStack().getItemMeta().getDisplayName(), configButtons, true) {
            @Override
            public void onClose() {
                unregister();
            }

            @Override
            public void click(Button button, int index) {
                if (configButtons.contains(button)) updateAll();
            }
        };
        for (int i = 0; i < this.menu.getButtonHashMap().size(); i++)
            if (this.menu.getButtonHashMap().get(i) instanceof ConfigButton) positions.add(i);
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
