package mortiz.penjatfirebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class ConexionFirebase {
    public static void conectar() throws IOException{
        FileInputStream serviceAccount = new FileInputStream("path/to/fir-penjat-firebase.json");
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        FirebaseApp.initializeApp(options);
    }
}
