import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileReader;
import java.util.*;


public class ContentServer {
    public static void main(String[] args) {
        LamportClock localClock = new LamportClock();
        String host = "localhost";
        final String CS_ID = String.valueOf(UUID.randomUUID());
        try {
            String sync = "GET /SYNC HTTP/1.1\r\n" +
                    "Host: " + host +
                    "Lamport-Clock: " + localClock.logCurrentEvent() + "\r\n" +
                    "CS-ID: " + CS_ID + "\r\n" +
                    "\r\n";
            SendRequest r = new SendRequest(host, 4567);
            String syncMessage = r.doALL(sync);
            localClock.updateUsingHTTPMessage(syncMessage);

            for (String file : args) {
                Scanner sc = new Scanner(new FileReader(file));
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
                            currentJsonObject.put("time", localClock.logCurrentEvent());
                            currentJsonObject.put("contentServerID", CS_ID);
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
                        "Lamport-Clock: " + localClock + "\r\n" +
                        "CS-ID: " + CS_ID + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + text.length() + "\r\n" +
                        "\r\n" +
                        text + "\r\n";

                SendRequest req = new SendRequest(host, 4567);
                System.out.println(req.doALL(request));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
