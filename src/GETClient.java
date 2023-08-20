import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class GETClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 4567);
            PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
            writer.println("Hello, World");
        }
        catch (Exception ignored) {

        }
    }
}
