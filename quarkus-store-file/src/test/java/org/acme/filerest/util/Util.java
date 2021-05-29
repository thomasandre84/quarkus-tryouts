package org.acme.filerest.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class Util {

    public static String fileLoc = "src/test/resources/test.txt";

    public static byte[] getFileData(String file) throws IOException {
        Path path = Path.of(file);
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    @Test
    void testReadFile() throws IOException {
        //System.out.println("File exists: "+ file.exists());
        byte[] data = getFileData(fileLoc);
        //String content = new String(data);
        //System.out.println("Data: " + content);
        assertNotNull(data);
    }
}
