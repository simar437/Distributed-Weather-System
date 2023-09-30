import java.net.Socket;
import java.util.Objects;

public class RequestHandler {
    Socket socket;
    SendRequest req;
    String request;

    public RequestHandler() {}

    public RequestHandler(RequestHandler other) {
        try {
            this.socket = other.socket;
            this.req = other.req;
            this.request = other.request;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public RequestHandler(Socket s) {
        try {
            this.socket = s;
            req = new SendRequest(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        try {
            request = req.receive();
            AggregationServer.updateClockUsingHTTP(request);
            if (request.startsWith("GET")) {
                Thread t = new Thread(new HandleGET(this, AggregationServer.getCurrentState()));
                t.start();
            }
            else if (request.startsWith("PUT")) {
                Thread t = new Thread(new HandlePUT(this));
                t.start();
            }
            else {
                req.send("HTTP/1.1 400 Bad Request\r\n\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
