package ioc.xtec.cat.freebooks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jordi on 17/03/2018.
 */

public class Adaptador extends RecyclerView.Adapter<Adaptador.ElMeuViewHolder> implements Filterable {

    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";
    public static final String SEPARADOR_IMATGE = "@LENGTH@";
    private ArrayList<Llibre> items, filterList;
    private Context context;
    private FiltreLlibres filter;

    /**
     * Creem el constructor
     *
     * @param context
     * @param items
     */
    public Adaptador(Context context, ArrayList<Llibre> items) {
        this.context = context;
        this.items = items;
        this.filterList = items;
    }

    /**
     * Crea noves files (l'invoca el layout manager). Aquí fem referència al layout fila.xml
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public Adaptador.ElMeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila, null);
        // create ViewHolder
        ElMeuViewHolder viewHolder = new ElMeuViewHolder(itemLayoutView);
        return viewHolder;
    }

    /**
     * Retorna la quantitat de les dades
     *
     * @return la quantitat de les dades
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Retorna l'objecte filtrat
     *
     * @return l'objecte filtrat
     */
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FiltreLlibres(filterList, this);
        }
        return filter;
    }

    /**
     * Carreguem els widgets amb les dades (l'invoca el layout manager)
     *
     * @param viewHolder
     * @param position   Conté la posició de l'element actual a la llista.
     *                   També l'utilitzarem com a índex per a recòrrer les dades
     */
    @Override
    public void onBindViewHolder(final ElMeuViewHolder viewHolder, final int position) {
        // String amb l'imatge en base64
        String base64StringImage = items.get(position).getImatgePortada().split(SEPARADOR_IMATGE)[0];
        Bitmap decodedByte = null;
        try {
            // Decodifica l'imatge
            byte[] decodedString = Base64.decode(base64StringImage, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Carreguem els widgets amb les dades

        // Si hi ha imatge carrega l'imatge
        if (decodedByte != null) {
            viewHolder.vThumbnail.setBackground(null);
            viewHolder.vThumbnail.setImageBitmap(decodedByte);
            // Si no hi ha imatge carrega una imatge per defecte
        } else {
            viewHolder.vThumbnail.setBackgroundResource(android.R.drawable.ic_menu_report_image);
            viewHolder.vThumbnail.getLayoutParams().height = 220;
            viewHolder.vThumbnail.getLayoutParams().width = 220;
            RelativeLayout.LayoutParams layoutarams = (RelativeLayout.LayoutParams)viewHolder.vThumbnail.getLayoutParams();
            layoutarams.leftMargin = 0;
        }
        viewHolder.vTitle.setText(items.get(position).getTitol());
        viewHolder.vAutor.setText(items.get(position).getAutor());

        //Al fer click sobre un llibre
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Passem la imatge com a SharedPreferences
                SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putString("ImatgePortada", items.get(position).getImatgePortada().split(SEPARADOR_IMATGE)[0]);
                ed.commit();
                //Crea un nou intent per visualitzar la informació del llibre
                Intent intent = new Intent(context, VisualitzarInfoLlibre.class);
                intent.putExtra("Titol", items.get(position).getTitol());
                intent.putExtra("Autor", items.get(position).getAutor());
                intent.putExtra("Descripcio", items.get(position).getDescripcio());
                //intent.putExtra("ImatgePortada", items.get(position).imatgePortada);
                intent.putExtra("EditorIAny", items.get(position).getEdAndYear());
                intent.putExtra("NumPagines", items.get(position).getNumPags());
                intent.putExtra("Idioma", items.get(position).getIdioma());
                intent.putExtra("ISBN", items.get(position).getISBN());
                final String extra = ((PrincipalActivity) context).getIntent().getStringExtra(EXTRA_MESSAGE);
                intent.putExtra(EXTRA_MESSAGE, extra);
                context.startActivity(intent);
            }
        });

        /**
         * De moment no s'utilitza
         //Al fer un click llarg sobre un llibre
         viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
        //Borrem el llibre de la llista visualitzada(no de les dades guardades)
        items.remove(position);
        //Notifiquem el canvi
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
        return true;
        }
        });
         **/


        /**
         * Creem un missatge de confirmació de la reserva
         */

        // Definim els listeners
        viewHolder.vButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Crea un missatge d'alerta
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // Títol del missatge d'alerta
                alertDialogBuilder.setTitle("Reservar: ");

                // Defineix el missatge de l'alerta
                alertDialogBuilder
                        .setMessage("Vols reservar el llibre: \n" + items.get(position).getTitol() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                viewHolder.itemView.setBackgroundColor(Color.parseColor("green"));
                                viewHolder.vtext.setText("Reservat!");
                                viewHolder.vButton.setVisibility(View.GONE);
                                Toast.makeText(context, "Has reservat el llibre: \n" + items.get(position).getTitol(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // Crea un missatge d'alerta
                AlertDialog alertDialog = alertDialogBuilder.create();

                // Mostra el missatge d'alerta
                alertDialog.show();
            }
        });
    }

    /**
     * Getters i Setters
     */

    public ArrayList<Llibre> getItems() {
        return items;
    }

    public void setItems(ArrayList<Llibre> items) {
        this.items = items;
    }

    public ArrayList<Llibre> getFilterList() {
        return filterList;
    }

    public void setFilterList(ArrayList<Llibre> filterList) {
        this.filterList = filterList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFilter(FiltreLlibres filter) {
        this.filter = filter;
    }

    /**
     * Definim el nostre ViewHolder, és a dir, un element de la llista en qüestió
     */
    public static class ElMeuViewHolder extends RecyclerView.ViewHolder {
        protected ImageView vThumbnail;
        protected TextView vTitle;
        protected TextView vAutor;
        protected TextView vtext;
        protected ImageButton vButton;

        public ElMeuViewHolder(View v) {
            super(v);
            vThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            vTitle = (TextView) v.findViewById(R.id.title);
            vAutor = (TextView) v.findViewById(R.id.autor);
            vButton = (ImageButton) v.findViewById(R.id.buttonReservar);
            vtext = (TextView) v.findViewById(R.id.textReservar);
        }
    }

}