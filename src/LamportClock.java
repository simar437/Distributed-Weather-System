import java.lang.Math;

public class LamportClock {

    /**
     * @param time The time to initialize the Lamport Clock using a String
     */
    LamportClock(String time) {
        this.time = Integer.parseInt(time);
    }

    /**
     * Initializes the Lamport Clock to 0
     */
    LamportClock() {}
    private int time = 0;

    /**
     * @return The current time of the Lamport Clock
     */
    synchronized int logCurrentEvent() {
        return ++time;
    }

    /**
     * Updates the time of the Lamport Clock to the maximum of the current time, and the time received plus 1
     *
     * @return The current time of the Lamport Clock
     */
    synchronized int updateTime(int t) {
        this.time = Math.max(this.time, t) + 1;
        return time;
    }

    /**
     * Updates the time of the Lamport Clock using the HTTP message
     *
     * @param message The HTTP message
     * @return The current time of the Lamport Clock
     */
    synchronized int updateUsingHTTPMessage(String message) {
        int t = 0;

        // Parse the HTTP message to get the Lamport Clock time
        for (String s : message.split("\n")) {
            if (s.startsWith("Lamport-Clock: ")) {
                t = Integer.parseInt(s.split(" ")[1]);
            }
        }
        return updateTime(t);
    }

    /**
     * @return The current time of the Lamport Clock as a String
     */
    @Override
    public String toString() {
        return String.valueOf(time);
    }
}
