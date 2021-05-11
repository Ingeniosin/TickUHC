package me.juan.uhc.menu.modifier;

import java.util.ArrayList;

public abstract class ModifierMenu {
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
