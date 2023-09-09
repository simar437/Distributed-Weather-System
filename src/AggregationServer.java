import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class AggregationServer {
    static private LamportClock localClock = new LamportClock();
    static private HashMap<String, Weather> currentState = new HashMap<>();
    static Scanner sc;

    synchronized static void updateCurrentState(String id, Weather w) {
        currentState.put(id, w);
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
                Thread t = new Thread(new HandleClient(s, new HashMap<>(currentState)));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
