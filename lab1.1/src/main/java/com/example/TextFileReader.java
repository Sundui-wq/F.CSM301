package com.example;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class TextFileReader {
    public String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        } catch (IOException e) {
            System.err.println("file unshihad aldaa zaalaa " + e.getMessage());
            return "";
        }
    }
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    public boolean isEmptyFile(String filePath) {
        try {
            return Files.size(Paths.get(filePath)) == 0;
        } catch (IOException e) {
            return true;
        }
    }
    public static void main(String[] args) {
        TextFileReader reader = new TextFileReader();
        String filePath = args[0];
        if (!reader.fileExists(filePath)) {
            System.out.println("file oldsongui: " + filePath);
            return;
        }
        String content = reader.readFile(filePath);
        if (reader.isEmptyFile(filePath)) {
            System.out.println("file hooson baina: " + filePath);
        } else {
            System.out.println("unshsan file:");
            System.out.println(content);
        }
    }
}
// mvn compile
// mvn exec:java -D"exec.mainClass"="com.example.TextFileReader" -D"exec.args"="src/test/resources/test-file.txt"