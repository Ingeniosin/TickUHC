package me.juan.uhc.manager.game;

import lombok.Getter;
import lombok.Setter;
import me.juan.uhc.configuration.game.GameConfiguration;

import static me.juan.uhc.configuration.worlds.WorldsConfiguration.NETHER;
import static me.juan.uhc.configuration.worlds.WorldsConfiguration.NORMAL;

@Setter
@Getter
public class PremadeGame {

    private String profileName;

    //GAME VALUES
    private IntConfig appleRate;
    private IntConfig slots;

    private IntConfig teamSize;

    //TIMERS
    private IntConfig pvpTime;
    private IntConfig healTime;
    private IntConfig invincibilityTime;
    private IntConfig startingTime;
    private IntConfig chatTime;

    //BORDER OPTIONS
    private IntConfig borderTime;
    private IntConfig borderEvery;
    private IntConfig borderSize;

    //DEATH MATCH
    private BolConfig deathMatch;
    private IntConfig deathMatchTime;

    //BOOLEAN VALUES
    private BolConfig bedBombs;
    private BolConfig shears;
    private BolConfig deathKick;

    //NETHER
    private BolConfig nether;   //HAY QUE ACTUALIZARLO
    private IntConfig netherSize;
    private IntConfig netherClose;


    public PremadeGame() {
        ValueConfiguration.DisabledValue pvpCause = () -> GameManager.getGameManager().isPlaying(), netherDisabledCause = () -> pvpCause.disabled() || !nether.getValue();
        int netherMaxSize = NETHER.get().getMaxSize(), normalMaxSize = NORMAL.get().getMaxSize(), lastBorderShrink = GameConfiguration.getLastBorderShrink();
        this.profileName = "Default";
        this.appleRate = new IntConfig(5, 0, 100, 1, 5);
        this.slots = new IntConfig(200, 1, 500, 10, 50);
        this.teamSize = new IntConfig(1, 1, 10, 1, -1);
        this.pvpTime = new IntConfig(20, 1, -1, 1, 5);
        this.healTime = new IntConfig(20, 1, -1, 1, 5);
        this.invincibilityTime = new IntConfig(30, 1, -1, 1, 5);
        this.startingTime = new IntConfig(10, 1, -1, 1, 5);
        this.chatTime = new IntConfig(10, 1, -1, 1, 5);
        this.borderTime = new IntConfig(35, 1, -1, 1, 5);
        this.borderEvery = new IntConfig(5, 1, -1, 1, 5);
        this.borderSize = new IntConfig(normalMaxSize, lastBorderShrink, normalMaxSize, 100, 500).setDisabledModify(pvpCause);
        this.deathMatch = new BolConfig(true);
        this.deathMatchTime = new IntConfig(5, 1, -1, 1, 5);
        this.bedBombs = new BolConfig(true);
        this.shears = new BolConfig(true);
        this.deathKick = new BolConfig(true);
        this.nether = new BolConfig(NETHER.get().isEnabled()).setDisabledModify(pvpCause);
        this.netherSize = new IntConfig(netherMaxSize, lastBorderShrink, netherMaxSize, 100, 500).setDisabledModify(netherDisabledCause);
        this.netherClose = new IntConfig(500, lastBorderShrink, normalMaxSize, 100, 500).setDisabledModify(netherDisabledCause);
    }

    public String getType() {
        return teamSize.getValue() > 1 ? "To" + teamSize : "FFA";
    }

    @Getter
    public abstract static class ValueConfiguration<E> {

        private DisabledValue disabledModify;
        private E value;

        public ValueConfiguration(E value) {
            disabledModify = () -> false;
            this.value = value;
        }

        public E setValue(E value) {
            if (!disabledModify.disabled()) this.value = value;
            return value;
        }

        public <T> T setDisabledModify(DisabledValue disabledModify) {
            this.disabledModify = disabledModify;
            return (T) this;
        }

        public interface DisabledValue {
            boolean disabled();
        }

    }

    @Getter
    public static class IntConfig extends ValueConfiguration<Integer> {

        private final int limitNegative, limitPositive, increment, increment2;

        public IntConfig(Integer value, int limitNegative, int limitPositive, int increment, int increment2) {
            super(value);
            this.limitNegative = limitNegative;
            this.limitPositive = limitPositive;
            this.increment = increment;
            this.increment2 = increment2;
        }
    }

    public static class BolConfig extends ValueConfiguration<Boolean> {
        public BolConfig(Boolean value) {
            super(value);
        }
    }

}
