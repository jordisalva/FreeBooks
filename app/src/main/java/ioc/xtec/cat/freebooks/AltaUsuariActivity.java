package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AltaUsuariActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables dels botons
    Button botoAltaUsuari, botoCancelar;
    // Variables pels editText
    EditText userText,userPass,userConfirmPass,userEmail;
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
        setContentView(R.layout.activity_alta_usuari);

        userText = (EditText)findViewById(R.id.textUsuari);
        userPass = (EditText)findViewById(R.id.textContrasenya);
        userConfirmPass = (EditText)findViewById(R.id.textConfirmaContrasenya);
        userEmail = (EditText)findViewById(R.id.textMail);

        // Definim els listeners
        botoAltaUsuari = ((Button)findViewById(R.id.buttonDonarAlta));
        botoAltaUsuari.setOnClickListener(this);
        botoCancelar = ((Button)findViewById(R.id.buttonCancelar));
        botoCancelar.setOnClickListener(this);

        // Crea un intent amb la pantalla de login
        i = new Intent(this, MainActivity.class);
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


        if (v == botoAltaUsuari) {

            if(usuariIntroduit.equals("")||passIntroduit.equals("")||passConfirmacioIntroduit.equals("")||emailIntroduit.equals("")) {
                showToast("Falten dades");
            } else {
                if(!passIntroduit.equals(passConfirmacioIntroduit)) {
                    showToast("Contrasenya diferent");
                } else{
                    // Crida per donar d'alta l'usuari
                    altaUsuari();
                }
            }
        } else {
            startActivity(i);
            finish();
        }
    }


    /**
     * Torna a la pantalla de login en cas de prémer el botó "Back"
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(i);
        finish();
    }

    /**
     * Dona d'alta l'usuari amb tote les dades introduïdes
     */
    public void altaUsuari() {
        final String codiRequest = "nouLogin-"+userText.getText()+"-"+userPass.getText()+"-Mobile-"+userEmail.getText();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    ConnexioServidor connexioServidor = new ConnexioServidor();
                    resposta = connexioServidor.consulta(codiRequest);
                    if(resposta.equals("OK")){
                        showToast("Usuari Creat");
                        startActivity(i);
                        finish();
                    }else{
                        showToast("L'usuari ja existeix");
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
                Toast.makeText(AltaUsuariActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
