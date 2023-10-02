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

    /**
     * This method handles the PUT request
     * @throws IOException
     */
    private void PUTRequest() throws IOException {
        try {
            String[] headAndBody = SendRequest.headersAndBodySplit(request);

            // If the body is empty, send 204 No Content
            if (headAndBody.length < 2 || headAndBody[1].isEmpty()) {
                req.send("HTTP/1.1 204 No Content");
                return;
            }
            String body = headAndBody[1];

            ObjectMapper o = new ObjectMapper();
            Set<Weather> s = o.readValue(body, new TypeReference<Set<Weather>>() {});


            // Remove duplicates stations sent by Content Server based on local_date_time_full
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

            // Update the current state of the Aggregation Server
            for (Weather w : objs) {
                AggregationServer.updateCurrentState(w.id, w);
            }

            String csID = objs.iterator().next().contentServerID;
            this.id = csID;

            // If the Aggregation Server has not received any data from the Content Server, send 201 Created
            // Else, send 200 OK
            if (AggregationServer.getTime(csID) == null) {
                req.send("HTTP/1.1 201 OK\r\n\r\n");
                AggregationServer.addContentStation(csID);
            }
            else {
                req.send("HTTP/1.1 200 OK\r\n\r\n");
            }
        } catch (Exception e) {
            // If there is an error, send 500 Internal Server Error
            req.send("HTTP/1.1 500 Internal Server Error\r\n");
        }
    }

    /**
     * This method activates the thread to remove the previous state of the Content Server
     */
    @Override
    public void run() {
        try {
            removePrevState(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method removes the previous state of the Content Server
     * @param CS_ID The ID of the Content Server to be removed
     * @throws InterruptedException
     * @throws IOException
     *
     * This method updates the last received time of the Content Server to the current time
     * If a thread is already running to remove the Content Server, return
     * If the Content Server has not given an update for 30 seconds, remove the Content Server
     */
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
