import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HandleGET extends RequestHandler implements Runnable {
    HashMap<String, PriorityQueue<Weather>> currentState;

    public HandleGET(RequestHandler r, HashMap<String, PriorityQueue<Weather>> copyOfCurrentState) {
        super(r);
        this.currentState = copyOfCurrentState;
    }


    @Override
    public void run() {
        GETRequest();
    }
    private void GETRequest()  {
        try {
            String path = request.split(" ")[1];
            ObjectMapper o = new ObjectMapper();
            String text;
            if (Objects.equals(path, "/")) {
                ArrayList<Weather> arr = new ArrayList<>();
                for (Map.Entry<String, PriorityQueue<Weather>> pair : currentState.entrySet()) {
                    System.out.println(pair.getValue().size());
                    if (!pair.getValue().isEmpty()) {
                        Weather w = new Weather(pair.getValue().peek());
                        w.contentServerID = null;
                        w.time = -1;
                        arr.add(w);
                    }
                }


                text = o.writeValueAsString(arr);
            } else if (path.equals("/SYNC")) {
                String response = "HTTP/1.1 200 OK\n" +
                        "Lamport-Clock: " + AggregationServer.logEvent();
                req.send(response);
                return;
            }
            else {
                String id = path.substring(1);
                if (!currentState.containsKey(id) || currentState.get(id).isEmpty()) {
                    req.send("HTTP/1.1 404 Not Found\r\n");
                    return;
                }
                Weather w = new Weather(currentState.get(id).peek());
                w.contentServerID = null;
                w.time = -1;
                text = o.writeValueAsString(w);
            }
            String response = "HTTP/1.1 200 OK\n" +
                    "Lamport-Clock: " + AggregationServer.logEvent() + "\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length:" + text.length() + "\r\n" +
                    "\r\n" +
                    text;
            req.send(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
