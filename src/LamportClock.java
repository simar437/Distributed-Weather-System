import java.lang.Math;

public class LamportClock {
    private int time = 0;

    synchronized int logCurrentEvent() {
        return ++time;
    }

    synchronized int updateTime(int t) {
        this.time = Math.max(this.time, t) + 1;
        return time;
    }

    @Override
    public String toString() {
        return String.valueOf(time);
    }
}
