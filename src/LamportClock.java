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

    synchronized int updateUsingHTTPMessage(String message) {
        int t = 0;
        for (String s : message.split("\n")) {
            if (s.startsWith("Lamport-Clock: ")) {
                t = Integer.parseInt(s.split(" ")[1]);
            }
        }
        return updateTime(t);
    }

    @Override
    public String toString() {
        return String.valueOf(time);
    }
}
