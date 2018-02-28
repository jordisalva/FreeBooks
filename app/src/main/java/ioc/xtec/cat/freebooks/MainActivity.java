package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //hola

    // Variables dels botons
    Button botoInici, botoAlta;

    // Variables dels TextViews
    TextView textUsuari, textContrasenya, missatge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definim els listeners
        botoInici = ((Button)findViewById(R.id.buttonInici));
        botoInici.setOnClickListener(this);
        botoAlta = ((Button)findViewById(R.id.buttonAlta));
        botoAlta.setOnClickListener(this);

        textUsuari = ((TextView)findViewById(R.id.textUsuari));
        textContrasenya = ((TextView)findViewById(R.id.textContrasenya));
        missatge = ((TextView)findViewById(R.id.missatge));

    }

    @Override
    public void onClick(View v) {

        // Obté les dades de login introduïdes per l'usuari
        String usuariIntroduit = textUsuari.getText().toString();
        String passIntroduit = textContrasenya.getText().toString();

        //  Mirem en quin botó ha fet click l'usuari
        if(v == botoInici) {
            if(!usuariIntroduit.equals("") && !passIntroduit.equals("")) {
                missatge.setText("Has fet el login amb: " + usuariIntroduit + " / " + passIntroduit);
                Intent i = new Intent(this, PrincipalActivity.class);
                startActivity(i);
            } else {
                missatge.setText("Has d'introduir l'usuari i contrasenya");
            }
        }

    }
}
