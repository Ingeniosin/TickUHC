package me.juan.uhc.utils.jmenu;

import lombok.Getter;
import me.juan.uhc.utils.jmenu.buttons.Button;
import me.juan.uhc.utils.jmenu.buttons.GlassButton;
import me.juan.uhc.utils.jmenu.buttons.PaginationButtons;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class PaginatedMenu extends Menu {

    private final ArrayList<TreeMap<Integer, Button>> pagesButtons;
    @Getter
    private final int maxPages;
    @Getter
    private final PaginationButtons nextBtn, backBtn;
    private final String title;
    @Getter
    private int currentPage;

    private PaginatedMenu(Player player, int size, boolean autoUpdate, String title, ArrayList<TreeMap<Integer, Button>> buttonHashMap, PaginationButtons nextBtn, PaginationButtons backBtn) {
        super(player, size, autoUpdate, title);
        this.title = title;
        this.pagesButtons = buttonHashMap;
        this.maxPages = pagesButtons.size();
        this.nextBtn = nextBtn.setPaginatedMenu(this);
        this.backBtn = backBtn.setPaginatedMenu(this);
        this.setPage(1);
    }

    public static PaginatedMenu create(Player player, int size, boolean autoUpdate, String title, ArrayList<Button> items) {
        items = new ArrayList<>(items);
        ArrayList<TreeMap<Integer, Button>> PagesAndButtons = new ArrayList<>();
        PaginationButtons nextBtn = new PaginationButtons(true), backBtn = new PaginationButtons(false);
        while (!items.isEmpty()) {
            TreeMap<Integer, Button> page = new TreeMap<>();
            int index = 0;
            for (Button button : new ArrayList<>(items)) {
                while (style(nextBtn, backBtn, page, index, size)) index++;
                if (index >= size) break;
                page.put(index++, button);
                items.remove(button);
            }
            if (page.size() != size)
                IntStream.range(page.size() - 1, size).forEach(value -> style(nextBtn, backBtn, page, value, size));
            PagesAndButtons.add(page);
        }
        return new PaginatedMenu(player, size, autoUpdate, title, PagesAndButtons, nextBtn, backBtn);
    }

    private static boolean style(PaginationButtons nextBtn, PaginationButtons backBtn, TreeMap<Integer, Button> page, int index, int maxSize) {
        if (index >= maxSize) return false;
        if (index % 9 == 0 || index % 9 == 8 || index >= 0 && index <= 8) {
            if (index == 0 || index == 8) page.put(index, index == 8 ? nextBtn : backBtn);
            else page.put(index, GlassButton.get());
            return true;
        }
        return false;
    }

    private String getPaginatedTitle() {
        return title + " §e" + currentPage + "§6/§e" + maxPages;
    }

    @Override
    public void onOpen() {
        super.setTitle(getPaginatedTitle());
    }

    private TreeMap<Integer, Button> getPage(int page) {
        if (page < 1 || page > pagesButtons.size()) return null;
        return pagesButtons.get(page - 1);
    }

    public void setPage(int pageN) {
        TreeMap<Integer, Button> page = getPage(pageN);
        if (page == null) return;
        this.currentPage = pageN;
        this.setButtonHashMap(page, false);
        this.nextBtn.update();
        this.backBtn.update();
        super.setTitle(getPaginatedTitle());
        if (!isAutoUpdate()) super.update();
    }
}
