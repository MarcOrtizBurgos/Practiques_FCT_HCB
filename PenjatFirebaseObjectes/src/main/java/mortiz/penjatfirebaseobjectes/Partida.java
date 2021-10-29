package mortiz.penjatfirebaseobjectes;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class Partida extends Joc{
    
    public Partida(int acerts, int intents) {
        super(acerts, intents);
    }
    
    public void start () throws InterruptedException, ExecutionException{
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
}
