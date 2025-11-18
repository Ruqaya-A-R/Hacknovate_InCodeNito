package com.mediscan;
import com.mongodb.client.*;
import org.bson.Document;
public class DbTest {
    private static final String MONGO_URI =
            "mongodb+srv://ruqar201_db_user:hacknovate@meds.fcfdvlb.mongodb.net/?appName=Meds";

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(MONGO_URI)) {
            MongoDatabase db = client.getDatabase("Mediscan");
            MongoCollection<Document> meds = db.getCollection("Medicines");
            System.out.println("===== MEDICINES IN DB =====");
            int count = 0;
            for (Document doc : meds.find()) {
                count++;
                System.out.println("#" + count + " -> " + doc.toJson());
            }
            System.out.println("Total medicines found: " + count);
        }
    }
}


