import java.lang.Math;

public class LamportClock {
    private int time = 0;

    synchronized void logCurrentEvent() {
        time++;
    }

    synchronized void updateTime(LamportClock other) {
        this.time = Math.max(this.time, other.time) + 1;
    }

    @Override
    public String toString() {
        return String.valueOf(time);
    }
}
