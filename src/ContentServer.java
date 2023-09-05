import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ContentServer {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {

            //Socket s = new Socket("localhost", 4567);
            String text = new String(Files.readAllBytes(Paths.get("weather data/file.txt")));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> keyValueData = Arrays.stream(text.split("\n"))
                    .map(line -> line.split(":", 2))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));
            text = objectMapper.writeValueAsString(keyValueData);
            String request = "PUT /weather.json HTTP/1.1\n" +
                    "User-Agent: ATOMClient/1/0\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length: " + text.length() + "\n" +
                    text;

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }
}
