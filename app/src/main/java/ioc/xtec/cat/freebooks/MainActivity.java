package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";

    // Variables dels botons
    Button botoInici, botoAlta, botoSortir;

    // Variables dels TextViews
    TextView textUsuari, textContrasenya, missatge;

    // Codi per identificar la sessió
    private String codiSessio = "";

    /**
     * Accions en la creació
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definim els listeners
        botoInici = ((Button)findViewById(R.id.buttonIniciarSessio));
        botoInici.setOnClickListener(this);
        botoAlta = ((Button)findViewById(R.id.buttonDonarAlta));
        botoAlta.setOnClickListener(this);
        botoSortir = ((Button)findViewById(R.id.buttonSortir));
        botoSortir.setOnClickListener(this);

        textUsuari = ((TextView)findViewById(R.id.textUsuari));
        textContrasenya = ((TextView)findViewById(R.id.textContrasenya));
        missatge = ((TextView)findViewById(R.id.missatge));

    }


    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {

        //  Mirem en quin botó ha fet click l'usuari
        if(v == botoInici) {
            // Crida per iniciar la sessió
            iniciarSessio();
        } else if (v == botoAlta) {
            Intent i = new Intent(this, AltaUsuariActivity.class);
            startActivity(i);
            finish();
        } else if (v == botoSortir) {
            finish();
        }

    }

    /**
     * Envia les dades al servidor perqué aquest validi el login
     * Si es correcte inicia sessió, en cas contrari mostrarà missatges indicant l'error
     */
    public void iniciarSessio() {
        // Obté les dades de login introduïdes per l'usuari
        final String usuariIntroduit = textUsuari.getText().toString();
        final String passIntroduit = textContrasenya.getText().toString();

        final String codiRequest = "userLogin"+"Sep@!-@rad0R"+usuariIntroduit +"Sep@!-@rad0R"+passIntroduit+"Sep@!-@rad0R"+"Mobile";
        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        if (usuariIntroduit.equals("") || passIntroduit.equals("")) {
                            showToast("Falten dades");
                        } else {
                            ConnexioServidor connexioServidor = new ConnexioServidor();
                            codiSessio = connexioServidor.consulta(codiRequest);
                            if(codiSessio.equals("FAIL")){
                                showToast("Usuari o contrasenya invàlids");
                            }else if (codiSessio.startsWith("OK")){
                                String message = usuariIntroduit +"Sep@!-@rad0R"+"Mobile";
                                showToast("Usuari i contrasenya vàlids");
                                Intent i = new Intent(MainActivity.this, PrincipalActivity.class);
                                i.putExtra(EXTRA_MESSAGE,message);
                                startActivity(i);
                                finish();
                            }else{
                                showToast("El servidor no respon");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    /**
     * Crea un missatge tipus toast
     *
     * @param toast amb el text del missatge
     */
    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}