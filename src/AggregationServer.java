import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class AggregationServer {
    LamportClock localClock = new LamportClock();
    static Scanner sc;

    static void PUTRequest(Socket s) throws IOException {

    }
    static void GETRequest(Socket s) throws IOException {

    }
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(4567);
            System.out.println("Server running...");
            while (true) {
                Socket s = ss.accept();
                Thread t = new Thread(new HandleClient(s));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
