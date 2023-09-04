import java.lang.Math;

public class LamportClock {
    private int time = 0;

    void logCurrentEvent() {
        time++;
    }

    void updateTime(LamportClock other) {
        this.time = Math.max(this.time, other.time) + 1;
    }
}
