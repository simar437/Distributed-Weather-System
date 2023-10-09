import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ContentServer {
    static Backup b;

    /**
     * The backup of the Content Server
     */
    public static class CSData {
        @JsonSerialize(using = ToStringSerializer.class)
        @JsonProperty("localClock")
        private LamportClock localClock;
        @JsonProperty("arr")
        private ArrayDeque<String> data;

        @JsonProperty("host")
        String host;
        @JsonProperty("port")
        int port;
        @JsonProperty("CS_ID")
        String CS_ID;


        /**
         * Creates a backup of the current state of the Content Server
         *
         * @throws IOException
         */
        synchronized void backup() throws IOException {
            b.initiateBackup(this);
        }

        /**
         * Restores the backup of the Content Server
         *
         * @return The restored backup
         * @throws IOException
         */
        synchronized static CSData restore() throws IOException {
            return (CSData) b.restore(new CSData());
        }
    }
    public static void main(String[] args) throws IOException {
        // Get the backup directory
        String dir = args[0];
        b = new Backup(dir);

        // Restore the backup
        CSData c = CSData.restore();

        // If there is no backup, create a new instance of data
        if (c == null) {
            c = new CSData();
            c.data = new ArrayDeque<>(Arrays.asList(args));
            c.data.removeFirst();
            c.localClock = new LamportClock();

            // Get the host and port from the URL
            if (c.data.size() >= 1) {
                List<String> url =  new ArrayList<>(Arrays.asList(c.data.getFirst().split("://|:")));
                if (url.size() == 3) {
                    url.remove(0);
                }
                c.host = url.get(0);
                c.port = Integer.parseInt(url.get(1));

                // Generate a unique ID for the Content Server
                c.CS_ID = String.valueOf(UUID.randomUUID());

                c.data.removeFirst();
            }
            else {
                System.out.println("URL not provided");
                System.exit(1);
            }
            c.backup();
        }
        try {
            // Sync with the Aggregation Server
            String sync = "GET /SYNC HTTP/1.1\r\n" +
                    "Host: " + c.host + "\r\n" +
                    "Lamport-Clock: " + c.localClock.logCurrentEvent() + "\r\n" +
                    "CS-ID: " + c.CS_ID + "\r\n" +
                    "\r\n";
            SendRequest r = new SendRequest(c.host, c.port);
            String syncMessage = r.doALL(sync);

            // Update the local clock using the sync message
            c.localClock.updateUsingHTTPMessage(syncMessage);

            // Exit if no files are provided
            if (c.data.isEmpty()) {
                System.out.println("No files provided!");
                System.exit(1);
            }
            while (true){
                // Read the file
                Scanner sc = new Scanner(new FileReader(c.data.getFirst()));
                List<ObjectNode> jsonObjects = new ArrayList<>();
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode currentJsonObject = null;
                while (sc.hasNext()) {
                    String[] parts = sc.nextLine().split(":", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        // Check if the key is "id"
                        if (key.equals("id")) {
                            // Create a new JSON object
                            currentJsonObject = objectMapper.createObjectNode();

                            // Add lamport clock and content server ID to the JSON object
                            currentJsonObject.put("time", c.localClock.logCurrentEvent());
                            currentJsonObject.put("contentServerID", c.CS_ID);
                            jsonObjects.add(currentJsonObject);
                        }

                        // Add a key-value pair to the current JSON object
                        if (currentJsonObject != null) {
                            currentJsonObject.put(key, value);
                        }
                    }
                }

                String text = jsonObjects.toString();
                System.out.println(text);
                String request = "PUT /weather.json HTTP/1.1\r\n" +
                        "User-Agent: ATOMClient/1/0\r\n" +
                        "Lamport-Clock: " + c.localClock + "\r\n" +
                        "CS-ID: " + c.CS_ID + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + text.length() + "\r\n" +
                        "\r\n" +
                        text + "\r\n";

                // Send the PUT request to the Aggregation Server
                SendRequest req = new SendRequest(c.host, 4567);
                System.out.println(req.doALL(request));

                // Remove the first file from the list of files as it has been sent
                c.data.removeFirst();

                // Backup the current state of the Content Server
                c.backup();

                // Exit if there are no more files to send
                if (c.data.isEmpty()) {
                    break;
                }

                // Wait for 28 seconds to simulate the time between sending files
                Thread.sleep(28000);
            }

            // Remove the backup as the Content Server has finished sending files
            System.out.println("Removing backup for Content Server " + dir);
            b.destroyBackup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
