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

        synchronized void backup() throws IOException {
            b.initiateBackup(this);
        }

        synchronized static CSData restore() throws IOException {
            return (CSData) b.restore(new CSData());
        }
    }
    public static void main(String[] args) throws IOException {
        b = new Backup(args[0]);
        CSData c = CSData.restore();
        if (c == null) {
            c = new CSData();
            c.data = new ArrayDeque<>(Arrays.asList(args));
            c.data.removeFirst();
            c.localClock = new LamportClock();

            if (c.data.size() >= 1) {
                List<String> url =  new ArrayList<>(Arrays.asList(c.data.getFirst().split("://|:")));
                if (url.size() == 3) {
                    url.remove(0);
                }
                c.host = url.get(0);
                c.port = Integer.parseInt(url.get(1));
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
            String sync = "GET /SYNC HTTP/1.1\r\n" +
                    "Host: " + c.host + "\r\n" +
                    "Lamport-Clock: " + c.localClock.logCurrentEvent() + "\r\n" +
                    "CS-ID: " + c.CS_ID + "\r\n" +
                    "\r\n";
            SendRequest r = new SendRequest(c.host, c.port);
            String syncMessage = r.doALL(sync);
            c.localClock.updateUsingHTTPMessage(syncMessage);
            System.out.println("done syncing");
            if (c.data.isEmpty()) {
                System.out.println("No files provided!");
                System.exit(1);
            }
            while (true){
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
                String request = "PUT /weather.json HTTP/1.1\r\n" +
                        "User-Agent: ATOMClient/1/0\r\n" +
                        "Lamport-Clock: " + c.localClock + "\r\n" +
                        "CS-ID: " + c.CS_ID + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + text.length() + "\r\n" +
                        "\r\n" +
                        text + "\r\n";

                SendRequest req = new SendRequest(c.host, 4567);
                System.out.println(req.doALL(request));
                c.data.removeFirst();
                c.backup();
                if (c.data.isEmpty()) {
                    break;
                }
                Thread.sleep(28000);
            }
            System.out.println("Removing backup...");
            b.destroyBackup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
