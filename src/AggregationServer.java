import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AggregationServer {
    static private LamportClock localClock = new LamportClock();
    static Scanner sc;

    static private final HashMap<String, PriorityQueue<Weather>> currentState = new HashMap<>();

    synchronized static void updateCurrentState(String id, Weather w) throws IOException {
        if (!currentState.containsKey(id)) {
            currentState.put(id, new PriorityQueue<>());
        }
        currentState.get(id).add(w);
        Backup.initiateBackup(getCurrentState());
    }

    synchronized static HashMap<String, PriorityQueue<Weather>> getCurrentState() {
        HashMap<String, PriorityQueue<Weather>> copy = new HashMap<>();
        for (Map.Entry<String, PriorityQueue<Weather>> entry : currentState.entrySet()) {
            copy.put(entry.getKey(), new PriorityQueue<>(entry.getValue()));
        }
        return copy;
    }
    synchronized static void removeContentStation(String id) {
        for (Map.Entry<String, PriorityQueue<Weather>> i : currentState.entrySet()) {
            PriorityQueue<Weather> weatherQueue = i.getValue();
            weatherQueue.removeIf(w -> Objects.equals(w.contentServerID, id));
        }
    }


    static HashMap<String, LocalDateTime> receivedTime = new HashMap<>();

    public synchronized static LocalDateTime getTime(String CS_ID) {
        return receivedTime.get(CS_ID);
    }

    public synchronized static boolean setReceivedTime(String CS_ID, LocalDateTime time) {
        boolean containsKey = receivedTime.containsKey(CS_ID);
        receivedTime.put(CS_ID, time);
        return containsKey;
    }

    synchronized static int logEvent() {
        return localClock.logCurrentEvent();
    }

    synchronized static void updateClockUsingHTTP(String message) {
        localClock.updateUsingHTTPMessage(message);
    }

    synchronized static String getTime() {
        return String.valueOf(localClock);
    }
    public static void main(String[] args) {
        try {
            // Backup.restore();
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
