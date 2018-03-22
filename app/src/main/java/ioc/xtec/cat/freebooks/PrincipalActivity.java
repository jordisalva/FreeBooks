package ioc.xtec.cat.freebooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    // Variable del botó logout
    Button botoLogout;

    // Variable amb l'intent
    Intent i;

    // Llista de llibres
    private ArrayList<Llibre> llistaLlibres;

    //Resta de variables utilitzades
    private RecyclerView recyclerView;
    private Adaptador adapter;
    private ProgressBar barra_progres;
    private int contador;

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
        //botoLogout = ((Button)findViewById(R.id.app_logout));
        //botoLogout.setOnClickListener(this);

        // Crea un intent amb la pantalla de login
        i = new Intent(PrincipalActivity.this, MainActivity.class);

        //Posem la barra de progrés amb un màxim de 100
        barra_progres = (ProgressBar) findViewById(R.id.progressBar);
        barra_progres.setMax(100);

        // Preparem la font de les dades
        llistaLlibres = new ArrayList<Llibre>();
        //PROVISIONAL!!
        /*for(int i = 1; i <= 10; i++){
            llistaLlibres.add(new Llibre("Prova","Prova","Prova",""));
        }*/

        //Referenciem el RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.rView);

        //Afegim l'adaptador amb les dades i el LinearLayoutManager que pintarà les dades
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adaptador(this, llistaLlibres);
        recyclerView.setAdapter(adapter);

        //Podem utilitzar animacions sobre la llista
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Amaguem el recyclerView
        recyclerView.setVisibility(View.INVISIBLE);
        // Mostrem la barra de progrés
        barra_progres.setVisibility(View.VISIBLE);

        // Executem la tasca per carregar els llibres
        new CarregaLlibres().execute((Void) null);

    }

    /**
     * Infla el menú
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Accions al seleccionar un item del menú
     *
     * @param item
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Si el botó és el de buscar, obre el buscador
        if (id == R.id.app_search) {
            // Buscador de llibres

        } else if (id == R.id.app_logout) {
            // Tanca la sessió
            tancaSessio();
        } else if (id == R.id.app_refresh) {
            // Refresca la llista de llibres

            // Amaga el recyclerView
            recyclerView.setVisibility(View.GONE);

            // Mostrem la barra de progrés
            barra_progres.setVisibility(View.VISIBLE);

            // Executem la tasca per carregar els llibres
            new CarregaLlibres().execute((Void) null);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Accions al fer click sobre els botons
     *
     * @param v amb el view on s'ha fet click
     */
    @Override
    public void onClick(View v) {
        /**if (v == botoLogout) {
            tancaSessio();
        }**/
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

    /**
     * AsyncTask que carrega les dades dels llibres del servidor, rep Strings com paràmetre
     * d'entrada, actualitza el progrés amb Integers i no retorna res
     */
    private class CarregaLlibres extends AsyncTask<Void, Integer, String>
    {
        protected String doInBackground(Void... voids)
        {
            // do something in background
            String llistaBooks = "";
            ConnexioServidor connexioServidor = new ConnexioServidor();
            llistaBooks = connexioServidor.consulta("getBooks");
            String[] llibresArray = llistaBooks.split("~");
            llistaLlibres.clear();
            //llistaLlibres = new ArrayList<Llibre>();
            for(String llib: llibresArray){
                //Exemple pasar String base64 a imatge
                //String base64Image = base64String.split(",")[1];
                //if (!llib.split("-")[3].equals("")) {

                    llistaLlibres.add(new Llibre(llib.split("-")[0],llib.split("-")[1],
                            llib.split("-")[2],llib.split("-")[3],llib.split("-")[4],llib.split("-")[5],llib.split("-")[6],llib.split("-")[7]));
                //}
            }

            // Temporal perquè va massa ràpid a carregar les imatges i no es veu la barra de progrés
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPreExecute()
        {
            // do something before start
            super.onPreExecute();
            //barra_progres.setProgress(0);
        }
        public void onProgressUpdate(Integer... args)
        {
            // Actualitzem la barra de progrés amb el valor que se'ns ha enviat des de doInBackground
            //barra_progres.setProgress(args[0]);
        }
        protected void onPostExecute(String result)
        {
            //recyclerView.setAdapter(adapter);

            //Podem utilitzar animacions sobre la llista
            //recyclerView.setItemAnimator(new DefaultItemAnimator());
            //Després de cada modificació a la font de les dades, hem de notificar-ho a l'adaptador
            adapter.notifyDataSetChanged();
            // Mostrem el recyclerView
            recyclerView.setVisibility(View.VISIBLE);
            // Amaguem la barra de progrés
            barra_progres.setVisibility(View.GONE);

        }
    }

}
