package tfg.app.activity;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Laura on 01/04/2017.
 */

public class Frame {
    private String nombre;
    private Bitmap Image;

    public Frame(String nombre, Bitmap Image) {
        this.nombre = nombre;
        this.Image = Image;
    }

    public String getNombre() {

        return nombre;
    }

    public Bitmap getImage() {

        return Image;
    }

    public int getId() {

        return nombre.hashCode();
    }

    public Frame getItem(ArrayList<Frame> imagenesBicicletas, int id) {
        for (Frame item : imagenesBicicletas) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

}



