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

    /**
     * Creates a new backup directory if it does not exist
     * @param directory The directory to create the backup in
     */

    Backup(String directory) {
        this.directory = directory + " Backup";
        File dir = new File(this.directory);
        if (!dir.exists()){
            dir.mkdirs();
        }
    }

     int num = 1;

    /**
     * Extracts the number from the backup file name
     * @param fileName The name of the backup file
     * @return The number in the file name
     */
    private static int extractNumber(String fileName) {
        Matcher matcher = Pattern.compile("backup(\\d+)\\.json").matcher(fileName);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    /**
     * Restores the latest backup (the one with the highest number)
     * @param obj The object to restore the backup to (Used to get the class of different object, i.e., AggregationServer or ContentServer)
     * @return The restored object
     * @throws IOException
     */
    Object restore(Object obj) throws IOException {
        String[] files = new File(directory).list();
        if (files == null || files.length == 0) {
            return null;
        }

        // Sort the files by the number in the file name
        Arrays.sort(files, Comparator.comparingInt(Backup::extractNumber));

        // Read the latest file
        String text = new String(Files.readAllBytes(Paths.get(directory + "/" + files[files.length - 1])));

        ObjectMapper o = new ObjectMapper();
        o.registerModule(new JavaTimeModule());

        // Deserialize the file into the object
        obj = o.readValue(text, obj.getClass());
        return obj;
    }

    /**
     * Creates a new backup file
     * @param obj The object to be backup (Used to get the class of different object, i.e., AggregationServer or ContentServer)
     * @throws IOException
     */
    void initiateBackup(Object obj) throws IOException {
        FileWriter f = new FileWriter(directory + "/backup" + num++ +".json");
        ObjectMapper o = new ObjectMapper();
        o.registerModule(new JavaTimeModule());
        o.enable(SerializationFeature.INDENT_OUTPUT);
        // Serialize the object into the file
        f.write(o.writeValueAsString(obj));
        f.close();

        // If there are more than 20 backup files, delete the oldest one
        if (num > 21) {
            destroyOldBackup();
        }
    }


    /**
     * Deletes the oldest backup file
     */
    private void destroyOldBackup() {
        File f = new File(directory + "/backup" + (num - 20) + ".json");
        f.delete();
    }

    /**
     * Deletes all backup files and the backup directory
     */
    void destroyBackup() {
        File dir = new File(directory);
        for (File file : dir.listFiles()) {
            file.delete();
        }
        dir.delete();
    }

}
