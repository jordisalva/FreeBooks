package ioc.xtec.cat.freebooks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by jordi on 07/03/2018.
 */

public class ConnexioServidor {

    // Variable amb la resposta
    String resposta = "";

    /**
     * Retorna la resposta del servidor, després de passar-li les dades
     * com a paràmetre.
     *
     * @param dades String amb les dades que se li pasaran al servidor
     * @return String amb la resposta del servidor
     */
    public String consulta(String dades) {
        try{
            Socket socket = new Socket("10.0.2.2", 9999);
            try(BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
                escriptor.write(dades);
                escriptor.newLine();
                escriptor.flush();
                //Obté el resultat del server
                try (BufferedReader lector = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
                    resposta = lector.readLine();
                }

            }
            socket.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return resposta;
    }
}
