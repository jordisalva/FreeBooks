package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jordi on 17/03/2018.
 */

public class VisualitzarInfoLlibre extends AppCompatActivity implements View.OnClickListener {
    // Variables per les dades del llibre
    String strImatge;
    TextView textTitol;
    TextView textAutor;
    TextView textDescripcio;
    TextView textEditorIAny;
    TextView textNumPagines;
    TextView textIdioma;
    TextView textISBN;
    ImageView imatgeLlibre;

    Button btnReserva;
    Button btnTornar;

    // Variable amb l'intent
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualitzar_info_llibre);
        Bundle bundle = getIntent().getExtras();

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
        strImatge = bundle.getString("ImatgePortada");
        byte[] decodedString = Base64.decode(strImatge, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imatgeLlibre.setImageBitmap(decodedByte);
        textEditorIAny.setText(bundle.getString("EditorIAny"));
        textNumPagines.setText(bundle.getString("NumPagines"));
        textIdioma.setText(bundle.getString("Idioma"));
        textISBN.setText(bundle.getString("ISBN"));

        // Definim els listeners
        btnReserva = ((Button)findViewById(R.id.btnReserva));
        btnReserva.setOnClickListener(this);
        btnTornar = ((Button)findViewById(R.id.btnTornar));
        btnTornar.setOnClickListener(this);

        // Crea un intent amb la pantalla de login
        i = new Intent(this, PrincipalActivity.class);
    }

    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {
        if (v == btnReserva) {
            showToast("Estarà disponible al TEA4!");
        } else if (v == btnTornar) {
            startActivity(i);
            finish();
        }
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
                Toast.makeText(VisualitzarInfoLlibre.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
