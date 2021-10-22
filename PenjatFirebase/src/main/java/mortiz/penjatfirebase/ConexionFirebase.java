package mortiz.penjatfirebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class ConexionFirebase {
    
    static Firestore bd;
    
    public static void conectar() throws IOException{
        FileInputStream serviceAccount = new FileInputStream("fir-penjat-firebase.json");
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        FirebaseApp.initializeApp(options);
        bd = FirestoreClient.getFirestore();
        System.out.println("S'ha conectat amb exit");
    }
    
    public static void main(String[] args){
        try{
            conectar();
        }catch(Exception e){
            System.out.println("Va haver-hi un error en la connexió");
        }
    }
}
