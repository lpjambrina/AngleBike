package tfg.app.activity;

import java.util.ArrayList;

/**
 * Created by Laura on 18/03/2017.
 */

public class Result {
    private String nombre;
    private int idDrawable;

    public Result(String nombre, int idDrawable) {
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


    public Result getItem(ArrayList<Result> videosResultado, int id) {
        for (Result item : videosResultado) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

}



