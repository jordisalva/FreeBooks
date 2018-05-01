package ioc.xtec.cat.freebooks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static ioc.xtec.cat.freebooks.CriptoUtils.desencriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.encriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.passwordKeyGeneration;

public class Reserves extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";
    public static final String SEPARADOR = "Sep@!-@rad0R";
    public static final String ALGORISME = "AES/ECB/PKCS5Padding";
    public static final String SEPARADOR_IMATGE = "@LENGTH@";

    // Llista de llibres
    private ArrayList<Llibre> llistaLlibres;

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
    TextView llocHoraLabel1;
    TextView llocHora1;

    ImageButton btnEditaReserva;
    ImageButton btnAnulaReserva;
    ImageView thumbnailReserva;
    TextView titleReserva;
    TextView autorReserva;
    TextView dataReserva;
    TextView dataReservaLabel;
    TextView llocHoraLabel;
    TextView llocHora;

    String llistaReserves;
    String isbn;
    String isbn1;

    int contReserves;

    private int anyInici, mesInici, diaInici;
    private DatePickerDialog datePickerDialog;

    String modificatISBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserves);

        Intent iFinalitza = new Intent("finish");
        sendBroadcast(iFinalitza);

        // Mostra la llista de reserves
        mostraLlistaReserves();
    }

    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(final View v) {
        if (v == btnEditaReserva || v == btnEditaReserva1) {
            if (v == btnEditaReserva) {
                modificatISBN = isbn;
            } else if (v == btnEditaReserva1) {
                modificatISBN = isbn1;
            }

            final Calendar c = Calendar.getInstance();
            anyInici = c.get(Calendar.YEAR);
            mesInici = c.get(Calendar.MONTH);
            diaInici = c.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(
                    Reserves.this, Reserves.this, anyInici, mesInici, diaInici);
            long now = System.currentTimeMillis() - 1000;
            datePickerDialog.getDatePicker().setMinDate(now);
            datePickerDialog.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * 21));
            datePickerDialog.setTitle("Indica la data de reserva");
            // Mostra un diàleg per seleccionar la data que es vol recollir el llibre
            datePickerDialog.show();

        } else if (v == btnAnulaReserva || v == btnAnulaReserva1) {
            String titolAEsborrar = "";
            if (v == btnAnulaReserva) {
                titolAEsborrar = titleReserva.getText().toString();
            } else if (v == btnAnulaReserva1) {
                titolAEsborrar = titleReserva1.getText().toString();
            }

            // Crea un missatge d'alerta
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Reserves.this);

            // Títol del missatge d'alerta
            alertDialogBuilder.setTitle("Anul·lar Reserva: ");

            // Defineix el missatge de l'alerta
            alertDialogBuilder
                    .setMessage("Vols anul·lar la reserva del llibre: " +
                            titolAEsborrar + "?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String isbnABorrar = "";
                            if (v == btnAnulaReserva) {
                                isbnABorrar = isbn;

                                titleReserva.setVisibility(View.GONE);
                                autorReserva.setVisibility(View.GONE);
                                dataReserva.setVisibility(View.GONE);
                                dataReservaLabel.setVisibility(View.GONE);
                                btnEditaReserva.setVisibility(View.GONE);
                                btnAnulaReserva.setVisibility(View.GONE);
                                thumbnailReserva.setVisibility(View.GONE);
                                llocHoraLabel.setVisibility(View.GONE);
                                llocHora.setVisibility(View.GONE);


                            } else if (v == btnAnulaReserva1) {
                                isbnABorrar = isbn1;

                                titleReserva1.setVisibility(View.GONE);
                                autorReserva1.setVisibility(View.GONE);
                                dataReserva1.setVisibility(View.GONE);
                                dataReservaLabel1.setVisibility(View.GONE);
                                btnEditaReserva1.setVisibility(View.GONE);
                                btnAnulaReserva1.setVisibility(View.GONE);
                                thumbnailReserva1.setVisibility(View.GONE);
                                llocHoraLabel1.setVisibility(View.GONE);
                                llocHora1.setVisibility(View.GONE);
                            }

                            final String finalIsbnABorrar = isbnABorrar;
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
                                        String removeReservation = "removeReserva" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + finalIsbnABorrar;
                                        try {
                                            codiRequestXifrat = encriptaDades(removeReservation, (SecretKeySpec) sKey, ALGORISME);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showToast("Reserva anul·lada correctament...");
                                                    contReserves--;
                                                    if (contReserves == 0) {
                                                        showToast("No hi ha reserves...");
                                                    }
                                                }
                                            });
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

        } else if (v == btnTornarReserves) {
            // Torna a la pantalla principal
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
        i.putExtra("Inici", "noinici");
        startActivity(i);
        finish();
    }


    /**
     * Converteix la data a un format llegible per l'usuari
     *
     * @param dataSenseFormat data obtinguda amb format estàndard
     * @return data amb format personalitzat
     */
    public static String dataAmbFormat(String dataSenseFormat) {
        String dataAmbFormat = "";
        // Obtenim el dia de la setmana i li donem format
        String weekDay = dataSenseFormat.substring(0, 3);
        switch (weekDay) {
            case "Mon":
                dataAmbFormat = "Dilluns, ";
                break;
            case "Tue":
                dataAmbFormat = "Dimarts, ";
                break;
            case "Wed":
                dataAmbFormat = "Dimecres, ";
                break;
            case "Thu":
                dataAmbFormat = "Dijous, ";
                break;
            case "Fri":
                dataAmbFormat = "Divendres, ";
                break;
            case "Sat":
                dataAmbFormat = "Dissabte, ";
                break;
            case "Sun":
                dataAmbFormat = "Diumenge, ";
                break;
        }
        // Obtenim el dia
        String day = dataSenseFormat.substring(8, 10);
        // Obtenim el mes i li donem format
        String month = dataSenseFormat.substring(4, 7);
        switch (month) {
            case "Jan":
                dataAmbFormat = dataAmbFormat + day + " de gener";
                break;
            case "Feb":
                dataAmbFormat = dataAmbFormat + day + " de febrer";
                break;
            case "Mar":
                dataAmbFormat = dataAmbFormat + day + " de març";
                break;
            case "Apr":
                dataAmbFormat = dataAmbFormat + day + " d'abril";
                break;
            case "May":
                dataAmbFormat = dataAmbFormat + day + " de maig";
                break;
            case "Jun":
                dataAmbFormat = dataAmbFormat + day + " de juny";
                break;
            case "Jul":
                dataAmbFormat = dataAmbFormat + day + " de juliol";
                break;
            case "Aug":
                dataAmbFormat = dataAmbFormat + day + " d'agost";
                break;
            case "Sep":
                dataAmbFormat = dataAmbFormat + day + " de setembre";
                break;
            case "Oct":
                dataAmbFormat = dataAmbFormat + day + " d'octubre";
                break;
            case "Nov":
                dataAmbFormat = dataAmbFormat + day + " de novembre";
                break;
            case "Dec":
                dataAmbFormat = dataAmbFormat + day + " de desembre";
                break;
        }
        return dataAmbFormat;
    }

    @Override
    public void onDateSet(DatePicker view, int any, int mes, int dia) {
        mes = mes + 1;
        //Toast.makeText(getApplicationContext(), "Has reservat el llibre: \n" + items.get(posicioReserva).getTitol(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Data de recollida seleccionada: \n" + dia + "/" + mes + "/" + any, Toast.LENGTH_SHORT).show();
        final String dataReservaDataSet = any + "-" + mes + "-" + dia;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
                String extras = getIntent().getStringExtra(EXTRA_MESSAGE);
                String codiRequestXifrat = "";
                ConnexioServidor connexioServidor = new ConnexioServidor(getApplicationContext());
                String checkLogin = "userIsLogged" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[1];
                int reservesUser;
                try {
                    codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec) sKey, ALGORISME);
                } catch (Exception ex) {
                    System.err.println("Error al encriptar: " + ex);
                }

                String resposta = connexioServidor.consulta(codiRequestXifrat);
                if (resposta.equals("OK")) {

                    String editReservationDate = "editReservationDate" + SEPARADOR + dataReservaDataSet + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + modificatISBN;
                    try {
                        codiRequestXifrat = encriptaDades(editReservationDate, (SecretKeySpec) sKey, ALGORISME);
                    } catch (Exception ex) {
                        System.err.println("Error al encriptar: " + ex);
                    }

                    try {
                        codiRequestXifrat = encriptaDades(editReservationDate, (SecretKeySpec) sKey, ALGORISME);
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mostra la llista de reserves
        mostraLlistaReserves();


    }

    /**
     * Verifica si existeixen reserves per l'usuari actual
     *
     * @return boleà per verificar si existeixen reserves per l'usuari actual
     */
    public boolean teReserves() {
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
                    String reservationsUser = "getReservationsPerUser" + SEPARADOR + extras.split(SEPARADOR)[0];

                    try {
                        codiRequestXifrat = encriptaDades(reservationsUser, (SecretKeySpec) sKey, ALGORISME);
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


    public void mostraLlistaReserves() {
        if (!teReserves()) {
            showToast("No hi ha reserves...");
        }

        // Preparem la font de les dades
        llistaLlibres = new ArrayList<Llibre>();

        // Executem la tasca per carregar els llibres
        new CarregaLlibres().execute((Void) null);

        contReserves = 0;

        imatgeReserves = ((ImageView) findViewById(R.id.imageReserves));
        imatgeReserves.setColorFilter(Color.GRAY);
        thumbnailReserva1 = ((ImageView) findViewById(R.id.thumbnailReserva1));
        titleReserva1 = ((TextView) findViewById(R.id.titleReserva1));
        autorReserva1 = ((TextView) findViewById(R.id.autorReserva1));
        dataReserva1 = ((TextView) findViewById(R.id.dataReserva1));
        dataReservaLabel1 = ((TextView) findViewById(R.id.dataReservaLabel1));
        llocHoraLabel1 = ((TextView) findViewById(R.id.llocHoraLabel1));
        llocHora1 = ((TextView) findViewById(R.id.llocHora1));
        thumbnailReserva = ((ImageView) findViewById(R.id.thumbnailReserva));
        titleReserva = ((TextView) findViewById(R.id.titleReserva));
        autorReserva = ((TextView) findViewById(R.id.autorReserva));
        dataReserva = ((TextView) findViewById(R.id.dataReserva));
        dataReservaLabel = ((TextView) findViewById(R.id.dataReservaLabel));
        llocHoraLabel = ((TextView) findViewById(R.id.llocHoraLabel));
        llocHora = ((TextView) findViewById(R.id.llocHora));
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
        llocHoraLabel.setVisibility(View.GONE);
        llocHora.setVisibility(View.GONE);
        thumbnailReserva1.setVisibility(View.GONE);
        titleReserva1.setVisibility(View.GONE);
        autorReserva1.setVisibility(View.GONE);
        dataReserva1.setVisibility(View.GONE);
        dataReservaLabel1.setVisibility(View.GONE);
        btnEditaReserva1.setVisibility(View.GONE);
        btnAnulaReserva1.setVisibility(View.GONE);
        llocHoraLabel1.setVisibility(View.GONE);
        llocHora1.setVisibility(View.GONE);

        // Crea un intent amb la pantalla de login
        i = new Intent(Reserves.this, PrincipalActivity.class);


        // Mostra la llista de reserves de l'usuari
        final Thread thread = new Thread(new Runnable() {

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
                        // Obtenim la llista de llibres
                        ArrayList<String> ar1 = getIntent().getExtras().getStringArrayList("LlistaLlibres");
                        // String amb l'imatge de portada
                        String imatgeString="";
                        // Obtenim les dades de cada reserva
                        String[] reserves = llistaReserves.split("~");
                        for (String reserva : reserves) {
                            if (reserva.split(SEPARADOR)[0].equals(extras.split(SEPARADOR)[0])) {
                                if (contReserves == 0) {
                                    titleReserva1.setText(reserva.split(SEPARADOR)[1].toString());
                                    autorReserva1.setText(reserva.split(SEPARADOR)[2].toString());
                                    String dataAmbFormatString = dataAmbFormat(reserva.split(SEPARADOR)[4].toString());
                                    dataReserva1.setText(dataAmbFormatString);
                                    isbn1 = reserva.split(SEPARADOR)[3].toString();
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < llistaLlibres.size(); i++) {
                                        if (llistaLlibres.get(i).getISBN().equals(isbn1)) {
                                            imatgeString = llistaLlibres.get(i).getImatgePortada().split(SEPARADOR_IMATGE)[0];
                                        }
                                    }
                                    // String amb l'imatge en base64
                                    final String base64StringImage = imatgeString;
                                    Bitmap decodedByte = null;
                                    try {
                                        // Decodifica l'imatge
                                        byte[] decodedString = Base64.decode(base64StringImage, Base64.DEFAULT);
                                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    final Bitmap finalDecodedByte = decodedByte;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            thumbnailReserva1.setImageBitmap(finalDecodedByte);
                                            if (base64StringImage.equals("")) {
                                                thumbnailReserva1.setBackgroundResource(android.R.drawable.ic_menu_report_image);
                                            }
                                        }
                                    });
                                } else if (contReserves == 1) {
                                    titleReserva.setText(reserva.split(SEPARADOR)[1].toString());
                                    autorReserva.setText(reserva.split(SEPARADOR)[2].toString());
                                    String dataAmbFormatString = dataAmbFormat(reserva.split(SEPARADOR)[4].toString());
                                    dataReserva.setText(dataAmbFormatString);
                                    isbn = reserva.split(SEPARADOR)[3].toString();
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < llistaLlibres.size(); i++) {
                                        if (llistaLlibres.get(i).getISBN().equals(isbn)) {
                                            imatgeString = llistaLlibres.get(i).getImatgePortada().split(SEPARADOR_IMATGE)[0];
                                        }
                                    }
                                    // String amb l'imatge en base64
                                    final String base64StringImage = imatgeString;
                                    Bitmap decodedByte = null;
                                    try {
                                        // Decodifica l'imatge
                                        byte[] decodedString = Base64.decode(base64StringImage, Base64.DEFAULT);
                                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    final Bitmap finalDecodedByte = decodedByte;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            thumbnailReserva.setImageBitmap(finalDecodedByte);
                                            if (base64StringImage.equals("")) {
                                                thumbnailReserva.setBackgroundResource(android.R.drawable.ic_menu_report_image);
                                            }
                                        }
                                    });
                                }
                                contReserves++;
                            }
                        }
                        final int finalCont = contReserves;
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
                                    llocHoraLabel1.setVisibility(View.VISIBLE);
                                    llocHora1.setVisibility(View.VISIBLE);
                                    titleReserva.setVisibility(View.GONE);
                                    autorReserva.setVisibility(View.GONE);
                                    dataReserva.setVisibility(View.GONE);
                                    dataReservaLabel.setVisibility(View.GONE);
                                    btnEditaReserva.setVisibility(View.GONE);
                                    btnAnulaReserva.setVisibility(View.GONE);
                                    thumbnailReserva.setVisibility(View.GONE);
                                    llocHoraLabel.setVisibility(View.GONE);
                                    llocHora.setVisibility(View.GONE);
                                } else if (finalCont == 2) {
                                    titleReserva.setVisibility(View.VISIBLE);
                                    autorReserva.setVisibility(View.VISIBLE);
                                    dataReserva.setVisibility(View.VISIBLE);
                                    dataReservaLabel.setVisibility(View.VISIBLE);
                                    thumbnailReserva.setVisibility(View.VISIBLE);
                                    btnAnulaReserva.setVisibility(View.VISIBLE);
                                    btnEditaReserva.setVisibility(View.VISIBLE);
                                    llocHoraLabel.setVisibility(View.VISIBLE);
                                    llocHora.setVisibility(View.VISIBLE);
                                    titleReserva1.setVisibility(View.VISIBLE);
                                    autorReserva1.setVisibility(View.VISIBLE);
                                    dataReserva1.setVisibility(View.VISIBLE);
                                    dataReservaLabel1.setVisibility(View.VISIBLE);
                                    thumbnailReserva1.setVisibility(View.VISIBLE);
                                    btnAnulaReserva1.setVisibility(View.VISIBLE);
                                    btnEditaReserva1.setVisibility(View.VISIBLE);
                                    llocHoraLabel1.setVisibility(View.VISIBLE);
                                    llocHora1.setVisibility(View.VISIBLE);
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
    }


    /**
     * AsyncTask que carrega les dades dels llibres del servidor, rep Strings com paràmetre
     * d'entrada, actualitza el progrés amb Integers i no retorna res
     */
    private class CarregaLlibres extends AsyncTask<Void, Integer, String> {
        final SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
        String codiRequestXifrat = "";

        protected String doInBackground(Void... voids) {
            // Borrem la llista antiga de llibres
            llistaLlibres.clear();
            // Realitzem la connexió amb el servidor
            ConnexioServidor connexioServidor = new ConnexioServidor(getApplicationContext());
            //Primer revisem si l'usuari està logat
            String extras = getIntent().getStringExtra(EXTRA_MESSAGE);
            String checkLogin = "userIsLogged" + SEPARADOR + extras.split(SEPARADOR)[0] + SEPARADOR + extras.split(SEPARADOR)[1];
            try {
                codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec) sKey, ALGORISME);
            } catch (Exception ex) {
                System.err.println("Error al encriptar: " + ex);
            }
            String resposta = connexioServidor.consulta(codiRequestXifrat);
            if (resposta.equals("OK")) {
                // El servidor retorna la llista de llibres en un string
                String llistaBooks = "getBooksDesktop";
                try {
                    codiRequestXifrat = encriptaDades(llistaBooks, (SecretKeySpec) sKey, ALGORISME);
                    // Anirem fent connexions al servidor demanant la llista de llibres, fins que aquesta es pugui desencriptar correctament
                    while (llistaBooks.equals("getBooksDesktop")) {
                        try {
                            llistaBooks = desencriptaDades(connexioServidor.consulta(codiRequestXifrat), (SecretKeySpec) sKey, ALGORISME);
                        } catch (Exception ex) {
                            System.err.println("Error al desencriptar: " + ex);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error al encriptar: " + ex);
                }
                // Si la llista no és buida carreguem els llibres
                if (!llistaBooks.isEmpty()) {
                    String[] llibresArray = llistaBooks.split("~");
                    int midaArray = llibresArray.length;
                    // Recorrem l'array de llibres
                    for (int i = 0; i < midaArray; i++) {
                        try {
                            llistaLlibres.add(new Llibre(llibresArray[i].split(SEPARADOR)[0], llibresArray[i].split(SEPARADOR)[1],
                                    llibresArray[i].split(SEPARADOR)[2], llibresArray[i].split(SEPARADOR)[3], llibresArray[i].split(SEPARADOR)[4],
                                    llibresArray[i].split(SEPARADOR)[5], llibresArray[i].split(SEPARADOR)[6], llibresArray[i].split(SEPARADOR)[7]));
                        } catch (Exception ex) {
                            System.err.println("Error al afegir llibre:  " + ex);
                        }

                    }

                    // Va massa ràpid a carregar les imatges i no es veu la barra de progrés
                    // Li afegim un segon de retard perqué és vegi sempre
                    /**try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }**/

                } else {
                    showToast("No hi ha llibres a la base de dades");
                }
            } else {
                showToast("El servidor no respón, torna a fer el login més tard...");
                startActivity(i);
                finish();
            }

            return null;
        }

        /**
         * S'executa abans de l'execució de la tasca
         */
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * S'executa durant l'execució de la tasca
         *
         * @param args
         */
        public void onProgressUpdate(Integer... args) {
            super.onProgressUpdate();
        }

        /**
         * S'executa després de l'execució de la tasca
         *
         * @param result
         */
        protected void onPostExecute(String result) {
            //
        }
    }

}
