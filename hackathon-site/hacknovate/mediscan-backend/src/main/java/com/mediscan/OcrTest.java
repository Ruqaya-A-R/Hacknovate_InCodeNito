package com.mediscan;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;
public class OcrTest {
    public static void main(String[] args) {
        // 1. Path to your image
        File imageFile = new File("C:\\Users\\ruqar\\OneDrive\\Pictures\\Screenshots\\Screenshot (101).png");
        // 2. Configure Tesseract
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng");
        try {
            System.out.println("Image exists? " + imageFile.exists());
            System.out.println("Path I'm using: " + imageFile.getAbsolutePath());
            String text = tesseract.doOCR(imageFile);
            System.out.println("===== RAW OCR TEXT =====");
            System.out.println(text);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }
}
