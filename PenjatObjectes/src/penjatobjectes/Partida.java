package penjatobjectes;

import java.util.Scanner;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class Partida extends Joc{
    
    public Partida(int acerts, int intents) {
        super(acerts, intents);
    }
    
    public void start (){
        Scanner in = new Scanner(System.in);
        
        boolean joc = true;
        
        //Do while del joc senser si es compleix la condició finalitza el joc
        do{
            //booleans dels do while de funcio del joc i de preguntar per jugar de nou
            boolean fin = false, altre = false;
            //Escull paraula random de l'array.
            String paraula = paraulaRandom(getParaules());
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
                
                if(getIntents() == 0){
                    System.out.println("Has perdut :( , la teva paraula era "+paraula);
                    fin = true;
                }
                else if(getAcerts() == ArraySep.length){
                    System.out.println("Has guanyat! :) , la paraula es "+paraula);
                    fin = true;
                }
                else{
                    System.out.println("Acerts: "+getAcerts()+" || Intents: "+getIntents());

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
                                    setAcerts(getAcerts()+1);
                                }
                            }
                            //Si no coincideix ninguna lletra et treu un intent.
                            if(!check){
                                setIntents(getIntents()-1);
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
                    setIntents(7);
                    setAcerts(0);
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
