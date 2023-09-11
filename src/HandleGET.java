import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HandleGET implements Runnable {

    private Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    // HashMap<String, Weather> weatherData = new HashMap<>();
    HashMap<String, Deque<Weather>> currentState;

    Boolean isPUT = false;

    public HandleGET(Socket s, HashMap<String, Deque<Weather>> copyOfCurrentState) {
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
                String id = "";
                if (header.length >= 2 && !Objects.equals(header[1], "/")) {
                    String[] toGet = header[1].split("/");
                    if (!Objects.equals(toGet[toGet.length - 1], "weather")) {
                        id = toGet[toGet.length - 1];
                    }
                }
                GETRequest(id);
            }
            if (Objects.equals(method, "PUT")) {
                isPUT = true;
                PUTRequest();
            }
            AggregationServer.logEvent();
            close();
            if (isPUT) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void PUTRequest() throws IOException, InterruptedException {

        close();
        Thread.sleep(30000);

    }
    void GETRequest(String id) throws JsonProcessingException {
        ObjectMapper o = new ObjectMapper();
        String text;
        if (Objects.equals(id, "")) {
            ArrayList<Weather> arr = new ArrayList<>();
            for (Map.Entry<String, Deque<Weather>> pair : currentState.entrySet()) {
                arr.add(pair.getValue().getLast());
            }
            text = o.writeValueAsString(arr);
        }
        else {
            text = o.writeValueAsString(currentState.get(id).getLast());
        }

        System.out.println("get reached");
        writer.println("HTTP/1.1 200 OK\n" +
                "Content-Type: application/json\n" +
                "Content-Length:" + text.length() + "\n" +
                "\n" +
                text);
        close();
    }

    void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
