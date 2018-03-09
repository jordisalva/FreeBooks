package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables dels botons
    Button botoLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Definim els listeners
        botoLogout = ((Button)findViewById(R.id.buttonLogout));
        botoLogout.setOnClickListener(this);
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(PrincipalActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == botoLogout) {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    ConnexioServidor connexioServidor = new ConnexioServidor();
                    String codiRequest = "userLogout";
                    String resposta = connexioServidor.consulta(codiRequest);
                    if(resposta.equals("OK")){
                        showToast("Sessió tancada correctament");
                    }
                    Intent i = new Intent(PrincipalActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });
            thread.start();

        }
    }
}
