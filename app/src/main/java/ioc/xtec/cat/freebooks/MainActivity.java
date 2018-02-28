package ioc.xtec.cat.freebooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // Variables dels botons
    Button botoInici, botoAlta;

    // Variables dels TextViews
    TextView textUsuari, textContrasenya, missatge;

    //codi per identificar la sessió
    private String codiSessio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definim els listeners
        botoInici = ((Button)findViewById(R.id.buttonInici));
        botoInici.setOnClickListener(this);
        botoAlta = ((Button)findViewById(R.id.buttonAlta));
        botoAlta.setOnClickListener(this);

        textUsuari = ((TextView)findViewById(R.id.textUsuari));
        textContrasenya = ((TextView)findViewById(R.id.textContrasenya));
        missatge = ((TextView)findViewById(R.id.missatge));

    }

    @Override
    public void onClick(View v) {

        // Obté les dades de login introduïdes per l'usuari
        String usuariIntroduit = textUsuari.getText().toString();
        String passIntroduit = textContrasenya.getText().toString();

        //  Mirem en quin botó ha fet click l'usuari
        if(v == botoInici) {
            /*if(!usuariIntroduit.equals("") && !passIntroduit.equals("")) {
                missatge.setText("Has fet el login amb: " + usuariIntroduit + " / " + passIntroduit);
                Intent i = new Intent(this, PrincipalActivity.class);
                startActivity(i);
            } else {
                missatge.setText("Has d'introduir l'usuari i contrasenya");
            }*/

            //System.out.println(user.getText() + "\n" + pass.getText());
            final String codiRequest = "userLogin-"+usuariIntroduit +"-"+passIntroduit;
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        try{
                            Socket socket = new Socket("192.168.0.157", 9999);
                            try(BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
                                escriptor.write(codiRequest);
                                escriptor.newLine();
                                escriptor.flush();
                                //Obté el resultat del server
                                try (BufferedReader lector = new BufferedReader(
                                        new InputStreamReader(socket.getInputStream()))) {
                                    codiSessio = lector.readLine();
                                }

                            }
                            socket.close();
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            if(codiSessio.equals("FAIL")){
                Toast.makeText(this, "Username Or Password Are Invalid", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Username Or Password Are Valid", Toast.LENGTH_SHORT).show();
            }

        }

    }


}
