package ioc.xtec.cat.freebooks;

import android.widget.Filter;
import java.util.ArrayList;

/**
 * Created by jordi on 24/03/2018.
 */

public class FiltreLlibres extends Filter {
    Adaptador adapter;
    ArrayList<Llibre> filterList;


    public FiltreLlibres(ArrayList<Llibre> filterList,Adaptador adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    // Filtratge
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        // Valida
        if(constraint != null && constraint.length() > 0)
        {
            // Modifica l'string a majúscula
            constraint=constraint.toString().toUpperCase();
            // Guarda els llibres filtrats
            ArrayList<Llibre> llibresFiltrats=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                // Verifica les dades
                if(filterList.get(i).titol.toUpperCase().contains(constraint))
                {
                    // Afegir llibre a llibresFiltrats
                    llibresFiltrats.add(filterList.get(i));
                }
            }

            results.count=llibresFiltrats.size();
            results.values=llibresFiltrats;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.items= (ArrayList<Llibre>) results.values;

        // Nofifica els canvis a l'adaptador
        adapter.notifyDataSetChanged();
    }
}
