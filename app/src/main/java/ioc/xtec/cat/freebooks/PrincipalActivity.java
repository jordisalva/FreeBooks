package ioc.xtec.cat.freebooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    // Variable del botó logout
    Button botoLogout;

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
        setContentView(R.layout.activity_principal);

        // Definim els listeners
        botoLogout = ((Button)findViewById(R.id.buttonLogout));
        botoLogout.setOnClickListener(this);

        // Crea un intent amb la pantalla de login
        i = new Intent(PrincipalActivity.this, MainActivity.class);
    }

    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {
        if (v == botoLogout) {
            tancaSessio();
        }
    }

    /**
     * Torna a la pantalla de login en cas de prémer el botó "Back"
     */
    @Override
    public void onBackPressed()
    {
        // Crida per tancar la sessió
        tancaSessio();
    }

    /**
     * Tanca la sessió activa i torna a la pantalla de login
     */
    public void tancaSessio() {
        // Crea un missatge d'alerta
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PrincipalActivity.this);

        // Títol del missatge d'alerta
        alertDialogBuilder.setTitle("Tancar sessió: ");

        // Defineix el missatge de l'alerta
        alertDialogBuilder
                .setMessage("Vol abandonar la sessió?")
                .setCancelable(false)
                .setPositiveButton("Sí",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                ConnexioServidor connexioServidor = new ConnexioServidor();
                                String codiRequest = "userLogout";
                                String resposta = connexioServidor.consulta(codiRequest);
                                if(resposta.equals("OK")){
                                    showToast("Sessió tancada correctament");
                                }
                                startActivity(i);
                                finish();
                            }
                        });
                        thread.start();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // Crea un missatge d'alerta
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Mostra el missatge d'alerta
        alertDialog.show();
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
                Toast.makeText(PrincipalActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
