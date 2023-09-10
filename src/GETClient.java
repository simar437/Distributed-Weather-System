import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GETClient {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 4567);
            String host = "";
            String id = "";
            String stationId = "";
            PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
            String request = "GET weather/" + stationId + " HTTP/1.1\n" +
                    "Host:" + host +"\n" +
                    "User-Agent: C-" + id + "\n" +
                    "Accept: application/json\n";
            System.out.println(request);
            writer.println(request);
            Scanner sc = new Scanner(s.getInputStream());
            while (sc.hasNext()) {
                System.out.println(sc.nextLine());
            }
        }
        catch (Exception ignored) {

        }
    }
}
