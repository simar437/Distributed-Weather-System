import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class Backup {

    static int num = 1;

    static void restore() throws IOException {
        if (!new File("backup.txt").exists()) {
            return;
        }
        String text = new String(Files.readAllBytes(Paths.get("backup1.txt")));
        ObjectMapper o = new ObjectMapper();

        HashMap<String, PriorityQueue<Weather>> h = o.readValue(text,
                new TypeReference<HashMap<String, PriorityQueue<Weather>>>() {});

        System.out.println("This Works?: " + o.writeValueAsString(h));
    }
    static void initiateBackup(Object obj) throws IOException {
        FileWriter f = new FileWriter("backup" + num +".txt");
        ObjectMapper o = new ObjectMapper();
        f.write(o.writeValueAsString(obj));
        f.close();
    }

}
