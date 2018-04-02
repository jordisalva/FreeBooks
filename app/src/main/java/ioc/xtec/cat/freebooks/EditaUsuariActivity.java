package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static ioc.xtec.cat.freebooks.CriptoUtils.desencriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.encriptaDades;
import static ioc.xtec.cat.freebooks.CriptoUtils.passwordKeyGeneration;

public class EditaUsuariActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SEPARADOR = "Sep@!-@rad0R";
    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";

    // Variables dels botons
    Button botoGuardar, botoCancelar;
    // Variables pels editText
    EditText userText, userPass, userConfirmPass, userEmail;
    private String resposta = "";

    // Variable amb l'intent
    Intent i;

    /**
     * Accions en la creació
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_usuari);

        Intent iFinalitza = new Intent("finish");
        sendBroadcast(iFinalitza);

        userText = (EditText) findViewById(R.id.textUsuari);
        userPass = (EditText) findViewById(R.id.textContrasenya);
        userConfirmPass = (EditText) findViewById(R.id.textConfirmaContrasenya);
        userEmail = (EditText) findViewById(R.id.textMail);

        // Definim els listeners
        botoGuardar = ((Button) findViewById(R.id.buttonGuardar));
        botoGuardar.setOnClickListener(this);
        botoCancelar = ((Button) findViewById(R.id.buttonCancelar));
        botoCancelar.setOnClickListener(this);

        // Crea un intent amb la pantalla principal
        i = new Intent(this, PrincipalActivity.class);

        // Crida per omplir les dades de l'usuari existents a la bdd
        ompleDadesUsuari();
    }


    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {
        // Obté les dades de login introduïdes per l'usuari
        String usuariIntroduit = userText.getText().toString();
        String passIntroduit = userPass.getText().toString();
        String passConfirmacioIntroduit = userConfirmPass.getText().toString();
        String emailIntroduit = userEmail.getText().toString();


        if (v == botoGuardar) {

            if (usuariIntroduit.equals("") || passIntroduit.equals("") || passConfirmacioIntroduit.equals("") || emailIntroduit.equals("")) {
                showToast("Falten dades");
            } else {
                if (!passIntroduit.equals(passConfirmacioIntroduit)) {
                    showToast("Contrasenya diferent");
                } else {
                    // Crida per actualitzar les dades de l'usuari a la bdd
                    try {
                        guardaDadesUsuari();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Torna a guardar les dades des sessió per si s'ha modificat l'usuari

                    showToast("Dades guardades correctament");
                    String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
                    i.putExtra(EXTRA_MESSAGE,extra);
                    startActivity(i);
                    finish();
                }
            }
        } else {
            String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
            i.putExtra(EXTRA_MESSAGE,extra);
            startActivity(i);
            finish();
        }
    }


    /**
     * Torna a la pantalla principal en cas de prémer el botó "Back"
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
        i.putExtra(EXTRA_MESSAGE,extra);
        startActivity(i);
        finish();
    }

    /**
     * Omple les dades de l'usuari actual
     */
    public void ompleDadesUsuari() {
        final SecretKey sKey = passwordKeyGeneration("pass*12@", 128);

        try {               // TODO MILLORAR EL CODI
            final String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
            final String checkLogin = "userIsLogged" + SEPARADOR + extra.split(SEPARADOR)[0]+SEPARADOR+extra.split(SEPARADOR)[1];
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        ConnexioServidor connexioServidor = new ConnexioServidor();
                        String codiRequestXifrat = encriptaDades(checkLogin, (SecretKeySpec)sKey, "AES/ECB/PKCS5Padding");
                        if (connexioServidor.consulta(codiRequestXifrat).equals("OK")) {
                            ConnexioServidor connexioServidor2 = new ConnexioServidor();
                            String llistaLogs = "getLogins";
                            try {
                                codiRequestXifrat = encriptaDades(llistaLogs, (SecretKeySpec)sKey, "AES/ECB/PKCS5Padding");
                                llistaLogs = desencriptaDades(connexioServidor.consulta(codiRequestXifrat),(SecretKeySpec)sKey, "AES/ECB/PKCS5Padding");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            String[] usuaris = llistaLogs.split("~");
                            for (String usr : usuaris) {
                                if (usr.split(SEPARADOR)[0].equals(extra.split(SEPARADOR)[0])
                                        && usr.split(SEPARADOR)[2].equals(extra.split(SEPARADOR)[1])) {
                                    userText.setText(usr.split(SEPARADOR)[0]);
                                    userPass.setText(usr.split(SEPARADOR)[1]);
                                    userConfirmPass.setText(usr.split(SEPARADOR)[1]);
                                    userEmail.setText(usr.split(SEPARADOR)[4]);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Omple les dades de l'usuari actual
     */
    public void guardaDadesUsuari() {
        final SecretKey sKey = passwordKeyGeneration("pass*12@", 128);
        try {               // TODO MILLORAR EL CODI
            final String extra = getIntent().getStringExtra(EXTRA_MESSAGE);
            final String checkLogin = "userIsLogged" + SEPARADOR + extra.split(SEPARADOR)[0]+SEPARADOR+extra.split(SEPARADOR)[1];

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        ConnexioServidor connexioServidor = new ConnexioServidor();
                        if (connexioServidor.consulta(checkLogin).equals("OK")) {
                            String req = "editLoginMyLogin" + "Sep@!-@rad0R" + extra.split("Sep@!-@rad0R")[0]
                                    + "Sep@!-@rad0R" + extra.split("Sep@!-@rad0R")[1] + "Sep@!-@rad0R"
                                    + userText.getText() + "Sep@!-@rad0R" + userPass.getText() + "Sep@!-@rad0R" + userEmail.getText();
                            String codiRequestXifrat = encriptaDades(req, (SecretKeySpec)sKey, "AES/ECB/PKCS5Padding");
                            String res = connexioServidor.consulta(codiRequestXifrat);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
                Toast.makeText(EditaUsuariActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
