package mortiz.penjatfirebase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class ConexionFirebase {
    
    static Firestore db;
    
    public static void conectar() throws IOException{
        FileInputStream serviceAccount = new FileInputStream("fir-penjat-firebase.json");
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        System.out.println("S'ha conectat amb exit");
    }
    
    public static void main(String[] args) throws InterruptedException, ExecutionException{
        try{
            conectar();
        }catch(Exception e){
            System.out.println("Va haver-hi un error en la connexió");
        }
        
        primero();
        segundo();
        leer();
        
        
    }
    
    public static void primero()throws InterruptedException, ExecutionException{
        DocumentReference docRef = db.collection("users").document("alovelace");
        // Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        // ...
        // result.get() blocks on response
        System.out.println("Update time : " + result.get().getUpdateTime());
    }
    
    public static void segundo()throws InterruptedException, ExecutionException{
        DocumentReference docRef = db.collection("users").document("aturing");
        // Add document data with an additional field ("middle")
        Map<String, Object> data = new HashMap<>();
        data.put("first", "Alan");
        data.put("middle", "Mathison");
        data.put("last", "Turing");
        data.put("born", 1912);

        ApiFuture<WriteResult> result = docRef.set(data);
        System.out.println("Update time : " + result.get().getUpdateTime());
    }
    
    public static void leer() throws InterruptedException, ExecutionException{
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
          System.out.println("User: " + document.getId());
          System.out.println("First: " + document.getString("first"));
          if (document.contains("middle")) {
            System.out.println("Middle: " + document.getString("middle"));
          }
          System.out.println("Last: " + document.getString("last"));
          System.out.println("Born: " + document.getLong("born"));
        }
    }
}
