package penjat;

import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author Marc Ortiz Burgos
 */
public class Penjat {
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int acerts = 0, intents = 7;
        String[] paraules = new String[]{"llapis", "goma", "llibreta", "mestre", "examen", "mates"};
        
        benvinguda();
        joc(acerts, intents,paraules);
    }
    
    public static void benvinguda (){
        Scanner in = new Scanner(System.in);
        System.out.println("Benvingut al penjat!");
        System.out.println("Pulsa ENTER per començar");
        in.nextLine();
    }
    
    public static String paraulaRandom (String[] paraules){
        Random rand = new Random();
        int val = rand.nextInt(6);
        return paraules[val];
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
    
}
