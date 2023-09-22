import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HandlePUT extends RequestHandler implements Runnable  {
    List<Weather> whetherData = null;

    int remainingTime = 30000;

    public HandlePUT(RequestHandler r) throws IOException {
        super(r);
        PUTRequest();
    }

    public HandlePUT(List<Weather> objs, int remainingTime) {
        this.whetherData = objs;
        this.remainingTime = remainingTime;
    }
    private void PUTRequest() throws IOException {
        try {
            String[] headAndBody = SendRequest.headersAndBodySplit(request);
            if (headAndBody.length < 2 || headAndBody[1].isEmpty()) {
                req.send("HTTP/1.1 204 No Content");
                return;
            }
            String body = headAndBody[1];

            ObjectMapper o = new ObjectMapper();
            List<Weather> objs = o.readValue(body, new TypeReference<List<Weather>>() {});
            this.whetherData = objs;
            for (Weather w : objs) {
                AggregationServer.updateCurrentState(w.id, w);
            }
            String csID = objs.get(0).contentServerID;
            if (AggregationServer.getTime(csID) == null) {
                req.send("HTTP/1.1 201 OK\r\n");
            }
            else {
                req.send("HTTP/1.1 200 OK\r\n");
            }
        } catch (Exception e) {
            req.send("HTTP/1.1 500 Internal Server Error\r\n");
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            removePrevState(whetherData, remainingTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void removePrevState(List<Weather> objs, long mili) throws InterruptedException, IOException {
        if (objs == null || objs.isEmpty()) {
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
        AggregationServer.removeContentStation(id);
    }
}
