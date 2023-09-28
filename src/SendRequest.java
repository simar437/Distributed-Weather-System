import java.io.*;
import java.net.Socket;

public class SendRequest {
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;


    SendRequest(Socket s) throws IOException {
        socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    SendRequest(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    static String[] headersAndBodySplit(String str) {
        return str.split("\n\n");
    }

    String doALL(String request) throws IOException {
        send(request);
        String response = receive();
        close();
        return response;
    }


    void send(String request) throws IOException {
        // System.out.println(request);
        writer.println(request);
        // writer.println("END");
    }

    String receive() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            response.append(line).append("\n");
        }
        response.append("\n");
        if (response.toString().contains("Content-Length:")) {
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }

    void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
