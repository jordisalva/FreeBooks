package ioc.xtec.cat.freebooks;

/**
 * Created by jordi on 17/03/2018.
 */

public class Llibre {
    public final String titol;
    public final String autor;
    public final String descripcio;
    public final String imatgePortada;

    /**
     * Constructor
     *
     * @param titol amb el títol del llibre
     * @param autor amb l'autor del llibre
     * @param descripcio amb la descripció del llibre
     * @param imatgePortada amb
     */
    public Llibre(String titol, String autor, String descripcio, String imatgePortada) {
        this.titol = titol;
        this.autor = autor;
        this.descripcio = descripcio;
        this.imatgePortada = imatgePortada;
}
}
