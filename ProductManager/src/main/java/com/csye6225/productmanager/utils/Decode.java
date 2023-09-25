package com.csye6225.productmanager.utils;

import java.util.Base64;

public class Decode {
    private static Base64.Decoder decoder = Base64.getDecoder();

    public String[] decodeAuth(String authString) {
        String decodedString = new String(decoder.decode(authString.substring(6)));

        int firstSemicolonIndex = decodedString.indexOf(':');
        String[] result = new String[2];
        result[0] = decodedString.substring(0, firstSemicolonIndex);
        result[1] = decodedString.substring(firstSemicolonIndex + 1);
        return result;
    }
}
