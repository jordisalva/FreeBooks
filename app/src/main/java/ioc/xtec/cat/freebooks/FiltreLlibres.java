package ioc.xtec.cat.freebooks;

import android.widget.Filter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String paraulaBuscada = constraint.toString();
        String titolOnBuscar="";
        String titolOnBuscarSenseAccents="";
        boolean trobat = false;

        // Guarda els llibres filtrats
        ArrayList<Llibre> llibresFiltrats=new ArrayList<>();

        // Valida
        if(constraint != null && constraint.length() > 0)
        {

            for (int i = 0; i < filterList.size(); i++){
                //Obtenim cada un dels titols de noticies disponibles a la bdd
                titolOnBuscar = filterList.get(i).titol;
                titolOnBuscarSenseAccents = Normalizer.normalize(titolOnBuscar, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                Pattern regex = Pattern.compile("\\b" + Pattern.quote(paraulaBuscada), Pattern.CASE_INSENSITIVE);
                Matcher match = regex.matcher(titolOnBuscar);
                Matcher matchSenseAccents = regex.matcher(titolOnBuscarSenseAccents);
                //Si hi ha alguna cooincidencia tan si te accents/majúcules/mínuscules que començi per la paraula buscada
                if(match.find() || matchSenseAccents.find()){
                    trobat = true;
                    //Afegim la noticia a la llista secundaria on acumularem les coincidents amb la paraulta cercada
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
