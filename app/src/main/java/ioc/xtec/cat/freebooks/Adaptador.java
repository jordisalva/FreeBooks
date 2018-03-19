package ioc.xtec.cat.freebooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jordi on 17/03/2018.
 */

public class Adaptador extends RecyclerView.Adapter<Adaptador.ElMeuViewHolder>  {
    private ArrayList<Llibre> items;
    private Context context;

    //Creem el constructor
    public Adaptador(Context context, ArrayList<Llibre> items) {
        this.context = context;
        this.items= items;
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
    //Carreguem els widgets amb les dades (l'invoca el layout manager)
    @Override
    public void onBindViewHolder(final ElMeuViewHolder viewHolder, final int position) {
        /* *
         * position conté la posició de l'element actual a la llista. També l'utilitzarem
         * com a índex per a recòrrer les dades
         * */

        //Obtenim la imatge
        String imatgePortada = items.get(position).imatgePortada;


         //Exemple pasar String base64 a imatge
         String base64String = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAA...";
         //String base64Image = imatgePortada.split(",")[1];
        String base64Image = imatgePortada;

         byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
         Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        //viewHolder.vThumbnail.setImageBitmap(decodedByte);


        //Si existiex l'imatge al directori cache
        if (imatgePortada.equals("")) {
            //Mostra una imatge per defecte
            viewHolder.vThumbnail.setBackgroundResource(android.R.drawable.ic_menu_report_image);
            viewHolder.vThumbnail.getLayoutParams().height = 250;
            viewHolder.vThumbnail.getLayoutParams().width = 250;
            viewHolder.vTitle.setText(items.get(position).titol);
            //Cas que no
        } else {
            //La carrega
            //viewHolder.vThumbnail.setImageDrawable(Drawable.createFromPath(imatgePortada));
            viewHolder.vThumbnail.setImageBitmap(decodedByte);
            viewHolder.vTitle.setText(items.get(position).titol);
        }

        //Al fer click sobre un llibre
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                //Crea un nou intent per visualitzar la informació del llibre
                Intent intent = new Intent(context, VisualitzarInfoLlibre.class);
                intent.putExtra("Titol", items.get(position).titol);
                intent.putExtra("Autor", items.get(position).autor);
                intent.putExtra("Descripcio", items.get(position).descripcio);
                intent.putExtra("ImatgePortada", items.get(position).imatgePortada);
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
        protected TextView vTitle;
        protected ImageView vThumbnail;
        public ElMeuViewHolder(View v) {
            super(v);
            vThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            vTitle = (TextView) v.findViewById(R.id.title);
        }
    }

}
