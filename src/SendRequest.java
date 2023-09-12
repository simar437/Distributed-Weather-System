import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class SendRequest {

    static String[] headersAndBodySplit(String str) {
        StringBuilder headers = new StringBuilder();
        StringBuilder body = new StringBuilder();
        boolean bodyReached = false;
        for (String s : str.split("\n")) {
            System.out.println("S: "+s);
            if (Objects.equals(s, "\r")) {
                bodyReached = true;
                continue;
            }
            if (bodyReached) {
                body.append(s).append("\n");
            }
            else {
                headers.append(s).append("\n");
            }
        }

        return new String[] {headers.toString(), body.toString()};
    }

    static String doALL(String host, int port, String request) throws IOException {
        Socket s = new Socket(host, port);
        send(s, request);
        String response = receive(s);
        s.close();
        return response;
    }

    static void send(Socket s, String request) throws IOException {
        PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
        writer.println(request);
    }

    static String receive(Socket s) throws IOException {
        Scanner sc = new Scanner(s.getInputStream());
        StringBuilder response = new StringBuilder();
        while (sc.hasNext()) {
            response.append(sc.nextLine());
        }
        return response.toString();
    }
}
