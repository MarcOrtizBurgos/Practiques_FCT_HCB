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
import com.google.firebase.database.DatabaseReference;
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

    public static void conectar() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("fir-penjat-firebase.json");
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        System.out.println("S'ha conectat amb exit");
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner in = new Scanner(System.in);
        try {
            conectar();
        } catch (Exception e) {
            System.out.println("Va haver-hi un error en la connexió");
        }
        benvinguda();
        joc();
    }

    public static void benvinguda() {
        Scanner in = new Scanner(System.in);
        System.out.println("Benvingut al penjat!");
        System.out.println("Pulsa ENTER per començar");
        in.nextLine();
    }

    public static void joc() throws InterruptedException, ExecutionException {
        Scanner in = new Scanner(System.in);
        boolean joc = true;

        String usuari = iniciUser();

        //Do while del joc senser si es compleix la condició finalitza el joc
        do {
            String nomp;
            nomp = comprovajoc(usuari);
            //booleans dels do while de funcio del joc i de preguntar per jugar de nou
            boolean fin = false, altre = false;
            //Escull paraula random de l'array.
            String paraula = (String) leerjoc(nomp).get("paraula").toString();
            //Array de les lletras de la paraula.
            String[] ArraySep = paraula.split("");
            //Array buit amb la longitud del primer array amb la paraula.
            String[] ArrayCon = new String[ArraySep.length];
            //String adivina amb les lletres trobades
            String adivina = (String) leerjoc(nomp).get("adivinar").toString();
            //For per posar amb _ l'array buida o les lletres que ja estan descobertes
            for (int i = 0; i < ArraySep.length; i++) {
                if (adivina.contains(ArraySep[i])) {
                    ArrayCon[i] = ArraySep[i];
                } else {
                    ArrayCon[i] = "_";
                }
            }

            //Do while per executar el joc
            do {
                boolean rep = false;
                //Si perds o guanyes s'acaba l'ha partida sino segueix el joc
                if ((int) leerjoc(nomp).get("intents").hashCode() == 0) {
                    System.out.println("Has perdut :( , la teva paraula era " + paraula);
                    finalitzat(nomp, "perdut");
                    fin = true;
                } else if ((int) leerjoc(nomp).get("acerts").hashCode() == ArraySep.length) {
                    System.out.println("Has guanyat! :) , la paraula es " + paraula);
                    finalitzat(nomp, "guanyat");
                    fin = true;
                } else {
                    System.out.println("Acerts: " + (int) leerjoc(nomp).get("acerts").hashCode() + " || Intents: " + (int) leerjoc(nomp).get("intents").hashCode());

                    //Imprimeix l'array amb les lletres ocultes.
                    for (int i = 0; i < ArraySep.length; i++) {
                        if (ArrayCon[i] != ArraySep[i]) {
                            System.out.print(ArrayCon[i]);
                        } else {
                            System.out.print(ArraySep[i]);
                        }
                        System.out.print(" ");
                    }

                    //Pregunta lletra.
                    System.out.println("\n");
                    System.out.println("Escriu una lletra: ");
                    String lletra = in.nextLine();

                    //Comproba que ya esta posada o no a l'array
                    for (int x = 0; x < ArrayCon.length; x++) {
                        if (ArrayCon[x].contains(lletra)) {
                            rep = true;
                        }
                    }

                    if (rep) {
                        System.out.println("Aquest caracter ja l'has posat");
                    } else {
                        //Si es una lletra segueix el joc sino es torna a preguntar.
                        if (!Character.isLetter(lletra.charAt(0))) {
                            System.out.println("Aquest caracter no es correcte.\n"
                                    + "Torna a probar.\n");
                        } else {
                            //Recorreix la array per comprobar si hi ha lletras que coincideixen.
                            boolean check = false;
                            for (int x = 0; x < ArraySep.length; x++) {
                                if (ArraySep[x].contains(lletra)) {
                                    ArrayCon[x] = lletra;
                                    check = true;
                                    update(nomp, (int) leerjoc(nomp).get("intents").hashCode(), (int) leerjoc(nomp).get("acerts").hashCode() + 1, adivina + lletra);
                                }
                            }
                            //Si no coincideix ninguna lletra et treu un intent.
                            if (!check) {
                                update(nomp, (int) leerjoc(nomp).get("intents").hashCode() - 1, (int) leerjoc(nomp).get("acerts").hashCode(), adivina + lletra);
                            }
                        }
                    }
                }
            } while (!fin);

            //Do while per preguntar si vols tornar a jugar.
            do {
                System.out.println("Vols jugar una altra partida? S / N");
                String resp = in.nextLine().toUpperCase();
                if (resp.equals("S")) {
                    altre = true;
                    System.out.println("\n\n\n\n\n");
                } else if (resp.equals("N")) {
                    altre = true;
                    joc = false;
                }

            } while (!altre);

        } while (joc);
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
        DocumentReference docRef = db.collection("jocs").document(nomp);
        Map<String, Object> data = new HashMap<>();
        data.put("state", true);
        data.put("estatjoc", estat);
        ApiFuture<WriteResult> result = docRef.update(data);
    }

    //Actualitza els resultats que hi ha al joc
    public static DocumentReference update(String id, int intents, int acerts, String adivina) throws InterruptedException, ExecutionException {
        DocumentReference docRef = db.collection("jocs").document(id);
        Map<String, Object> data = new HashMap<>();
        data.put("acerts", acerts);
        data.put("intents", intents);
        data.put("adivinar", adivina);
        ApiFuture<WriteResult> result = docRef.update(data);
        return docRef;
    }

    //Crea una nova partida a la base de dades
    public static void create(String id, String usuari) throws InterruptedException, ExecutionException {
        DocumentReference docRef = db.collection("jocs").document(id);
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
        ApiFuture<DocumentSnapshot> query = db.collection("jocs").document(id).get();
        DocumentSnapshot querySnapshot = query.get();
        return querySnapshot;
    }

    //Retorna el document que conte la informacio de la plantilla del joc
    public static DocumentSnapshot leerplantilla() throws InterruptedException, ExecutionException {
        ApiFuture<DocumentSnapshot> query = db.collection("plantilla").document("penjat").get();
        DocumentSnapshot querySnapshot = query.get();
        return querySnapshot;
    }

    //Retorna un boolea de si existeix la partida que busquem
    public static boolean leerpartidas(String nomp) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> query = db.collection("jocs").get();
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
        ApiFuture<QuerySnapshot> query = db.collection("jocs").get();
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
        DocumentReference docRef = db.collection("users").document(id);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("clau", clau);
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    //Retorna un boolea de si existeix o no l'usuari amb contrasenya indicat
    public static boolean loginUser(String id, String clau) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
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
