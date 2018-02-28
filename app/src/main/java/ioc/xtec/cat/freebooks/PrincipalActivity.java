package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

    @Override
    public void onClick(View v) {
        if (v == botoLogout) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}
