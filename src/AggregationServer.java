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
    // The current time at which the last content was received from the content station
    static HashMap<String, LocalDateTime> receivedTime = new HashMap<>();
    static Backup b = new Backup("AS");
    static int noOfCS = 0;
    static int port = 4567;
    static private LamportClock localClock = new LamportClock();
    // HashMap of content station id to weather data with the highest priority by Lamport timestamp of the content station
    static private HashMap<String, PriorityQueue<Weather>> currentState = new HashMap<>();
    // HashMap of content station id to process id
    static private HashMap<String, Integer> pid = new HashMap<>();

    /**
     * Gives a new PID to a content station
     * Creates backup after this
     *
     * @param id The id of the content station
     * @throws IOException
     */

    static synchronized void addContentStation(String id) throws IOException {
        pid.put(id, noOfCS++);
        ASBackup.createBackup();
    }

    /**
     * Gets the PID of a content station
     *
     * @param id The id of the content station
     * @return The PID of the content station
     */
    static synchronized int getPID(String id) {
        return pid.get(id);
    }

    /**
     * Updates the current state of the Aggregation Server
     * Creates a new priority queue if the content station is new
     * Adds the weather data to the priority queue
     * Creates backup after this
     *
     * @param id The id of the content station
     * @param w  The weather data to be added
     * @throws IOException
     */
    synchronized static void updateCurrentState(String id, Weather w) throws IOException {
        if (!currentState.containsKey(id)) {
            currentState.put(id, new PriorityQueue<>());
        }
        currentState.get(id).add(w);
        ASBackup.createBackup();
    }

    /**
     * Gets the deep copy of current state (Data) of the Aggregation Server
     *
     * @return The current state of the Aggregation Server
     */
    synchronized static HashMap<String, PriorityQueue<Weather>> getCurrentState() {
        HashMap<String, PriorityQueue<Weather>> copy = new HashMap<>();
        for (Map.Entry<String, PriorityQueue<Weather>> entry : currentState.entrySet()) {
            copy.put(entry.getKey(), new PriorityQueue<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * Removes the content station from the current state of the Aggregation Server
     * Creates backup after this
     *
     * @param id The id of the content station
     * @throws IOException
     */
    synchronized static void removeContentStation(String id) throws IOException {
        for (Map.Entry<String, PriorityQueue<Weather>> i : currentState.entrySet()) {
            PriorityQueue<Weather> weatherQueue = i.getValue();
            weatherQueue.removeIf(w -> Objects.equals(w.contentServerID, id));
        }
        ASBackup.createBackup();
    }

    /**
     * Gets the received time of a content station
     *
     * @param CS_ID The id of the content station
     * @return The received time of the content station
     */

    public synchronized static LocalDateTime getTime(String CS_ID) {
        return receivedTime.get(CS_ID);
    }

    /**
     * Sets the received time of a content station
     *
     * @param CS_ID The id of the content station
     * @param time  The received time of the content from the station
     * @return True if the content station is already present, false otherwise
     */
    public synchronized static boolean setReceivedTime(String CS_ID, LocalDateTime time) {
        boolean containsKey = receivedTime.containsKey(CS_ID);
        receivedTime.put(CS_ID, time);
        return containsKey;
    }

    /**
     * @return The received time of the Aggregation Server
     */
    synchronized static int logEvent() {
        return localClock.logCurrentEvent();
    }

    /**
     * Updates the lamport clock of the Aggregation Server using the HTTP message
     *
     * @param message The HTTP message
     */
    synchronized static void updateClockUsingHTTP(String message) {
        localClock.updateUsingHTTPMessage(message);
    }

    /**
     * @return The lamport clock of the Aggregation Server
     */
    synchronized static String getASTime() {
        return String.valueOf(localClock);
    }

    public static void main(String[] args) {
        try {
            // Restore previous backup
            ASBackup.restoreBackup();

            // Check if port is provided as command line argument
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            }

            ServerSocket ss = new ServerSocket(port);
            // Create a shutdown hook to release the port gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down...");
                    // Release resources, close the server socket, etc.
                    ss.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            // Start server
            System.out.println("Server running...");
            while (true) {
                Socket s = ss.accept();
                new RequestHandler(s).handle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The backup of the Aggregation Server
     */
    static class ASBackup {
        @JsonSerialize(using = ToStringSerializer.class)

        @JsonProperty("localClock")
        private LamportClock localClock;

        @JsonProperty("receivedTime")
        private HashMap<String, LocalDateTime> receivedTime;

        @JsonProperty("pid")
        private HashMap<String, Integer> pid;

        @JsonProperty("currentState")
        private HashMap<String, PriorityQueue<Weather>> currentState;

        @JsonProperty("port")
        private int port;


        /**
         * Creates a backup of the current state of the Aggregation Server
         *
         * @throws IOException
         */
        static synchronized void createBackup() throws IOException {
            ASBackup data = new ASBackup();
            data.localClock = AggregationServer.localClock;
            data.receivedTime = AggregationServer.receivedTime;
            data.currentState = AggregationServer.currentState;
            data.port = AggregationServer.port;
            data.pid = AggregationServer.pid;
            b.initiateBackup(data);
        }

        /**
         * Restores the backup of the Aggregation Server
         *
         * @throws IOException
         */
        static synchronized void restoreBackup() throws IOException {
            ASBackup data = (ASBackup) b.restore(new ASBackup());
            if (data == null) {
                return;
            }
            AggregationServer.currentState = data.currentState;
            AggregationServer.localClock = data.localClock;
            AggregationServer.receivedTime = data.receivedTime;
            AggregationServer.pid = data.pid;
            AggregationServer.port = data.port;
            for (Map.Entry<String, LocalDateTime> entry : AggregationServer.receivedTime.entrySet()) {
                Thread t = new Thread(new HandlePUT(entry.getKey()));
                t.start();
            }
        }
    }
}
