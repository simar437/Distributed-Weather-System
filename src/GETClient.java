import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GETClient {
    LamportClock localClock = new LamportClock();
    public static void main(String[] args) {
        try {
            List<String> url = null;
            if (args.length >= 1) {
                url = Arrays.asList(args[0].split("://|:"));
                if (url.size() == 3) {
                    url.remove(0);
                }
            }
            else {
                System.out.println("URL not provided");
                System.exit(1);
            }
            String host = url.get(0);
            int port = Integer.parseInt(url.get(1));
            String id = "";
            String stationId = "";
            if (args.length == 2) {
                stationId = args[1];
            }
            String request = "GET /" + stationId + " HTTP/1.1\r\n" +
                    "Host:" + host +"\r\n" +
                    "User-Agent: C-" + id + "\r\n" +
                    "Accept: application/json\r\n";
            SendRequest req = new SendRequest(host, port);
            String response = req.doALL(request);
            String[] data = SendRequest.headersAndBodySplit(response);
            if (data[0].contains("OK")) {
                ObjectMapper o = new ObjectMapper();
                for (Weather w : o.readValue(data[1], new TypeReference<List<Weather>>() {})) {
                    System.out.println(w);
                    System.out.println();
                }
            }
            else {
                System.out.println("Some error occurred");
                System.out.println(response);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
