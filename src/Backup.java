import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Backup {
    String directory;

    Backup(String directory) {
        this.directory = directory + " Backup";
        File dir = new File(this.directory);
        if (!dir.exists()){
            dir.mkdirs();
        }
    }

     int num = 1;

    private static int extractNumber(String fileName) {
        Matcher matcher = Pattern.compile("backup(\\d+)\\.txt").matcher(fileName);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    Object restore(Object obj) throws IOException {
        String[] files = new File(directory).list();
        if (files == null || files.length == 0) {
            return null;
        }

        Arrays.sort(files, Comparator.comparingInt(Backup::extractNumber));

        String text = new String(Files.readAllBytes(Paths.get(directory + "/" + files[files.length - 1])));
        ObjectMapper o = new ObjectMapper();
        o.registerModule(new JavaTimeModule());

        obj = o.readValue(text, obj.getClass());
        return obj;
    }
    void initiateBackup(Object obj) throws IOException {
        FileWriter f = new FileWriter(directory + "/backup" + num++ +".txt");
        ObjectMapper o = new ObjectMapper();
        o.registerModule(new JavaTimeModule());
        o.enable(SerializationFeature.INDENT_OUTPUT);
        f.write(o.writeValueAsString(obj));
        f.close();

        if (num > 21) {
            destroyOldBackup();
        }
    }

    // destroy backup older than 20
    private void destroyOldBackup() {
        File f = new File(directory + "/backup" + (num - 20) + ".txt");
        f.delete();
    }

    // destroy backup directory
    void destroyBackup() {
        File dir = new File(directory);
        dir.delete();
    }

}
