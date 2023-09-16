import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GETClient {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {
            String host = "";
            String id = "";
            String stationId = "";
            String request = "GET /" + stationId + " HTTP/1.1\r\n" +
                    "Host:" + host +"\r\n" +
                    "User-Agent: C-" + id + "\r\n" +
                    "Accept: application/json\r\n";
            //System.out.println(request);
            SendRequest req = new SendRequest("localhost", 4567);
            String response = req.doALL(request);
            System.out.println(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
