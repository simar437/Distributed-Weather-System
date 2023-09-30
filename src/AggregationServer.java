import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class AggregationServer {
    static HashMap<String, LocalDateTime> receivedTime = new HashMap<>();
    static Backup b = new Backup("AS");
    static private LamportClock localClock = new LamportClock();
    static private HashMap<String, PriorityQueue<Weather>> currentState = new HashMap<>();
    static int port = 4567;

    synchronized static void updateCurrentState(String id, Weather w) throws IOException {
        if (!currentState.containsKey(id)) {
            currentState.put(id, new PriorityQueue<>());
        }
        currentState.get(id).add(w);
        ASBackup.createBackup();
    }

    synchronized static HashMap<String, PriorityQueue<Weather>> getCurrentState() {
        HashMap<String, PriorityQueue<Weather>> copy = new HashMap<>();
        for (Map.Entry<String, PriorityQueue<Weather>> entry : currentState.entrySet()) {
            copy.put(entry.getKey(), new PriorityQueue<>(entry.getValue()));
        }
        return copy;
    }

    synchronized static void removeContentStation(String id) throws IOException {
        for (Map.Entry<String, PriorityQueue<Weather>> i : currentState.entrySet()) {
            PriorityQueue<Weather> weatherQueue = i.getValue();
            weatherQueue.removeIf(w -> Objects.equals(w.contentServerID, id));
        }
        ASBackup.createBackup();
    }

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

    synchronized static String getASTime() {
        return String.valueOf(localClock);
    }

    public static void main(String[] args) {
        try {
            ASBackup.restoreBackup();
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            }
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Server running...");
            while (true) {
                Socket s = ss.accept();
                new RequestHandler(s).handle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ASBackup {
        @JsonSerialize(using = ToStringSerializer.class)

        @JsonProperty("localClock")
        private LamportClock localClock;

        @JsonProperty("receivedTime")
        private HashMap<String, LocalDateTime> receivedTime;

        @JsonProperty("currentState")
        private HashMap<String, PriorityQueue<Weather>> currentState;

        @JsonProperty("port")
        private int port;

        static synchronized void createBackup() throws IOException {
            ASBackup data = new ASBackup();
            data.localClock = AggregationServer.localClock;
            data.receivedTime = AggregationServer.receivedTime;
            data.currentState = AggregationServer.currentState;
            data.port = AggregationServer.port;
            b.initiateBackup(data);
        }

        static synchronized void restoreBackup() throws IOException {
            ASBackup data = (ASBackup) b.restore(new ASBackup());
            if (data == null) {
                return;
            }
            AggregationServer.currentState = data.currentState;
            AggregationServer.localClock = data.localClock;
            AggregationServer.receivedTime = data.receivedTime;
            AggregationServer.port = data.port;
            for (Map.Entry<String, LocalDateTime> entry : AggregationServer.receivedTime.entrySet()) {
                Thread t = new Thread(new HandlePUT(entry.getKey()));
                t.start();
            }
        }
    }
}
