import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Deque;
import java.util.HashMap;
import java.util.Objects;

public class RequestHandler {
    Socket socket;
    BufferedReader reader;

    public RequestHandler() {}

    public RequestHandler(RequestHandler other) {
        try {
            this.socket = other.socket;
            this.reader = other.reader;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public RequestHandler(Socket s) {
        try {
            this.socket = s;
            System.out.println(socket.getInetAddress());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        try {
            String first = reader.readLine();
            AggregationServer.logEvent();
            String[] header = first.split(" ");
            String method = header[0];
            if (Objects.equals(method, "GET")) {

                String id = "";
                if (header.length >= 2 && !Objects.equals(header[1], "/")) {
                    String[] toGet = header[1].split("/");
                    if (!Objects.equals(toGet[toGet.length - 1], "weather")) {
                        id = toGet[toGet.length - 1];
                    }
                }
                Thread t = new Thread(new HandleGET(this, AggregationServer.getCurrentState() , id));
                t.start();
            }
            else if (Objects.equals(method, "PUT")) {

                Thread t = new Thread(new HandlePUT(this));
                t.start();
            }
            AggregationServer.logEvent();
            //close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void close() {
        try {
            reader.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
