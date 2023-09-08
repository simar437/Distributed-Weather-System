import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HandleClient implements Runnable {

    private Socket socket;
    BufferedReader reader = null;
    PrintWriter writer = null;
    List<List<ObjectNode>> requestHistory = new ArrayList<>();

    public HandleClient(Socket s) {
        try {
            this.socket = s;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            System.out.println(reader.readLine());
            String method = String.valueOf(reader.read());
            if (Objects.equals(method, "GET")) {
                GETRequest();
            }
            if (Objects.equals(method, "PUT")) {
                PUTRequest();
            }
        } catch (Exception ignored) {}
    }
    void PUTRequest() {

    }
    void GETRequest() {
        writer.println("Hello, World!");
    }
}
