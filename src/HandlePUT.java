import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

public class HandlePUT implements Runnable{
    private Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    // HashMap<String, Weather> weatherData = new HashMap<>();
    HashMap<String, Deque<Weather>> currentState;
    List<Weather> wheatherData;

    int remainingTime = 30000;

    public HandlePUT(Socket s, HashMap<String, Deque<Weather>> copyOfCurrentState) {
        try {
            this.socket = s;
            this.currentState = copyOfCurrentState;
            System.out.println(socket.getInetAddress());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            PUTRequest();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HandlePUT(List<Weather> objs, int remainingTime) {
        this.wheatherData = objs;
        this.remainingTime = remainingTime;
    }
    private void PUTRequest() {
        try {
            String[] second = reader.readLine().split(" ");
            reader.readLine();
            String body = "";
            reader.readLine();
            String line = reader.readLine();
            System.out.println(line);
            while (line != null) {
                body += line + "\n";
                line = reader.readLine();
            }
            ObjectMapper o = new ObjectMapper();
            List<Weather> objs = o.readValue(body, new TypeReference<List<Weather>>() {
            });
            this.wheatherData = objs;
            for (Weather w : objs) {
                AggregationServer.updateCurrentState(w.id, w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            removePrevState(wheatherData, remainingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void removePrevState(List<Weather> objs, int mili) throws InterruptedException {
        Thread.sleep(mili);
        for (Weather w : objs) {
            AggregationServer.removePrevState(w.id);
        }
    }
}
