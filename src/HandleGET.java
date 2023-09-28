import com.fasterxml.jackson.databind.ObjectMapper;

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
            ArrayList<Weather> arr = new ArrayList<>();
            if (path.equals("/") || path.equals("/weather") || path.equals("/weather/")) {
                for (Map.Entry<String, PriorityQueue<Weather>> pair : currentState.entrySet()) {
                    if (!pair.getValue().isEmpty()) {
                        Weather w = new Weather(pair.getValue().peek());
                        w.contentServerID = null;
                        w.time = -1;
                        arr.add(w);
                    }
                }
            } else if (path.equals("/SYNC")) {

                String body = "Lamport Clock Time is " + AggregationServer.logEvent() + "\r\n";
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Lamport-Clock: " +  AggregationServer.getASTime() + "\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "\r\n" +
                        body;

                req.send(response);
                return;
            }
            else {
                String id = null;
                if (path.startsWith("/weather/")) {
                    id = path.substring(path.lastIndexOf("/") + 1);
                }
                if (id == null || !currentState.containsKey(id) || currentState.get(id).isEmpty()) {
                    req.send("HTTP/1.1 404 Not Found\r\n" +
                            "Lamport-Clock: " + AggregationServer.logEvent() + "\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: 96\r\n" +
                            "\r\n" +
                            "The requested resource could not be found on the server. " +
                            "Please check the URL and try again.\r\n\r\n"
                    );
                    return;
                }
                Weather w = new Weather(currentState.get(id).peek());
                w.contentServerID = null;
                w.time = -1;
                arr.add(w);
            }

            text = o.writeValueAsString(arr);
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Lamport-Clock: " + AggregationServer.logEvent() + "\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + text.length() + "\r\n" +
                    "\r\n" +
                    text + "\r\n";
            req.send(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
