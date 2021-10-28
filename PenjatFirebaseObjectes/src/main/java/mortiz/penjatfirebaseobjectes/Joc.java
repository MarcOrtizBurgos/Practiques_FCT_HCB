package mortiz.penjatfirebaseobjectes;

import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Marc Ortiz Burgos
 */
abstract class Joc {
    
    private int acerts;
    private int intents;
    private String[] paraules = new String[]{"llapis", "goma", "llibreta", "mestre", "examen", "mates"};

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

    public void jugar(){
        benvinguda();
        Partida partida = new Partida(getAcerts(),getIntents());
        partida.start();
    }
    
    public void benvinguda (){
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
    
    
    
}
