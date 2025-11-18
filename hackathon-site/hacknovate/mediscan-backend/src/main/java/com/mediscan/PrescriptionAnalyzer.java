package com.mediscan;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrescriptionAnalyzer {

    // 👇 your image path
    private static final String IMAGE_PATH =
            "C:\\Users\\ruqar\\OneDrive\\Pictures\\Screenshots\\Screenshot (101).png";

    // 👇 tessdata path
    private static final String TESSDATA_PATH =
            "C:/Program Files/Tesseract-OCR/tessdata";

    // 👇 MongoDB Atlas URI
    private static final String MONGO_URI =
            "mongodb+srv://ruqar201_db_user:hacknovate@meds.fcfdvlb.mongodb.net/?appName=Meds";

    public static void main(String[] args) {

        // (logs will still show, that’s ok)
        // System.setProperty("org.slf4j.simpleLogger.log.org.mongodb.driver", "ERROR");

        // 1️⃣ Run OCR on the prescription image
        String ocrText = runOcr();

        System.out.println("===== RAW OCR TEXT (SHORTENED) =====");
        System.out.println(ocrText.substring(0, Math.min(200, ocrText.length())) + "...");
        System.out.println();

        // 2️⃣ Connect to Mongo and build JSON result
        try (MongoClient client = MongoClients.create(MONGO_URI)) {

            MongoDatabase db = client.getDatabase("Mediscan");      // EXACT case
            MongoCollection<Document> medsCollection = db.getCollection("Medicines");

            String lowerOcr = ocrText.toLowerCase();
            Set<String> matchedNames = new HashSet<>();

            // find which medicines appear in OCR text
            for (Document doc : medsCollection.find()) {
                String name = doc.getString("name");
                if (name == null) continue;

                if (lowerOcr.contains(name.toLowerCase())) {
                    matchedNames.add(name);
                }
            }

            // build a list of medicine documents for JSON output
            List<Document> resultMeds = new ArrayList<>();

            for (String medName : matchedNames) {
                Document medDoc = medsCollection.find(Filters.eq("name", medName)).first();
                if (medDoc != null) {
                    // keep only the useful fields (no _id)
                    Document cleaned = new Document()
                            .append("name", medDoc.getString("name"))
                            .append("purpose", medDoc.getString("purpose"))
                            .append("dosage", medDoc.getString("dosage"))
                            .append("timing", medDoc.getString("timing"))
                            .append("sideEffects", medDoc.getString("sideEffects"))
                            .append("warnings", medDoc.getString("warnings"));

                    resultMeds.add(cleaned);
                }
            }

            // wrap everything into one result JSON
            Document resultJson = new Document()
                    .append("medicines", resultMeds)
                    .append("rawTextPreview",
                            ocrText.substring(0, Math.min(200, ocrText.length())));

            System.out.println("===== FINAL JSON RESULT =====");
            System.out.println(resultJson.toJson());    // <- your teammates can use this

            // OPTIONAL: save this analysis into a new collection
            MongoCollection<Document> history = db.getCollection("AnalyzedPrescriptions");
            history.insertOne(resultJson);
            System.out.println("Saved analysis to Mediscan.AnalyzedPrescriptions ✔");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper: Run OCR and return text
    private static String runOcr() {
        File imageFile = new File(IMAGE_PATH);

        System.out.println("Image exists? " + imageFile.exists());
        System.out.println("Using image path: " + imageFile.getAbsolutePath());
        System.out.println();

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PATH);
        tesseract.setLanguage("eng");

        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            throw new RuntimeException("OCR failed", e);
        }
    }
}


