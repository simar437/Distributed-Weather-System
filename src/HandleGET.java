import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HandleGET extends RequestHandler implements Runnable {
    HashMap<String, Deque<Weather>> currentState;
    String id;

    public HandleGET(RequestHandler r, HashMap<String, Deque<Weather>> copyOfCurrentState, String id) {
        super(r);
        this.currentState = copyOfCurrentState;
        this.id = id;
    }


    @Override
    public void run() {
        System.out.println("Connected: 123456");

        GETRequest();
    }
    private void GETRequest()  {
        System.out.println("Connected: " + socket.isConnected());


        try {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
            ObjectMapper o = new ObjectMapper();
            String text;
            if (Objects.equals(id, "")) {
                ArrayList<Weather> arr = new ArrayList<>();
                for (Map.Entry<String, Deque<Weather>> pair : currentState.entrySet()) {
                    arr.add(pair.getValue().getLast());
                }
                text = o.writeValueAsString(arr);
            } else {
                text = o.writeValueAsString(currentState.get(id).getLast());
            }
            System.out.println("get reached");
            System.out.println(text);
            String response = "HTTP/1.1 200 OK\n" +
                    "Content-Type: application/json\n" +
                    "Content-Length:" + text.length() + "\n" +
                    "\n" +
                    text;
            //writer.println();
            req.send(response);
            // writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
