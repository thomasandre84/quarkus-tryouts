package org.acme.resteasyjackson.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class Util {

    public static byte[] getFileData(String fileName) throws IOException {
        URI uri = URI.create(fileName);
        Path path = Path.of(uri);
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    @Test
    void testReadFile() throws IOException {
        String file = "";
        byte[] data = getFileData(file);
        assertNotNull(data);
    }
}
