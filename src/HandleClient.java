import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class HandleClient implements Runnable {

    private Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    // HashMap<String, Weather> weatherData = new HashMap<>();
    HashMap<String, Weather> currentState;

    public HandleClient(Socket s, HashMap<String, Weather> copyOfCurrentState) {
        try {
            this.socket = s;
            this.currentState = copyOfCurrentState;
            System.out.println(socket.getInetAddress());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            String first = reader.readLine();
            AggregationServer.logEvent();
            String[] header = first.split(" ");
            String method = header[0];
            if (Objects.equals(method, "GET")) {
                GETRequest();
            }
            if (Objects.equals(method, "PUT")) {
                PUTRequest();
            }
            AggregationServer.logEvent();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void PUTRequest() throws IOException {
        String[] second = reader.readLine().split(" ");
        reader.readLine();
        String body = "";
        String line = reader.readLine();
        while (line != null) {
            body += line + "\n";
            line = reader.readLine();
        }
        ObjectMapper o = new ObjectMapper();
        Weather w = o.readValue(body, Weather.class);
        AggregationServer.updateCurrentState(w.id, w);
    }
    void GETRequest() {
        System.out.println("get reached");
        writer.println("HTTP/1.1 200 OK\n" +
                "Content-Type: text/plain\n" +
                "Content-Length: 13\n" +
                "\n" +
                currentState.get("123"));
    }

    void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
