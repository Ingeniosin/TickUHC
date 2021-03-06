package me.juan.uhc.manager.game.premade;

import lombok.Getter;
import lombok.Setter;
import me.juan.uhc.configuration.game.GameConfiguration;
import me.juan.uhc.manager.GameManager;

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


    //WHITELIST
    private BolConfig autoScatter;
    private IntConfig autoScatterTime;

    public PremadeGame() {
        ValueConfiguration.DisabledValue pvpCause = () -> GameManager.getGameManager().isPlaying(), netherDisabledCause = () -> pvpCause.disabled() || !nether.getValue();
        int netherMaxSize = NETHER.get().getMaxSize(), normalMaxSize = NORMAL.get().getMaxSize(), lastBorderShrink = GameConfiguration.getLastBorderShrink();
        this.profileName = "Default";
        this.appleRate = new IntConfig(5, 0, 100, 1, 5);
        this.slots = new IntConfig(200, 1, 500, 10, 50);
        this.teamSize = new IntConfig(1, 1, 10, 1, -1);
        this.pvpTime = new IntConfig(20, 1, -1, 1, 5).setChangedValue(() -> autoScatterTime.setLimitPositive(pvpTime.getValue()));
        this.healTime = new IntConfig(10, 1, -1, 1, 5);
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

        this.autoScatter = new BolConfig(true);
        this.autoScatterTime = new IntConfig(this.healTime.getValue(), 1, this.pvpTime.getValue(), 1, 5).setDisabledModify(() -> !autoScatter.getValue());
    }

    public boolean isTeams() {
        return teamSize.getValue() > 1;
    }

    public String getType() {
        return isTeams() ? "To" + teamSize : "FFA";
    }

    @Getter
    public abstract static class ValueConfiguration<E> {

        private ChangedValue changedValue;
        private DisabledValue disabledModify;
        private E value;

        public ValueConfiguration(E value) {
            disabledModify = () -> false;
            this.value = value;
        }

        public E setValue(E value) {
            if (!disabledModify.disabled()) this.setForceValue(value);
            if (changedValue != null) changedValue.onChange();
            return value;
        }

        public void setForceValue(E value) {
            this.value = value;
        }

        public <T> T setChangedValue(ChangedValue changedValue) {
            this.changedValue = changedValue;
            return (T) this;
        }

        public <T> T setDisabledModify(DisabledValue disabledModify) {
            this.disabledModify = disabledModify;
            return (T) this;
        }

        public interface DisabledValue {
            boolean disabled();
        }


        public interface ChangedValue {
            void onChange();
        }
    }

    @Getter
    public static class IntConfig extends ValueConfiguration<Integer> {

        private final int limitNegative, increment, increment2;
        private int limitPositive;

        public IntConfig(Integer value, int limitNegative, int limitPositive, int increment, int increment2) {
            super(value);
            this.limitNegative = limitNegative;
            this.limitPositive = limitPositive;
            this.increment = increment;
            this.increment2 = increment2;
        }

        public void setLimitPositive(int limitPositive) {
            this.limitPositive = limitPositive;
            if (getValue() > limitPositive) setValue(limitPositive);
        }
    }

    public static class BolConfig extends ValueConfiguration<Boolean> {
        public BolConfig(Boolean value) {
            super(value);
        }
    }

}
