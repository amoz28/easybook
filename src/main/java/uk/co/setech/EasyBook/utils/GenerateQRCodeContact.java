//package uk.co.setech.EasyBook;
//
//import java.io.File;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//
//public class GenerateQRCodeContact {
//
//    public static void main(String[] args) {
//        // Create a map with 6 items
//        Map<String, Integer> map = new HashMap<>();
//        map.put("One", 1);
//        map.put("Two", 2);
//        map.put("Three", 3);
//        map.put("Four", 4);
//        map.put("Five", 5);
//        map.put("Six", 6);
//
//        // Loop through the map using a for-each loop
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            String key = entry.getKey();
//            Integer value = entry.getValue();
//            System.out.println("Key: " + key + ", Value: " + value);
//        }
//    }
////    public static void main(String[] args) {
////
////        // Define the contact information to be encoded
////        String name = "Oyindamola";
////        String phone = "+4478 1026 8829";
////        String email = "oyindamola.oluwole@gmail.com";
////        String website = "http://example.com";
////        String address = "123 Main St, Anytown, USA";
////        String linkedin = "https://www.linkedin.com/in/johndoe";
////        String facebook = "https://www.facebook.com/johndoe";
////        String twitter = "https://www.twitter.com/johndoe";
////        // Define the QR code image size
////        int width = 300;
////        int height = 300;
////
////        // Define the QR code content
////        String content = "BEGIN:VCARD\n" +
////                "VERSION:3.0\n" +
////                "N:" + name + "\n" +
////                "TEL:" + phone + "\n" +
////                "EMAIL:" + email + "\n" +
////                "homepage:" + email + "\n" +
////                "address:" + email + "\n" +
////                "URL:" + website + "\n" +
////                "ADR:" + address + "\n" +
////                "X-SOCIALPROFILE;type=linkedin:" + linkedin + "\n" +
////                "X-SOCIALPROFILE;type=facebook:" + facebook + "\n" +
////                "X-SOCIALPROFILE;type=twitter:" + twitter + "\n" +
////                "END:VCARD";
////
////        // Set the QR code encoding parameters
////        Map<EncodeHintType, Object> hints = new HashMap<>();
////        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
////
////        try {
////            // Generate the QR code bit matrix
////            QRCodeWriter qrCodeWriter = new QRCodeWriter();
////            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
////
////            System.out.println(bitMatrix);
////            // Define the output file path and format
////            Path path = FileSystems.getDefault().getPath("qrcode.png");
////            String format = "png";
////
////            // Write the QR code bit matrix to an image file
////            MatrixToImageWriter.writeToPath(bitMatrix, format, path);
////
////            System.out.println("QR code generated successfully!");
////        } catch (WriterException e) {
////            System.err.println("Failed to generate QR code: " + e.getMessage());
////        } catch (Exception e) {
////            System.err.println("An error occurred: " + e.getMessage());
////        }
////    }
//}
