package ioc.xtec.cat.freebooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static ioc.xtec.cat.freebooks.CriptoUtils.desencriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.encriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.passwordKeyGeneration;

/**
 * Created by jordi on 17/03/2018.
 */

public class VisualitzarInfoLlibre extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";
    public static final String SEPARADOR = "Sep@!-@rad0R";
    public static final String SEPARADOR_IMATGE = "@LENGTH@";
    public static final String ALGORISME = "AES/ECB/PKCS5Padding";
    // Variables per les dades del llibre
    String strImatge;
    TextView textTitol;
    TextView textAutor;
    TextView textDescripcio;
    TextView textEditorIAny;
    TextView textNumPagines;
    TextView textIdioma;
    TextView textISBN;

    // Resta de variables
    ImageView imatgeLlibre;
    Button btnReserva;
    Button btnTornar;

    // Variable amb l'intent
    Intent i;

    private int anyInici, mesInici, diaInici;
    private DatePickerDialog datePickerDialog;
    private int reservesUser;
    private String errorReserva;

    /**
     * Accions en la creació
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualitzar_info_llibre);
        Bundle bundle = getIntent().getExtras();
        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();

        Intent iFinalitza = new Intent("finish");
        sendBroadcast(iFinalitza);

        textTitol = (TextView) findViewById(R.id.textTitol);
        textAutor = (TextView) findViewById(R.id.textAutor);
        textDescripcio = (TextView) findViewById(R.id.textDescripcio);
        textEditorIAny = (TextView) findViewById(R.id.textEditorAny);
        textNumPagines = (TextView) findViewById(R.id.textPagines);
        textIdioma = (TextView) findViewById(R.id.textIdioma);
        textISBN = (TextView) findViewById(R.id.textISBN);
        imatgeLlibre = (ImageView) findViewById(R.id.imatgeLlibre);

        textTitol.setText(bundle.getString("Titol"));
        textAutor.setText(bundle.getString("Autor"));
        textDescripcio.setText(bundle.getString("Descripcio"));
        textDescripcio.setMovementMethod(new ScrollingMovementMethod());
        //strImatge = bundle.getString("ImatgePortada");
        strImatge = pref.getString("ImatgePortada", null);
        byte[] decodedString = Base64.decode(strImatge, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        // Si hi ha imatge carrega l'imatge
        if (decodedByte != null) {
            //imatgeLlibre.setBackground(null);
            imatgeLlibre.setImageBitmap(decodedByte);
            // Si no hi ha imatge carrega una imatge per defecte
        } else {
            imatgeLlibre.setImageResource(android.R.drawable.ic_menu_report_image);
        }
        textEditorIAny.setText(bundle.getString("EditorIAny"));
        textNumPagines.setText(bundle.getString("NumPagines"));
        textIdioma.setText(bundle.getString("Idioma"));
        textISBN.setText(bundle.getString("ISBN"));

        // Definim els listeners
        btnReserva = ((Button) findViewById(R.id.btnReserva));
        btnReserva.setOnClickListener(this);
        btnTornar = ((Button) findViewById(R.id.btnTornar));
        btnTornar.setOnClickListener(this);

        // Crea un intent amb la pantalla de login
        i = new Intent(VisualitzarInfoLlibre.this, PrincipalActivity.class);

        ed.remove("ImatgePortada");
        ed.clear();
        ed.commit();
    }

    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {
        if (v == btnReserva) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
                    String extras = getIntent().getStringExtra(EXTRA_MESSAGE);
                    String codiRequestXifrat = "";
                    ConnexioServidor connexioServidor = new ConnexioServidor(getApplicationContext());
                    String checkLogin = "userIsLogged" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[1];
                    String reservesUserString = "";
                    try {
                        codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec) sKey, ALGORISME);
                    } catch (Exception ex) {
                        System.err.println("Error al encriptar: " + ex);
                    }

                    // Fa la crida al servidor amb al consulta de si l'usuari està logat
                    String resposta = connexioServidor.consulta(codiRequestXifrat);

                    // En cas que si estigui logat, guarda a reservesUser les reserves que ha realitzat l'usuari
                    if (resposta.equals("OK")) {
                        String reservationsUser = "getReservationsPerUser" + SEPARADOR + extras.split(SEPARADOR)[0];

                        try {
                            codiRequestXifrat = encriptaDades(reservationsUser, (SecretKeySpec) sKey, ALGORISME);
                            try {
                                reservesUserString = desencriptaDades(connexioServidor.consulta(codiRequestXifrat), (SecretKeySpec) sKey, ALGORISME);
                            } catch (Exception ex) {
                                System.err.println("Error al desencriptar: " + ex);
                            }
                            reservesUser = Integer.parseInt(reservesUserString);
                        } catch (Exception ex) {
                            System.err.println("Error al encriptar: " + ex);
                        }
                        resposta = connexioServidor.consulta(codiRequestXifrat);
                    } else {
                        Toast.makeText(getApplicationContext(), "L'usuari no està logat...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // En cas que passi de les 2 reserves, mostra un missatge a l'usuari informant-lo
            if (reservesUser >= 2 || llibreJaReservat(textISBN.getText().toString())) {
                if (reservesUser >= 2) {
                    Toast.makeText(getApplicationContext(), "Has assolit el màxim de reserves simultànies permeses per usuari, que és 2...", Toast.LENGTH_SHORT).show();
                }

                // En cas que el llibre ja hagi estat reservat, mostra un missatge a l'usuari informant-lo
                if (llibreJaReservat(textISBN.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "No pots reservar el mateix llibre dos cops...", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Crea un missatge d'alerta
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        VisualitzarInfoLlibre.this);

                // Títol del missatge d'alerta
                alertDialogBuilder.setTitle("Reservar: ");

                // Defineix el missatge de l'alerta
                alertDialogBuilder
                        .setMessage("Vols reservar el llibre: " +
                                textTitol.getText().toString() + "? \n\n" +
                                "Al fer click a \"Sí\" hauras de triar el dia que vols recollir el llibre")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final Calendar c = Calendar.getInstance();
                                anyInici = c.get(Calendar.YEAR);
                                mesInici = c.get(Calendar.MONTH);
                                diaInici = c.get(Calendar.DAY_OF_MONTH);
                                datePickerDialog = new DatePickerDialog(
                                        VisualitzarInfoLlibre.this, VisualitzarInfoLlibre.this, anyInici, mesInici, diaInici);
                                long now = System.currentTimeMillis() - 1000;
                                // Defineix una data mínima
                                datePickerDialog.getDatePicker().setMinDate(now);
                                // Defineix una dat màxima
                                datePickerDialog.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * 21));
                                datePickerDialog.setTitle("Indica la data de reserva");
                                // Mostra un diàleg per seleccionar la data que es vol recollir el llibre
                                datePickerDialog.show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // Crea un missatge d'alerta
                AlertDialog alertDialog = alertDialogBuilder.create();

                // Mostra el missatge d'alerta
                alertDialog.show();
            }
        } else if (v == btnTornar) {
            String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
            i.putExtra(EXTRA_MESSAGE, extra);
            i.putExtra("Inici", "noinici");
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
                Toast.makeText(VisualitzarInfoLlibre.this, toast, Toast.LENGTH_SHORT).show();
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
        i.putExtra("Inici", "noinici");
        startActivity(i);
        finish();
    }

    /**
     * @param view amb la vista
     * @param any  amb l'any de la reserva
     * @param mes  amb el mes de la reserva
     * @param dia  amb el dia de la reserva
     */
    @Override
    public void onDateSet(DatePicker view, int any, int mes, int dia) {
        mes = mes + 1;
        final String dataReserva = any + "-" + mes + "-" + dia;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Verifica si l'usuari està logat
                SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
                String extras = getIntent().getStringExtra(EXTRA_MESSAGE);
                String codiRequestXifrat = "";
                ConnexioServidor connexioServidor = new ConnexioServidor(getApplicationContext());
                String checkLogin = "userIsLogged" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[1];
                try {
                    codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec) sKey, ALGORISME);
                } catch (Exception ex) {
                    System.err.println("Error al encriptar: " + ex);
                }

                // Fa la crida al servidor amb al consulta de si l'usuari està logat
                String resposta = connexioServidor.consulta(codiRequestXifrat);

                // En cas que si estigui logat, guarda la reserva
                if (resposta.equals("OK")) {
                    String insertReservation = "novaReserva" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + textISBN.getText().toString() + SEPARADOR + dataReserva;
                    try {
                        codiRequestXifrat = encriptaDades(insertReservation, (SecretKeySpec) sKey, ALGORISME);
                    } catch (Exception ex) {
                        System.err.println("Error al encriptar: " + ex);
                    }
                    errorReserva = connexioServidor.consulta(codiRequestXifrat);
                } else {
                    Toast.makeText(getApplicationContext(), "L'usuari no està logat...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Si no s'ha pogut fer la reserva, s'informarà a l'usuari
        if (errorReserva.equals("FAILNOTUNIQUE")) {
            Toast.makeText(getApplicationContext(), "No pots reservar el mateix llibre dos cops...", Toast.LENGTH_SHORT).show();

            // Si s'ha fet la reserva, s'informa a l'usuari i accedeix a la pantalla de reserves
        } else if (errorReserva.equals("OK")) {
            Toast.makeText(getApplicationContext(), "Has reservat el llibre: \n" + textTitol.getText().toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Data de recollida seleccionada: \n" + dia + "/" + mes + "/" + any, Toast.LENGTH_SHORT).show();
            // Al finalitzar la reserva et porta a la pantalla de reserves
            String extrasMessage = ((Activity) VisualitzarInfoLlibre.this).getIntent().getStringExtra(EXTRA_MESSAGE);
            // Crea un intent amb la pantalla de reserves
            Intent i = new Intent(VisualitzarInfoLlibre.this, Reserves.class);
            i.putExtra(EXTRA_MESSAGE, extrasMessage);
            i.putExtra("Inici", "noinici");
            VisualitzarInfoLlibre.this.startActivity(i);
            finish();
        }

    }

    /**
     * Verifica si l'usuari actual ja ha reservat un llibre amb el mateix ISBN
     *
     * @return boleà per verificar si existeixen reserves per l'usuari actual amb el mateix ISBN
     */
    public boolean llibreJaReservat(final String ISBN) {
        final int[] reservesUser = new int[1];
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
                String extras = getIntent().getStringExtra(EXTRA_MESSAGE);
                String codiRequestXifrat = "";
                ConnexioServidor connexioServidor = new ConnexioServidor(getApplicationContext());
                String checkLogin = "userIsLogged" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[1];
                String reservesUserString = "";
                try {
                    codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec) sKey, ALGORISME);
                } catch (Exception ex) {
                    System.err.println("Error al encriptar: " + ex);
                }

                String resposta = connexioServidor.consulta(codiRequestXifrat);
                if (resposta.equals("OK")) {
                    String reservationsUserAndIsbn = "getReservationsPerUserAndIsbn" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + ISBN;

                    try {
                        codiRequestXifrat = encriptaDades(reservationsUserAndIsbn, (SecretKeySpec) sKey, ALGORISME);
                        try {
                            reservesUserString = desencriptaDades(connexioServidor.consulta(codiRequestXifrat), (SecretKeySpec) sKey, ALGORISME);
                        } catch (Exception ex) {
                            System.err.println("Error al desencriptar: " + ex);
                        }
                        reservesUser[0] = Integer.parseInt(reservesUserString);
                    } catch (Exception ex) {
                        System.err.println("Error al encriptar: " + ex);
                    }
                    resposta = connexioServidor.consulta(codiRequestXifrat);
                } else {
                    Toast.makeText(getApplicationContext(), "L'usuari no està logat...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean teReserves = false;
        if (reservesUser[0] > 0) {
            teReserves = true;
        }

        return teReserves;
    }
}
