package mortiz.penjatfirebase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import jdk.nashorn.internal.runtime.JSType;


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
        
        System.out.println(leerplantilla().get("paraules"));
        //leerjocs();
        //leerplantilla();
        System.out.println(paraulaRandom(leerplantilla().get("paraules").toString()));
    }
    
    
    public static String paraulaRandom (String paraules){
        
        String replace = paraules.replace("[","");
        String replace1 = replace.replace("]","");
        ArrayList<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(", ")));
        
        Random rand = new Random();
        int val = rand.nextInt(6);
        return myList.get(val);
    }
    
    
    public static void joc (int acerts, int intents, String[] paraules){
        Scanner in = new Scanner(System.in);
        
        boolean joc = true;
        
        //Do while del joc senser si es compleix la condició finalitza el joc
        do{
            //booleans dels do while de funcio del joc i de preguntar per jugar de nou
            boolean fin = false, altre = false;
            //Escull paraula random de l'array.
            String paraula = paraulaRandom(paraules);
            //Array de les lletras de la paraula.
            String[] ArraySep = paraula.split("");
            //Array buit amb la longitud del primer array amb la paraula.
            String[] ArrayCon = new String[ArraySep.length];

            //For per posar amb _ l'array buida
            for (int i = 0; i < ArraySep.length; i++){
                ArrayCon[i] = "_";
            }

            //Do while per executar el joc
            do{
                
                boolean rep = false;
                
                if(intents == 0){
                    System.out.println("Has perdut :( , la teva paraula era "+paraula);
                    fin = true;
                }
                else if(acerts == ArraySep.length){
                    System.out.println("Has guanyat! :) , la paraula es "+paraula);
                    fin = true;
                }
                else{
                    System.out.println("Acerts: "+acerts+" || Intents: "+intents);

                    //Imprimeix l'array amb les lletres ocultes.
                    for (int i = 0; i < ArraySep.length; i++){
                        if(ArrayCon[i] != ArraySep[i]){
                            System.out.print(ArrayCon[i]);
                        }
                        else{
                            System.out.print(ArraySep[i]);
                        }
                        System.out.print(" ");
                    }

                    //Pregunta lletra.
                    System.out.println("\n");
                    System.out.println("Escriu una lletra: ");
                    String lletra = in.nextLine();
                    
                    //Comproba que ya esta posada o no a l'array
                    for (int x = 0; x < ArrayCon.length; x++){
                        if(ArrayCon[x].contains(lletra)){
                            rep = true;
                        }
                    }
                    
                    if(rep){
                        System.out.println("Aquest caracter ja l'has posat");
                    }
                    else{
                        //Si es una lletra segueix el joc sino es torna a preguntar.
                        if(!Character.isLetter(lletra.charAt(0))){
                            System.out.println("Aquest caracter no es correcte.\n"
                                             + "Torna a probar.\n");
                        }
                        else{
                            //Recorreix la array per comprobar si hi ha lletras que coincideixen.
                            boolean check = false;
                            for (int x = 0; x < ArraySep.length; x++){
                                if(ArraySep[x].contains(lletra)){
                                    ArrayCon[x] = lletra;
                                    check = true;
                                    acerts++;
                                }
                            }
                            //Si no coincideix ninguna lletra et treu un intent.
                            if(!check){
                                intents--;
                            }
                        }
                    }
                }
            }while(!fin);
            
            //Do while per preguntar si vols tornar a jugar.
            do{
                System.out.println("Vols tornar a jugar? S / N");
                String resp = in.nextLine().toUpperCase();
                if(resp.equals("S")){
                    intents = 7;
                    acerts = 0;
                    altre = true;
                    System.out.println("\n\n\n\n\n");
                }
                else if(resp.equals("N")){
                    altre = true;
                    joc = false;
                }
                
            }while(!altre);
            
        }while(joc);
    }
    
    public static void create()throws InterruptedException, ExecutionException{
        CollectionReference docRef = db.collection("jocs");
        
        Map<String, Object> data = new HashMap<>();
        data.put("acerts", leerplantilla().get("acerts"));
        data.put("adivinar", "");
        data.put("intents", leerplantilla().get("intents"));
        data.put("paraula", paraulaRandom(leerplantilla().get("paraules").toString()));

        ApiFuture<DocumentReference> result = docRef.add(data);
        System.out.println("Update time : " + result.get());
    }
    
    
    public static void leerjocs() throws InterruptedException, ExecutionException{
        
        ApiFuture<QuerySnapshot> query = db.collection("jocs").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        
        for (QueryDocumentSnapshot document : documents) {
          System.out.println("Acerts: " + document.getDouble("acerts"));
          System.out.println("Adivinar: " + document.getString("adivinar"));
          System.out.println("Intents: " + document.getDouble("intents"));
          System.out.println("Paraula: " + document.getString("paraula"));
        }
    }
    
    public static DocumentSnapshot leerplantilla() throws InterruptedException, ExecutionException{
        
        ApiFuture<DocumentSnapshot> query = db.collection("plantilla").document("penjat").get();
        DocumentSnapshot querySnapshot = query.get();
        
        //System.out.println("Acerts: " + querySnapshot.get("acerts"));
        //System.out.println("Adivinar: " + querySnapshot.get("intents"));
        //System.out.println("Paraula: " + querySnapshot.get("paraules"));
        //System.out.println(querySnapshot.get("paraules"));
        
        //System.out.println(paraulaRandom(querySnapshot.get("paraules")));
        
        return querySnapshot;
    }
}
