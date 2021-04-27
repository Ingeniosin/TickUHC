package me.juan.uhc.utils.jmenu.buttons;

import me.juan.uhc.utils.ItemCreator;
import me.juan.uhc.utils.jmenu.Menu;
import me.juan.uhc.utils.jmenu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PaginationButtons extends Button {

    private final boolean isNext;
    private PaginatedMenu paginatedMenu;

    public PaginationButtons(boolean isNext) {
        super(new ItemCreator(Material.CARPET, 0, isNext ? 13 : 14).setName(isNext ? "§aNext Page" : "§cPrevious page").get());
        this.isNext = isNext;
    }

    public PaginationButtons setPaginatedMenu(PaginatedMenu paginatedMenu) {
        this.paginatedMenu = paginatedMenu;
        return this;
    }

    @Override
    public void onClick(Player player, Menu menu) {
        paginatedMenu.setPage(paginatedMenu.getCurrentPage() + (isNext ? 1 : -1));
    }

    private short getDamage() {
        return (short) (isNext ? 13 : 14);
    }

    public void update() {
        item.setDurability(hasPages() ? 7 : getDamage());
    }

    private boolean hasPages() {
        return isNext ? paginatedMenu.getCurrentPage() >= paginatedMenu.getMaxPages() : paginatedMenu.getCurrentPage() <= 1;
    }

}
