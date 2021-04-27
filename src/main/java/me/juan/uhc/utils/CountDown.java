package me.juan.uhc.utils;

import lombok.Setter;

@Setter
public class CountDown {

    private long timeEnd;
    private long time;

    public CountDown(long time) {
        this.time = time * 1000;
    }

    public boolean hasExpired() {
        return timeEnd > System.currentTimeMillis();
    }

    private long getRemaining() {
        return this.timeEnd - System.currentTimeMillis();
    }

    public String getTimeLeft() {
        return this.getRemaining() >= 60_000 ? TimeUtil.millisToRoundedTime(this.getRemaining()) : TimeUtil.millisToSeconds(this.getRemaining());
    }

    public void start() {
        this.timeEnd = System.currentTimeMillis() + this.time;
    }
}
