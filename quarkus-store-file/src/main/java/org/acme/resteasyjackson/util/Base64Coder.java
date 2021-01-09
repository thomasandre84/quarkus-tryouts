package org.acme.resteasyjackson.util;


import java.util.Base64;

public class Base64Coder {
    private Base64Coder(){}

    public static String encodeB64(byte[] filecontent) {
        Base64.Encoder encoder = Base64.getEncoder();
        String content = encoder.encodeToString(filecontent);

        return content;
    }

    public static byte[] decodeToByteArray(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] data = decoder.decode(base64);
        return data;
    }
}
