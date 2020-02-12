package tfg.app.activity;


import java.util.ArrayList;


/**
 * Created by Laura on 22/04/2017.
 */

public class Graph {

    private String nombre;
    private int idDrawable;

    public Graph(String nombre, int idDrawable) {
        this.nombre = nombre;
        this.idDrawable = idDrawable;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    public int getId() {
        return nombre.hashCode();
    }


    public Graph getItem(ArrayList<Graph> graficos, int id) {
        for (Graph item : graficos) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

}



