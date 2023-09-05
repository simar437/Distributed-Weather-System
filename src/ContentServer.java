import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ContentServer {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 4567);
            String text = new String(Files.readAllBytes(Paths.get("weather data/file.txt")));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }
}
