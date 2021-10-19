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
        String[] paraules = new String[]{"lapiz", "goma", "libreta", "maestro", "examen", "matematicas"};
        
        benvinguda();
        joc(acerts, intents,paraules);
        adeu();
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
        
        String paraula = paraulaRandom(paraules);
        
        String[] ArraySep = paraula.split("");
        String[] ArrayCon = new String[ArraySep.length];
        for (int i = 0; i < ArraySep.length; i++){
            ArrayCon[i] = "_";
        }
        
        while(intents != 0 || acerts == ArraySep.length){
            System.out.println("Acerts: "+acerts+" || Intents: "+intents);
            for (int i = 0; i < ArraySep.length; i++){
                if(ArrayCon[i] != ArraySep[i]){
                    System.out.print(ArrayCon[i]);
                }
                else{
                    System.out.print(ArraySep[i]);
                }
                System.out.print(" ");
            }
            
            System.out.println("\n");
            System.out.println("Escriu una lletra: ");
            String lletra = in.nextLine();
            
            for (int x = 0; x < ArraySep.length; x++){
                if(ArraySep[x].contains(lletra)){
                    ArrayCon[x] = lletra;
                }
            }
            
            if(ArrayCon.toString().contains(lletra)){
                acerts++;
            }
            else{
                intents--;
            }
            
        }
        
        if(intents == 0){
            System.out.println("Has perdut :( , la teva paraula era "+paraula);
        }
        else if(acerts == ArraySep.length){
            System.out.println("Has guanyat! :)");
        }
        
    }
    
    
    public static void adeu (){
        Scanner in = new Scanner(System.in);
        System.out.println(); System.out.println();
        System.out.print("Pulsa ENTER per terminar... ");
        in.nextLine();
    }
    
}
