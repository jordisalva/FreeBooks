package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // Variables dels botons
    Button botoInici, botoAlta, botoSortir;

    // Variables dels TextViews
    TextView textUsuari, textContrasenya, missatge;

    // Codi per identificar la sessió
    private String codiSessio = "";

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

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        // Obté les dades de login introduïdes per l'usuari
        final String usuariIntroduit = textUsuari.getText().toString();
        final String passIntroduit = textContrasenya.getText().toString();

        //  Mirem en quin botó ha fet click l'usuari
        if(v == botoInici) {

            final String codiRequest = "userLogin-"+usuariIntroduit +"-"+passIntroduit+"-Mobile";
            final Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        ConnexioServidor connexioServidor = new ConnexioServidor();
                        codiSessio = connexioServidor.consulta(codiRequest);
                        if (usuariIntroduit.equals("") || passIntroduit.equals("")) {
                            showToast("Falten dades");
                        } else {
                            if(codiSessio.equals("FAIL")){
                                showToast("Usuari o contrasenya invàlids");
                            }else if (codiSessio.startsWith("OK")){
                                showToast("Usuari i contrasenya vàlids");
                                Intent i = new Intent(MainActivity.this, PrincipalActivity.class);
                                startActivity(i);
                            }else{
                                showToast("El servidor no respon");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


        } else if (v == botoAlta) {
            Intent i = new Intent(this, AltaUsuariActivity.class);
            startActivity(i);
        } else if (v == botoSortir) {
            finish();
        }

    }


}