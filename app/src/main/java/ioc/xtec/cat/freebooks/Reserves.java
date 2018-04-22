package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static ioc.xtec.cat.freebooks.CriptoUtils.encriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.passwordKeyGeneration;

public class Reserves extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";
    public static final String SEPARADOR = "Sep@!-@rad0R";
    public static final String ALGORISME = "AES/ECB/PKCS5Padding";

    // Variable amb l'intent
    Intent i;

    Button btnEditaReserva;
    ImageButton btnAnulaReserva;
    Button btnTornarReserves;
    ImageView imatgeReserves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserves);

        Intent iFinalitza = new Intent("finish");
        sendBroadcast(iFinalitza);

        imatgeReserves = ((ImageView) findViewById(R.id.imageReserves));
        imatgeReserves.setColorFilter(Color.GRAY);
        // Definim els listeners
        btnTornarReserves = ((Button) findViewById(R.id.btnTornarReserves));
        btnTornarReserves.setOnClickListener(this);
        btnAnulaReserva = (ImageButton)findViewById(R.id.buttonAnularReserva1);
        btnAnulaReserva.setOnClickListener(this);
        // Crea un intent amb la pantalla de login
        i = new Intent(Reserves.this, PrincipalActivity.class);
    }

    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {
        if (v == btnEditaReserva) {
            // TODO Ha de poder editar la data de la reserva
        } else if (v == btnAnulaReserva) {
            // TODO Ha d'anular la reserva
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
                    String extras = getIntent().getStringExtra(EXTRA_MESSAGE);
                    String checkLogin = "userIsLogged" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[1];
                    String codiRequestXifrat = "";
                    ConnexioServidor connexioServidor = new ConnexioServidor(getApplicationContext());
                    try {
                        codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec) sKey, ALGORISME);
                    } catch (Exception ex) {
                        System.err.println("Error al encriptar: " + ex);
                    }
                    String resposta = connexioServidor.consulta(codiRequestXifrat);
                    if (resposta.equals("OK")) {
                        String removeReservation = "removeReserva" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[2];
                        try {
                            codiRequestXifrat = encriptaDades(removeReservation, (SecretKeySpec) sKey, ALGORISME);
                        } catch (Exception ex) {
                            System.err.println("Error al encriptar: " + ex);
                        }
                        resposta = connexioServidor.consulta(codiRequestXifrat);
                    } else {
                        //TODO resposta si l'usuari no està logat
                    }

                }
            });
            thread.start();

        } else if (v == btnTornarReserves) {
            // Torna a la pantalla principal
            String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
            i.putExtra(EXTRA_MESSAGE, extra);
            startActivity(i);
            finish();
        }
    }

    /**
     * Crea un missatge tipus toast
     *
     * @param toast amb el text del missatge
     */
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(Reserves.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Torna a la pantalla de login en cas de prémer el botó "Back"
     */
    @Override
    public void onBackPressed() {
        // Torna a la pantalla principal
        String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
        i.putExtra(EXTRA_MESSAGE, extra);
        startActivity(i);
        finish();
    }
}
