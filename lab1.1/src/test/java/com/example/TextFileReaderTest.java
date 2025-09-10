package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TextFileReaderTest {
    private TextFileReader textFileReader;
    private String filePath;

    @BeforeEach
    void setUp() {
        textFileReader = new TextFileReader();
        filePath = System.getProperty("filePath");

    }
    @Test
    void testReadFile() {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("file baihgui baina: " + filePath);
            return;
        }
        String content = textFileReader.readFile(filePath);
        if (content.isEmpty()) {
            System.out.println("file hooson baina: " + filePath);
        } else {
            System.out.println("unshsan file:\n" + content);
        }
        assertNotNull(content);
    }
}
// mvn test -Dtest=TextFileReaderTest -DfilePath="src/test/resources/hooson-file.txt"
//mvn compile