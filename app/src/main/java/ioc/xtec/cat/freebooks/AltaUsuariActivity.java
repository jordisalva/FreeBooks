package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AltaUsuariActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables dels botons
    Button botoAltaUsuari, botoCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuari);

        // Definim els listeners
        botoAltaUsuari = ((Button)findViewById(R.id.buttonDonarAlta));
        botoAltaUsuari.setOnClickListener(this);
        botoCancelar = ((Button)findViewById(R.id.buttonCancelar));
        botoCancelar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == botoAltaUsuari) {
            Intent i = new Intent(this, PrincipalActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}
