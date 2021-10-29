package mortiz.penjatfirebaseobjectes;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class ConexioFirebase {
    
    private static Firestore db;

    public static Firestore getDb() {
        return db;
    }

    public static void setDb(Firestore db) {
        ConexioFirebase.db = db;
    }

    public static void conectar() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("fir-penjat-firebase.json");
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        System.out.println("S'ha conectat amb exit");
    }
    
}
