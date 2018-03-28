package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EditaUsuariActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SEPARADOR = "Sep@!-@rad0R";

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
                    guardarSessio();
                    showToast("Dades guardades correctament");
                    startActivity(i);
                    finish();
                }
            }
        } else {
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
        startActivity(i);
        finish();
    }

    /**
     * Omple les dades de l'usuari actual
     */
    public void ompleDadesUsuari() {
        try {               // TODO MILLORAR EL CODI
            final String checkLogin = "userIsLogged" + SEPARADOR + ObtainUserType();
            final ConnexioServidor connexioServidor = new ConnexioServidor();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        if (connexioServidor.consulta(checkLogin).equals("OK")) {
                            ConnexioServidor connexioServidor2 = new ConnexioServidor();
                            String llistaLogs = connexioServidor2.consulta("getLogins");
                            String[] usuaris = llistaLogs.split("~");
                            for (String usr : usuaris) {
                                if (usr.split(SEPARADOR)[0].equals(ObtainUserType().split(SEPARADOR)[0])
                                        && usr.split(SEPARADOR)[2].equals(ObtainUserType().split(SEPARADOR)[1])) {
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
        try {               // TODO MILLORAR EL CODI
            final String checkLogin = "userIsLogged" + SEPARADOR + ObtainUserType();
            final ConnexioServidor connexioServidor = new ConnexioServidor();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        if (connexioServidor.consulta(checkLogin).equals("OK")) {
                            String req = "editLoginMyLogin" + "Sep@!-@rad0R" + ObtainUserType().split("Sep@!-@rad0R")[0]
                                    + "Sep@!-@rad0R" + ObtainUserType().split("Sep@!-@rad0R")[1] + "Sep@!-@rad0R"
                                    + userText.getText() + "Sep@!-@rad0R" + userPass.getText() + "Sep@!-@rad0R" + userEmail.getText();
                            connexioServidor.consulta(req);
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
     * Obté les dades de l'usuari
     *
     * @return Un string amb les dades de l'usuari
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String ObtainUserType() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String res;
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        byte[] bytesClau = Files.readAllBytes(new File(Environment.getExternalStorageDirectory().toString() + "/Freebooks/ClauS").toPath());
        SecretKey myKey = new SecretKeySpec(bytesClau, "DES");
        Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, myKey);
        byte[] textEncrypted = fitxerEnBytes(new File(Environment.getExternalStorageDirectory().toString() + "/Freebooks/Usuari"));
        byte[] textDecrypted = desCipher.doFinal(textEncrypted);
        String s = new String(textDecrypted);
        res = s;
        return res;
    }

    /**
     * Se li passa com a paràmetre un fixer i retorna un fitxer en bytes
     *
     * @param f amb el fitxer
     * @return El fitxer en bytes
     * @throws IOException
     */
    private byte[] fitxerEnBytes(File f) throws IOException {
        byte[] fbytes;
        try (FileInputStream fis = new FileInputStream(f)) {
            fbytes = new byte[(int) f.length()];
            fis.read(fbytes);
        }
        return fbytes;
    }

    public void guardarSessio() {
        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        String message = userText.getText() + "Sep@!-@rad0R" + "Mobile";

                        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
                        SecretKey myKey = keyGen.generateKey();
                        Cipher desCipher;
                        desCipher = Cipher.getInstance("DES");
                        String userToEncrypt = message;
                        byte[] user = userToEncrypt.getBytes("UTF8");
                        desCipher.init(Cipher.ENCRYPT_MODE, myKey);
                        byte[] userEncrypted = desCipher.doFinal(user);
                        //Crea el directori Freebooks en cas de no existir
                        File directori = new File(Environment.getExternalStorageDirectory().toString() + "/Freebooks/");
                        if (!directori.exists()) {
                            directori.mkdir();
                        }
                        try (FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/FreeBooks/Usuari")) {
                            fos.write(userEncrypted);
                            fos.flush();
                        }
                        try (FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/FreeBooks/ClauS")) {
                            fos.write(myKey.getEncoded());
                            fos.flush();
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
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(EditaUsuariActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
