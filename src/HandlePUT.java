import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
public class HandlePUT extends RequestHandler implements Runnable  {
    String id;
    public HandlePUT(RequestHandler r) throws IOException {
        super(r);
        PUTRequest();
    }

    public HandlePUT(String id) {
        this.id = id;
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
            Set<Weather> s = o.readValue(body, new TypeReference<Set<Weather>>() {});

            Set<Weather> objs = new TreeSet<>((o1, o2) -> {
                int idComparison = o1.id.compareTo(o2.id);
                if (idComparison != 0) {
                    return idComparison;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date date1 = dateFormat.parse(o1.local_date_time_full);
                    Date date2 = dateFormat.parse(o2.local_date_time_full);
                    int comp = date1.compareTo(date2);
                    return comp > 0 ? 1 : 0;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
            objs.addAll(s);

            for (Weather w : objs) {
                AggregationServer.updateCurrentState(w.id, w);
                System.out.println(w);
            }
            String csID = objs.iterator().next().contentServerID;
            this.id = csID;
            if (AggregationServer.getTime(csID) == null) {
                req.send("HTTP/1.1 201 OK\r\n\r\n");
            }
            else {
                req.send("HTTP/1.1 200 OK\r\n\r\n");
            }
        } catch (Exception e) {
            req.send("HTTP/1.1 500 Internal Server Error\r\n");
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            removePrevState(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void removePrevState(String CS_ID) throws InterruptedException, IOException {
        if (AggregationServer.setReceivedTime(CS_ID, LocalDateTime.now())) {
            return;
        }
        while (true) {
            LocalDateTime t = AggregationServer.getTime(CS_ID);
            long mili = Duration.between(LocalDateTime.now(), t.plusSeconds(30)).getSeconds() * 1000;
            if (mili <= 0) {
                break;
            }
            Thread.sleep(mili);
        }
        AggregationServer.removeContentStation(CS_ID);
    }
}
