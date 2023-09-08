import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class ContentServer {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {

            Socket s = new Socket("localhost", 4567);
            String file = "weather data/file.txt";
            Scanner sc = new Scanner(new FileReader(file));
            List<ObjectNode> jsonObjects = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode currentJsonObject = null;
            while (sc.hasNext()) {
                String[] parts = sc.nextLine().split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    // Check if the key is "id"
                    if (key.equals("id")) {
                        // Create a new JSON object
                        currentJsonObject = objectMapper.createObjectNode();
                        jsonObjects.add(currentJsonObject);
                    }

                    // Add a key-value pair to the current JSON object
                    if (currentJsonObject != null) {
                        currentJsonObject.put(key, value);
                    }
                }
            }

            String text = jsonObjects.toString();
            String request = "PUT /weather.json HTTP/1.1\n" +
                    "User-Agent: ATOMClient/1/0\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length: " + text.length() + "\n" +
                    text;
            System.out.println(request);

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.print(request);
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
