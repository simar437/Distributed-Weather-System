public class LamportClock {
    private int time = 0;

    void logCurrentEvent() {
        time++;
    }

    int getTime() {
        return time;
    }
}
