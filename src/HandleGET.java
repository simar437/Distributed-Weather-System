import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class HandleGET extends RequestHandler implements Runnable {

    // The copy AggregationServer.currentState at the time of the request
    HashMap<String, PriorityQueue<Weather>> currentState;

    /**
     * Constructor for HandleGET
     * @param r The RequestHandler object
     * @param copyOfCurrentState The copy of AggregationServer.currentState at the time of the request
     */
    public HandleGET(RequestHandler r, HashMap<String, PriorityQueue<Weather>> copyOfCurrentState) {
        super(r);
        this.currentState = copyOfCurrentState;
    }


    /**
     * This method runs
     */
    @Override
    public void run() {
        GETRequest();
    }

    /**
     * This method handles the GET request
     */
    private void GETRequest()  {
        try {
            // Get the path from the request
            String path = request.split(" ")[1];

            ObjectMapper o = new ObjectMapper();
            String text;
            ArrayList<Weather> arr = new ArrayList<>();

            /*
             * Handle /weather and / requests
             * For e.g.
             *
             *      localhost:4567 or
             *      localhost:4567/weather or
             *      localhost:4567/weather/<-nothing->
             */
            if (path.equals("/") || path.equals("/weather") || path.equals("/weather/")) {
                for (Map.Entry<String, PriorityQueue<Weather>> pair : currentState.entrySet()) {
                    if (!pair.getValue().isEmpty()) {
                        Weather w = new Weather(pair.getValue().peek());
                        w.contentServerID = null;
                        w.time = -1;
                        arr.add(w);
                    }
                }
            }
            // Handle SYNC request
            else if (path.equals("/SYNC")) {

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
                // Handle /weather/{id} request
                if (path.startsWith("/weather/")) {
                    id = path.substring(path.lastIndexOf("/") + 1);
                }

                // Handle Invalid request
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

            // Convert the ArrayList to JSON
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
