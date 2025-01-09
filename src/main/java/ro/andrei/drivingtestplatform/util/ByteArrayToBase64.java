package ro.andrei.drivingtestplatform.util;

import java.util.Base64;

public class ByteArrayToBase64 {
    public static String convertByteArrayToBase64(byte[] image) {
        if (image == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(image);
    }

    public static byte[] convertBase64ToByteArray(String imageBase64) {
        if (imageBase64 == null) {
            return null;
        }
        return Base64.getDecoder().decode(imageBase64);
    }
}
