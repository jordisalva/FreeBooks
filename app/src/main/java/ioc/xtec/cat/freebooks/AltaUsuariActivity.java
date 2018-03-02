package ioc.xtec.cat.freebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class AltaUsuariActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables dels botons
    Button botoAltaUsuari, botoCancelar;
    //Variables pels editText
    EditText userText,userPass;
    private String resposta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuari);

        userText = (EditText)findViewById(R.id.textUsuari);
        userPass = (EditText)findViewById(R.id.textContrasenya);
        // Definim els listeners
        botoAltaUsuari = ((Button)findViewById(R.id.buttonDonarAlta));
        botoAltaUsuari.setOnClickListener(this);
        botoCancelar = ((Button)findViewById(R.id.buttonCancelar));
        botoCancelar.setOnClickListener(this);
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(AltaUsuariActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == botoAltaUsuari) {
            final String codiRequest = "nouLogin-"+userText.getText()+"-"+userPass.getText();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        try{
                            Socket socket = new Socket("192.168.1.36", 9999);
                            try(BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
                                escriptor.write(codiRequest);
                                escriptor.newLine();
                                escriptor.flush();
                                //Obté el resultat del server
                                try (BufferedReader lector = new BufferedReader(
                                        new InputStreamReader(socket.getInputStream()))) {
                                    resposta = lector.readLine();
                                    if(resposta.equals("OK")){
                                        showToast("User created");
                                    }else{
                                        showToast("User already exists");
                                    }
                                }

                            }
                            socket.close();
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        }
    }
}
