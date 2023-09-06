import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class AggregationServer {
    LamportClock localClock = new LamportClock();

    static void PUTRequest(Socket s) throws IOException {
        Scanner sc = new Scanner(s.getInputStream());
        while (sc.hasNext()) {
            System.out.println(sc.nextLine());
        }
    }
    static void GETRequest(Socket s) throws IOException {
        Scanner sc = new Scanner(s.getInputStream());
    }
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(4567);
            System.out.println("Server running...");
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client connected: " + s.getInetAddress());

                Scanner sc = new Scanner(s.getInputStream());
                String method = sc.next();
                if (Objects.equals(method, "GET")) {
                    GETRequest(s);
                }
                if (Objects.equals(method, "PUT")) {
                    PUTRequest(s);
                }
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
