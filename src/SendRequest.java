import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class SendRequest {
    Socket socket;
    BufferedReader reader;

    SendRequest(Socket s) throws IOException {
        socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    SendRequest(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    static String[] headersAndBodySplit(String str) {
        return str.split("\n\n");
    }

    String doALL(String request) throws IOException {
        send(socket, request);
        String response = receive();
        socket.close();
        return response;
    }


    void send(String request) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(request);
        writer.println("END");
    }

    static void send(Socket s, String request) throws IOException {
        PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
        writer.println(request);
        writer.println("END");
    }

//    static String receive(Socket s) throws IOException {
//        Scanner sc = new Scanner(s.getInputStream());
//        StringBuilder response = new StringBuilder();
//        while (sc.hasNext()) {
//            response.append(sc.nextLine());
//        }
//        return response.toString();
//    }

    String receive() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.equals("END") && !line.equals("END:")) {
            response.append(line).append("\n");
        }
        return response.toString();
    }


}
