package tfg.app.activity;

/**
 * Created by Laura on 02/04/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import tfg.lol.R;

/**
 * Necesita FriendsFragment, AdaptadorDeFrames, Frame y FrameDetalle
 * {@link BaseAdapter} para probar los frames de los videos de las Bicicletas en un gridView
 */

public class AdaptadorDeFrames extends BaseAdapter {
    private Context context;
    private ArrayList<Frame> imagenesBicicletas;

    public AdaptadorDeFrames(Context context) {
        this.context = context;
        imagenesBicicletas = new ArrayList<Frame>();
        crearItemsDeimagenesBicicletas();
    }

    @Override
    public int getCount() {
        return imagenesBicicletas.size();
    }

    public Frame getItem(int position) {
        return imagenesBicicletas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item_frame, viewGroup, false);
        }

        ImageView imagenFrame = (ImageView) view.findViewById(R.id.imagen_frame);
        TextView nombreFrame = (TextView) view.findViewById(R.id.nombre_frame);

        final Frame item = getItem(position);
        imagenFrame.setImageBitmap(item.getImage());
       // Glide.with(imagenFrame.getContext()).load(item.getImage()).asBitmap().into(imagenFrame);
       // Glide.with(imagenFrame.getContext()).load(item.getImage()).into(imagenFrame);
        nombreFrame.setText(item.getNombre());

        return view;
    }

    public void crearItemsDeimagenesBicicletas(){
        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/IMAGENES");
        for (File f : folderTfg.listFiles()) {
            if (f.isFile() && !f.getName().equals("info.txt")) {
                String name = f.getName();
                File imgFile = new  File(folderTfg.getPath());
                Bitmap myImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath() + "/" + name);
                imagenesBicicletas.add(new Frame(name, myImage));
            }
        }
    }


}
