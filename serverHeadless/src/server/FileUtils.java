package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sam
 */
public class FileUtils {
    static List<String> readFromTextFile(String fileName) throws IOException {
        List<String> fileContents = new ArrayList<>();
        // FileReader reads text files in the default encoding. 
		// To specify encoding you need to use new InputStreamReader(new FileInputStream(pathToFile), encoding). For standard Turkish text files, enconding is "ISO-8859-9".
        FileReader fileReader = new FileReader(fileName);
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContents.add(line);
            }
        }
        return fileContents;
    }

    static void writeToTextFile(String fileName, List<String> fileContents) throws IOException {
        // Assume default encoding.
        FileWriter fileWriter = new FileWriter(fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (String string : fileContents) {
                bufferedWriter.write(string);
                // Note that write() does not automatically append a newline character.
                bufferedWriter.newLine();
            }
        }
    }
}
