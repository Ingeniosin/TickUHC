package me.juan.uhc.manager.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    private final ArrayList<String> whitelistNames = new ArrayList<>();
    private final ArrayList<String> respawnedButNotJoined = new ArrayList<>();
    private final List<Location> scatterLocations = new ArrayList<>();
    @Setter
    private boolean whitelist = true;
    @Setter
    private PremadeGame premadeGame;

    protected Game(PremadeGame premadeGame) {
        this.premadeGame = premadeGame;
    }


}
