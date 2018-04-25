package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static ioc.xtec.cat.freebooks.CriptoUtils.desencriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.encriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.passwordKeyGeneration;

public class Reserves extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";
    public static final String SEPARADOR = "Sep@!-@rad0R";
    public static final String ALGORISME = "AES/ECB/PKCS5Padding";

    // Variable amb l'intent
    Intent i;

    Button btnTornarReserves;
    ImageView imatgeReserves;

    ImageButton btnEditaReserva1;
    ImageButton btnAnulaReserva1;
    ImageView thumbnailReserva1;
    TextView titleReserva1;
    TextView autorReserva1;
    TextView dataReserva1;
    TextView dataReservaLabel1;

    ImageButton btnEditaReserva;
    ImageButton btnAnulaReserva;
    ImageView thumbnailReserva;
    TextView titleReserva;
    TextView autorReserva;
    TextView dataReserva;
    TextView dataReservaLabel;


    String llistaReserves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserves);

        Intent iFinalitza = new Intent("finish");
        sendBroadcast(iFinalitza);

        imatgeReserves = ((ImageView) findViewById(R.id.imageReserves));
        imatgeReserves.setColorFilter(Color.GRAY);
        thumbnailReserva1 = ((ImageView) findViewById(R.id.thumbnailReserva1));
        titleReserva1 = ((TextView) findViewById(R.id.titleReserva1));
        autorReserva1 = ((TextView) findViewById(R.id.autorReserva1));
        dataReserva1 = ((TextView) findViewById(R.id.dataReserva1));
        dataReservaLabel1 = ((TextView) findViewById(R.id.dataReservaLabel1));
        thumbnailReserva = ((ImageView) findViewById(R.id.thumbnailReserva));
        titleReserva = ((TextView) findViewById(R.id.titleReserva));
        autorReserva = ((TextView) findViewById(R.id.autorReserva));
        dataReserva = ((TextView) findViewById(R.id.dataReserva));
        dataReservaLabel = ((TextView) findViewById(R.id.dataReservaLabel));
        // Definim els listeners
        btnTornarReserves = ((Button) findViewById(R.id.btnTornarReserves));
        btnTornarReserves.setOnClickListener(this);
        btnEditaReserva1 = (ImageButton) findViewById(R.id.buttonEditarReserva1);
        btnEditaReserva1.setOnClickListener(this);
        btnAnulaReserva1 = (ImageButton) findViewById(R.id.buttonAnularReserva1);
        btnAnulaReserva1.setOnClickListener(this);
        btnEditaReserva = (ImageButton) findViewById(R.id.buttonEditarReserva);
        btnEditaReserva.setOnClickListener(this);
        btnAnulaReserva = (ImageButton) findViewById(R.id.buttonAnularReserva);
        btnAnulaReserva.setOnClickListener(this);


        // Inicialment ocultem totes les reserves
        thumbnailReserva.setVisibility(View.GONE);
        titleReserva.setVisibility(View.GONE);
        autorReserva.setVisibility(View.GONE);
        dataReserva.setVisibility(View.GONE);
        dataReservaLabel.setVisibility(View.GONE);
        btnEditaReserva.setVisibility(View.GONE);
        btnAnulaReserva.setVisibility(View.GONE);
        thumbnailReserva1.setVisibility(View.GONE);
        titleReserva1.setVisibility(View.GONE);
        autorReserva1.setVisibility(View.GONE);
        dataReserva1.setVisibility(View.GONE);
        dataReservaLabel1.setVisibility(View.GONE);
        btnEditaReserva1.setVisibility(View.GONE);
        btnAnulaReserva1.setVisibility(View.GONE);

        // Crea un intent amb la pantalla de login
        i = new Intent(Reserves.this, PrincipalActivity.class);

        // TODO Ha de mostrar la llista de reserves de l'usuari
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
                    String getReserves = "getReservations";
                    try {
                        codiRequestXifrat = encriptaDades(getReserves, (SecretKeySpec) sKey, ALGORISME);
                        llistaReserves = desencriptaDades(connexioServidor.consulta(codiRequestXifrat), (SecretKeySpec) sKey, ALGORISME);
                    } catch (Exception ex) {
                        System.err.println("Error al desencriptar: " + ex);
                    }
                    if (!llistaReserves.isEmpty()) {
                        // Obtenim les dades de cada reserva
                        String[] reserves = llistaReserves.split("~");
                        int cont = 0;
                        for (String reserva : reserves) {
                            if (reserva.split(SEPARADOR)[0].equals(extras.split(SEPARADOR)[0])) {
                                if (cont == 0) {
                                    titleReserva1.setText(reserva.split(SEPARADOR)[1].toString());
                                    autorReserva1.setText(reserva.split(SEPARADOR)[2].toString());
                                    dataReserva1.setText(reserva.split(SEPARADOR)[4].toString());
                                } else if (cont == 1) {
                                    titleReserva.setText(reserva.split(SEPARADOR)[1].toString());
                                    autorReserva.setText(reserva.split(SEPARADOR)[2].toString());
                                    dataReserva.setText(reserva.split(SEPARADOR)[4].toString());
                                }
                                cont++;
                            }
                        }
                        final int finalCont = cont;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalCont == 1) {
                                    titleReserva1.setVisibility(View.VISIBLE);
                                    autorReserva1.setVisibility(View.VISIBLE);
                                    dataReserva1.setVisibility(View.VISIBLE);
                                    dataReservaLabel1.setVisibility(View.VISIBLE);
                                    btnEditaReserva1.setVisibility(View.VISIBLE);
                                    btnAnulaReserva1.setVisibility(View.VISIBLE);
                                    thumbnailReserva1.setVisibility(View.VISIBLE);
                                    titleReserva.setVisibility(View.GONE);
                                    autorReserva.setVisibility(View.GONE);
                                    dataReserva.setVisibility(View.GONE);
                                    dataReservaLabel.setVisibility(View.GONE);
                                    btnEditaReserva.setVisibility(View.GONE);
                                    btnAnulaReserva.setVisibility(View.GONE);
                                    thumbnailReserva.setVisibility(View.GONE);
                                } else if (finalCont == 2) {
                                    titleReserva.setVisibility(View.VISIBLE);
                                    autorReserva.setVisibility(View.VISIBLE);
                                    dataReserva.setVisibility(View.VISIBLE);
                                    dataReservaLabel.setVisibility(View.VISIBLE);
                                    thumbnailReserva.setVisibility(View.VISIBLE);
                                    btnAnulaReserva.setVisibility(View.VISIBLE);
                                    btnEditaReserva.setVisibility(View.VISIBLE);
                                    titleReserva1.setVisibility(View.VISIBLE);
                                    autorReserva1.setVisibility(View.VISIBLE);
                                    dataReserva1.setVisibility(View.VISIBLE);
                                    dataReservaLabel1.setVisibility(View.VISIBLE);
                                    thumbnailReserva1.setVisibility(View.VISIBLE);
                                    btnAnulaReserva1.setVisibility(View.VISIBLE);
                                    btnEditaReserva1.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("No hi ha reserves...");
                            }
                        });
                    }


                    resposta = connexioServidor.consulta(codiRequestXifrat);
                } else {
                    //TODO resposta si l'usuari no està logat
                }

            }
        });
        thread.start();
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
