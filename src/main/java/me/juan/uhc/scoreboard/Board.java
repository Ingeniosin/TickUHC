package me.juan.uhc.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.IntStream;

@Getter
public abstract class Board {

    @Getter
    private static final HashMap<UUID, Board> boards = new HashMap<>();

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective sidebar;

    public Board(Player player) {
        this.player = player;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        Objective name = scoreboard.registerNewObjective("name", "health");
        name.setDisplaySlot(DisplaySlot.BELOW_NAME);
        name.setDisplayName("§4❤");
        Objective tab = scoreboard.registerNewObjective("tab", "health");
        tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        IntStream.range(1, 16).forEach(value -> scoreboard.registerNewTeam("SLOT_" + value).addEntry(genEntry(value)));
        player.setScoreboard(scoreboard);
        boards.put(player.getUniqueId(), this);
    }

    public void setTitle(String title) {
        if (title.length() > 32) title = title.substring(0, 32);
        if (!sidebar.getDisplayName().equals(title)) sidebar.setDisplayName(title);
    }

    private void setSlot(int slot, String text) {
        if (slot > 15) return;

        Team team = scoreboard.getTeam("SLOT_" + slot);
        String entry = genEntry(slot);

        if (!scoreboard.getEntries().contains(entry)) sidebar.getScore(entry).setScore(slot);

        String prefix = getFirstSplit(text);

        int lastIndex = prefix.lastIndexOf(167);
        String lastColor = lastIndex >= 14 ? prefix.substring(lastIndex) : ChatColor.getLastColors(prefix);

        if (lastIndex >= 14) prefix = prefix.substring(0, lastIndex);

        String suffix = getFirstSplit(lastColor + getSecondSplit(text));

        if (!team.getPrefix().equals(prefix)) team.setPrefix(prefix);
        if (!team.getSuffix().equals(suffix)) team.setSuffix(suffix);
    }

    private void removeSlot(int slot) {
        String entry = genEntry(slot);
        if (scoreboard.getEntries().contains(entry)) scoreboard.resetScores(entry);
    }

    public void setSlotsFromList(ArrayList<String> list) {
        int slot = list.size();
        if (slot < 15) IntStream.range(slot + 1, 16).forEach(this::removeSlot);
        IntStream.range(1, slot + 1).forEach(value -> setSlot(value, list.get(value - 1)));
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32) s = s.substring(0, 32);
        return s.length() > 16 ? s.substring(16) : "";
    }

    public abstract void update();
}
