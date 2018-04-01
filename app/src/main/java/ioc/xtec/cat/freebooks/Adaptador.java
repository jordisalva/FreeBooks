package ioc.xtec.cat.freebooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jordi on 17/03/2018.
 */

public class Adaptador extends RecyclerView.Adapter<Adaptador.ElMeuViewHolder> implements Filterable {

    public static final String EXTRA_MESSAGE = "ioc.xtec.cat.freeboks.MESSAGE";
    public static final String SEPARADOR_IMATGE = "@LENGTH@";
    public ArrayList<Llibre> items, filterList;
    private Context context;
    FiltreLlibres filter;

    //Creem el constructor
    public Adaptador(Context context, ArrayList<Llibre> items) {
        this.context = context;
        this.items= items;
        this.filterList=items;
    }
    //Crea noves files (l'invoca el layout manager). Aquí fem referència al layout fila.xml
    @Override
    public Adaptador.ElMeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila, null);
        // create ViewHolder
        ElMeuViewHolder viewHolder = new ElMeuViewHolder(itemLayoutView);
        return viewHolder;
    }
    //Retorna la quantitat de les dades
    @Override
    public int getItemCount() {
        return items.size();
    }
    // Retorna l'objecte filtrat
    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new FiltreLlibres(filterList,this);
        }

        return filter;
    }
    //Carreguem els widgets amb les dades (l'invoca el layout manager)
    @Override
    public void onBindViewHolder(final ElMeuViewHolder viewHolder, final int position) {
        /**
         *position conté la posició de l'element actual a la llista. També l'utilitzarem
         *com a índex per a recòrrer les dades
         **/
        String base64StringImage = items.get(position).imatgePortada.split(SEPARADOR_IMATGE)[0];

        Bitmap decodedByte = null;
        try {
            byte[] decodedString = Base64.decode(base64StringImage, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.vThumbnail.setImageBitmap(decodedByte);
        viewHolder.vTitle.setText(items.get(position).titol);
        viewHolder.vAutor.setText(items.get(position).autor);

        //Al fer click sobre un llibre
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                //Passem la imatge com a SharedPreferences
                SharedPreferences pref = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putString("ImatgePortada",items.get(position).imatgePortada.split(SEPARADOR_IMATGE)[0]);
                ed.commit();
                //Crea un nou intent per visualitzar la informació del llibre
                Intent intent = new Intent(context, VisualitzarInfoLlibre.class);
                intent.putExtra("Titol", items.get(position).titol);
                intent.putExtra("Autor", items.get(position).autor);
                intent.putExtra("Descripcio", items.get(position).descripcio);
                //intent.putExtra("ImatgePortada", items.get(position).imatgePortada);
                intent.putExtra("EditorIAny", items.get(position).edAndYear);
                intent.putExtra("NumPagines", items.get(position).numPags);
                intent.putExtra("Idioma", items.get(position).idioma);
                intent.putExtra("ISBN", items.get(position).ISBN);
                final String extra = ((PrincipalActivity)context).getIntent().getStringExtra(EXTRA_MESSAGE);
                intent.putExtra(EXTRA_MESSAGE,extra);
                context.startActivity(intent);
            }
        });

        //Al fer un click llarg sobre un llibre
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Borrem el llibre de la llista visualitzada(no de les dades guardades)
                items.remove(position);
                //Notifiquem el canvi
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                return true;
            }
        });

    }

    //Definim el nostre ViewHolder, és a dir, un element de la llista en qüestió
    public static class ElMeuViewHolder extends RecyclerView.ViewHolder {
        protected ImageView vThumbnail;
        protected TextView vTitle;
        protected TextView vAutor;
        public ElMeuViewHolder(View v) {
            super(v);
            vThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            vTitle = (TextView) v.findViewById(R.id.title);
            vAutor = (TextView) v.findViewById(R.id.autor);
        }
    }

}