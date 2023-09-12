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
            String request = "GET weather/" + stationId + " HTTP/1.1\n" +
                    "Host:" + host +"\n" +
                    "User-Agent: C-" + id + "\n" +
                    "Accept: application/json\n";
            //System.out.println(request);
            String response = SendRequest.doALL("localhost", 4567, request);
            System.out.println(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
