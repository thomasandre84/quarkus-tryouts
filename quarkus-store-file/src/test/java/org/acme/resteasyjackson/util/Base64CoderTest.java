package org.acme.resteasyjackson.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Base64CoderTest {

    @Test
    void encodeB64() throws IOException {
        byte[] data = Util.getFileData(Util.fileLoc);
        String base64 = Base64Coder.encodeB64(data);
        System.out.println("Base64 String: " + base64);
        assertNotNull(base64);
        //assertTrue(base64.endsWith("PRgo="));
    }

    @Test
    void decodeToByteArray() throws IOException {
        byte[] data = Util.getFileData(Util.fileLoc);
        String base64 = Base64Coder.encodeB64(data);
        byte[] dataRevert = Base64Coder.decodeToByteArray(base64);
        assertEquals(data[0], dataRevert[0]);
        assertEquals(data.length, dataRevert.length);
        assertEquals(data[data.length-1], dataRevert[dataRevert.length-1]);
    }
}