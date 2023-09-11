import java.io.*;
public class Backup {
    static FileWriter f;

    static void restore() throws FileNotFoundException {
        if (!new File("backup.txt").exists()) {
            return;
        }
        FileReader fr = new FileReader("backup.txt");

    }
    static void initiateBackup() throws IOException {
        f = new FileWriter("backup.txt");
    }

    static void logEvent(String event) throws IOException {
        f.write(event);
    }
}
