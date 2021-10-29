package mortiz.penjatfirebaseobjectes;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Marc Ortiz Burgos
 */
public class PenjatObjectes {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        ConexioFirebase.conectar();
        Joc joc = new Joc(0,7) {};
        joc.jugar();
    }
    
}
