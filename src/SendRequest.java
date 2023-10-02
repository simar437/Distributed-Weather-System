import java.io.*;
import java.net.Socket;

public class SendRequest {
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;


    /**
     * Constructor for SendRequest
     * Initializes the reader and writer
     *
     * @param s The socket to be used for sending and receiving
     * @throws IOException
     */
    SendRequest(Socket s) throws IOException {
        socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Constructor for SendRequest that takes in a host and port
     *
     * @param host The host to be used for sending and receiving
     * @param port The port to be used for sending and receiving
     * @throws IOException
     */
    SendRequest(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    /**
     * Splits the headers and body of the response / request
     *
     * @param str The response
     * @return The headers and body of the response / request
     */
    static String[] headersAndBodySplit(String str) {
        return str.split("\n\n");
    }

    /**
     * Sends the request, receives the response and closes the socket
     *
     * @param request The request to be sent
     * @return The response received
     * @throws IOException
     */
    String doALL(String request) throws IOException {
        send(request);
        String response = receive();
        close();
        return response;
    }


    /**
     * Sends the request
     *
     * @param request The request to be sent
     * @throws IOException
     */
    void send(String request) throws IOException {
        writer.println(request);
    }

    /**
     * Receives the response
     *
     * @return The response received
     * @throws IOException
     */
    String receive() throws IOException {
        StringBuilder response = new StringBuilder();

        // Read the response line by line
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            response.append(line).append("\n");
        }
        response.append("\n");

        // If the response contains Content-Length, read the body
        if (response.toString().contains("Content-Length:")) {
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }

/**
     * Closes the reader, writer and socket
     *
     * @throws IOException
     */
    void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
