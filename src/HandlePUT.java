import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HandlePUT extends RequestHandler implements Runnable  {
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
            System.out.println("State: " +AggregationServer.getCurrentState());
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
    void removePrevState(List<Weather> objs, long mili) throws InterruptedException {
        if (objs.isEmpty()) {
            return;
        }
        String id = objs.get(0).getContentServerID();
        if (AggregationServer.setReceivedTime(id, LocalDateTime.now())) {
            return;
        }
        while (true) {
            LocalDateTime t = AggregationServer.getTime(id);
            mili = Duration.between(LocalDateTime.now(), t.plusSeconds(30)).getSeconds() * 1000;
            if (mili <= 0) {
                break;
            }
            Thread.sleep(mili);
        }
        System.out.println("Removing...");
        AggregationServer.removeContentStation(id);
        System.out.println("Removed!");
    }
}
