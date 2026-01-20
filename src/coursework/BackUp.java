package coursework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class BackUp {
    private String p;

    public BackUp(String p) {
        this.p = p;
    }
    
     // Method to back up the data to a file
    public void backUp(String s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(p))) {
            bw.write(s);
            bw.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Method to check if the backup file exists
    public Boolean exists() {
        return new File(p).isFile();
    }
    
    // Method to restore the data from the backup file
    public String restore() {
        String s = "";

        try (BufferedReader br = new BufferedReader(new FileReader(p))){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            s = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }
    
    // Method to clear (delete) the backup file
    public void clearBackUp() {
        File backupFile = new File(p);
        if (backupFile.exists()) {
            boolean deleted = backupFile.delete(); // Deletes the backup file
            if (!deleted) {
                System.out.println("Failed to delete backup file.");
            } else {
                System.out.println("Backup file cleared successfully.");
            }
        }
    }
}