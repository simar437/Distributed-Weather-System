import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AggregationServer {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(4567);
            System.out.println("Server running...");
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client connected: " + s.getInetAddress());

                Scanner sc = new Scanner(s.getInputStream());
                while (sc.hasNext()) {
                    System.out.println(sc.nextLine());
                }

                s.close(); // Close the socket after handling client interaction
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
