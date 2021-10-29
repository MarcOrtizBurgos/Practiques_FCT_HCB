package mortiz.penjatfirebaseobjectes;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Marc Ortiz Burgos
 */
abstract class Joc {
    
    private int acerts;
    private int intents;
    private String[] paraules ;

    public Joc(int acerts, int intents) {
        this.acerts = acerts;
        this.intents = intents;
    }

    public int getAcerts() {
        return acerts;
    }

    public int getIntents() {
        return intents;
    }

    public String[] getParaules() {
        return paraules;
    }

    public void setAcerts(int acerts) {
        this.acerts = acerts;
    }

    public void setIntents(int intents) {
        this.intents = intents;
    }

    public void setParaules(String[] paraules) {
        this.paraules = paraules;
    }

    public void jugar() throws InterruptedException, ExecutionException{
        benvinguda();
        Partida partida = new Partida(getAcerts(),getIntents());
        partida.start();
    }
    
    public static void benvinguda (){
        Scanner in = new Scanner(System.in);
        System.out.println("Benvingut al penjat!");
        System.out.println("Pulsa ENTER per començar");
        in.nextLine();
    }
    
    //Busca una paraula random de l'array
    public static String paraulaRandom(String paraules) {
        String replace = paraules.replace("[", "");
        String replace1 = replace.replace("]", "");
        ArrayList<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(", ")));
        Random rand = new Random();
        int val = rand.nextInt(6);
        return myList.get(val);
    }

    //Finalitza la partida a la base de dades
    public static void finalitzat(String nomp, String estat) {
        DocumentReference docRef = ConexioFirebase.getDb().collection("jocs").document(nomp);
        Map<String, Object> data = new HashMap<>();
        data.put("state", true);
        data.put("estatjoc", estat);
        ApiFuture<WriteResult> result = docRef.update(data);
    }

    //Actualitza els resultats que hi ha al joc
    public static DocumentReference update(String id, int intents, int acerts, String adivina) throws InterruptedException, ExecutionException {
        DocumentReference docRef = ConexioFirebase.getDb().collection("jocs").document(id);
        Map<String, Object> data = new HashMap<>();
        data.put("acerts", acerts);
        data.put("intents", intents);
        data.put("adivinar", adivina);
        ApiFuture<WriteResult> result = docRef.update(data);
        return docRef;
    }

    //Crea una nova partida a la base de dades
    public static void create(String id, String usuari) throws InterruptedException, ExecutionException {
        DocumentReference docRef = ConexioFirebase.getDb().collection("jocs").document(id);
        Map<String, Object> data = new HashMap<>();
        data.put("acerts", leerplantilla().get("acerts"));
        data.put("adivinar", "");
        data.put("intents", leerplantilla().get("intents"));
        data.put("paraula", paraulaRandom(leerplantilla().get("paraules").toString()));
        data.put("state", false);
        data.put("estatjoc", "");
        data.put("usuari", usuari);
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    //Retorna el document que conte la informacio de la partida
    public static DocumentSnapshot leerjoc(String id) throws InterruptedException, ExecutionException {
        ApiFuture<DocumentSnapshot> query = ConexioFirebase.getDb().collection("jocs").document(id).get();
        DocumentSnapshot querySnapshot = query.get();
        return querySnapshot;
    }

    //Retorna el document que conte la informacio de la plantilla del joc
    public static DocumentSnapshot leerplantilla() throws InterruptedException, ExecutionException {
        ApiFuture<DocumentSnapshot> query = ConexioFirebase.getDb().collection("plantilla").document("penjat").get();
        DocumentSnapshot querySnapshot = query.get();
        return querySnapshot;
    }

    //Retorna un boolea de si existeix la partida que busquem
    public static boolean leerpartidas(String nomp) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> query = ConexioFirebase.getDb().collection("jocs").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        boolean existe = false;
        for (QueryDocumentSnapshot document : documents) {
            if (nomp.equals(document.getId())) {
                existe = true;
            }
        }
        return existe;
    }

    //Retorna un boolea de si existeix o ja esta finalitzada una partida
    public static boolean leerpartidasfetes(String nomp) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> query = ConexioFirebase.getDb().collection("jocs").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        boolean existe = false;
        for (QueryDocumentSnapshot document : documents) {
            if (nomp.equals(document.getId()) && document.get("state").equals(true)) {
                existe = true;
            }
        }
        return existe;
    }

    //Selecciona si vols crear una partida nova o começada i retorna el nom de la partida
    public static String comprovajoc(String usuari) throws InterruptedException, ExecutionException {
        Scanner in = new Scanner(System.in);
        System.out.println("Escull:\n"
                + "0 - Partida nova\n"
                + "1 - Partida a mitjes\n");
        String select = in.nextLine();
        String nomp = null;
        switch (select) {
            case "0":
                boolean exit = false;
                do {
                    System.out.println("Escriu com vols que es digui la teva partida: ");
                    nomp = in.nextLine();
                    if (leerpartidas(nomp)) {
                        System.out.println("Aquesta partida ja existeix torna a provar.");
                    } else {
                        create(nomp, usuari);
                        exit = true;
                    }
                } while (!exit);
                break;
            case "1":
                boolean exit1 = false;
                do {
                    System.out.println("Escriu la partida que vols reprende: ");
                    nomp = in.nextLine();
                    if (leerpartidasfetes(nomp)) {
                        System.out.println("Aquesta partida ja esta finalitzada o no existeix torna a provar.");
                    } else {
                        exit1 = true;
                    }
                } while (!exit1);
                break;
            default:
                break;
        }
        return nomp;
    }

    //Creació del usuari
    public static void createUser(String id, String clau) throws InterruptedException, ExecutionException {
        DocumentReference docRef = ConexioFirebase.getDb().collection("users").document(id);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("clau", clau);
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    //Retorna un boolea de si existeix o no l'usuari amb contrasenya indicat
    public static boolean loginUser(String id, String clau) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> query = ConexioFirebase.getDb().collection("users").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        boolean existe = false;
        for (QueryDocumentSnapshot document : documents) {
            if (document.get("id").equals(id) && document.get("clau").equals(clau)) {
                existe = true;
            }
        }
        return existe;
    }

    //Seleccionar per iniciar o registrar usuari i retorna un string amb el nom de l'usuari
    public static String iniciUser() throws InterruptedException, ExecutionException {
        Scanner in = new Scanner(System.in);
        System.out.println("Escull:\n"
                + "0 - Iniciar Sessió\n"
                + "1 - Registrarse\n");
        String select = in.nextLine();
        String usuari = null;
        String clau = null;
        switch (select) {
            case "0":
                boolean exit = false;
                do {
                    System.out.println("Usuari: ");
                    usuari = in.nextLine();
                    System.out.println("Clau: ");
                    clau = in.nextLine();
                    if (!loginUser(usuari, clau)) {
                        System.out.println("La clau o l'usuari son incorrectes torna a provar.");
                    } else {
                        System.out.println("Hola " + usuari);
                        exit = true;
                    }
                } while (!exit);
                break;
            case "1":
                System.out.println("Escriu el nom d'usuari: ");
                usuari = in.nextLine();
                System.out.println("Clau: ");
                clau = in.nextLine();
                createUser(usuari, clau);
                System.out.println("Hola " + usuari);
                break;
            default:
                break;
        }
        return usuari;
    }
    
    
    
}
