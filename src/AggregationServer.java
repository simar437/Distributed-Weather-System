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

    synchronized static HashMap<String, Deque<Weather>> getCurrentState() {
        return new HashMap<>(currentState);
    }

    synchronized static void removePrevState(String id) {
        currentState.get(id).remove();
    }

    synchronized static void logEvent() {
        localClock.logCurrentEvent();
    }

    synchronized static void updateClock(int t) {
        localClock.updateTime(t);
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
                new RequestHandler(s).handle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
