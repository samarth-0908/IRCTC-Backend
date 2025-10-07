package com.substring.irctc.helper;

import java.util.UUID;

public class Helper {


    //logic to generate file name
    public static String getFileName(String folder, String originalFileName) {

        return folder+ UUID.randomUUID()+ "_" +originalFileName;

    }
}
