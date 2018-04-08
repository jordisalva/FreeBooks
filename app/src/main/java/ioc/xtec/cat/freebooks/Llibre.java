package ioc.xtec.cat.freebooks;

/**
 * Created by jordi on 17/03/2018.
 */

public class Llibre {
    public final String titol;
    public final String autor;
    public final String descripcio;
    public final String imatgePortada;
    public final String edAndYear;
    public final String numPags;
    public final String idioma;
    public final String ISBN;

    /**
     * Constructor
     *
     * @param titol         amb el títol del llibre
     * @param autor         amb l'autor del llibre
     * @param descripcio    amb la descripció del llibre
     * @param imatgePortada amb la imatge de la portada
     * @param edAndYear     amb l'editor i l'any
     * @param numPags       amb el número de pàgines
     * @param idioma        amb l'idioma
     * @param ISBN          amb el número d'ISBN
     */
    public Llibre(String titol, String autor, String descripcio, String imatgePortada, String edAndYear, String numPags, String idioma, String ISBN) {
        this.titol = titol;
        this.autor = autor;
        this.descripcio = descripcio;
        this.imatgePortada = imatgePortada;
        this.edAndYear = edAndYear;
        this.numPags = numPags;
        this.idioma = idioma;
        this.ISBN = ISBN;
    }
}
