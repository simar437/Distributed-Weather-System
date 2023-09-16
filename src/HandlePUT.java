import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HandlePUT extends RequestHandler implements Runnable  {
    // HashMap<String, Weather> weatherData = new HashMap<>();
    List<Weather> whetherData;

    int remainingTime = 30000;

    public HandlePUT(RequestHandler r) throws IOException {
        super(r);
        PUTRequest();
    }

    public HandlePUT(List<Weather> objs, int remainingTime) {
        this.whetherData = objs;
        this.remainingTime = remainingTime;
    }
    private void PUTRequest() {
        try {
            String[] headAndBody = SendRequest.headersAndBodySplit(request);
            String body = headAndBody[1];
            System.out.println("body: " +body);
            ObjectMapper o = new ObjectMapper();
            List<Weather> objs = o.readValue(body, new TypeReference<List<Weather>>() {});
            this.whetherData = objs;
            for (Weather w : objs) {
                AggregationServer.updateCurrentState(w.id, w);
            }

            req.send("PUT Success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            removePrevState(whetherData, remainingTime);
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
