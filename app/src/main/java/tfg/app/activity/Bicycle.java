package tfg.app.activity;

import java.util.ArrayList;

/**
 * Created by Laura on 18/03/2017.
 */

public class Bicycle {
    private String nombre;
    private int idDrawable;

    public Bicycle(String nombre, int idDrawable) {
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

//    public static Bicycle[] ITEMS = {
//            new Bicycle("Test0", R.drawable.video0),
//            new Bicycle("Test1", R.drawable.video1),
//            new Bicycle("Test2", R.drawable.video0),
//            new Bicycle("Test3", R.drawable.video0),
//            new Bicycle("Test4", R.drawable.video0),
//            new Bicycle("Test5", R.drawable.video0),
//            new Bicycle("Test6", R.drawable.video0),
//            new Bicycle("Test7", R.drawable.video0),
//            new Bicycle("Test8", R.drawable.video0),
//    };


    public Bicycle getItem(ArrayList<Bicycle> videosBicicletas, int id) {
        for (Bicycle item : videosBicicletas) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

}



