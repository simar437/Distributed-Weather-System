import java.io.PrintWriter;
import java.net.Socket;

public class ContentServer {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 4567);

        }
        catch (Exception ignored) {

        }
    }
}
