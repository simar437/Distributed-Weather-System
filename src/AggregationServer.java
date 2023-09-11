import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class AggregationServer {
    static private LamportClock localClock = new LamportClock();
    static private HashMap<String, Deque<Weather>> currentState = new HashMap<>();
    static Scanner sc;

    synchronized static void updateCurrentState(String id, Weather w) {
        if (!currentState.containsKey(id)) {
            currentState.put(id, new ArrayDeque<>());
        }
        currentState.get(id).add(w);
    }

    synchronized static void removePrevState(String id) {
        currentState.get(id).remove();
    }

    synchronized static void logEvent() {
        localClock.logCurrentEvent();
    }

    synchronized static void updateClock(LamportClock l) {
        localClock.updateTime(l);
    }

    synchronized static String getTime() {
        return String.valueOf(localClock);
    }
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(4567);
            System.out.println("Server running...");
            while (true) {
                Socket s = ss.accept();
                Thread t = new Thread(new HandleGET(s, new HashMap<>(currentState)));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
