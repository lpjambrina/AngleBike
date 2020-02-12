package tfg.app.activity;

/**
 * Created by Laura on 18/03/2017.
 */

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import tfg.lol.R;

/**
 * {@link BaseAdapter} para poblar los videos de las bicicletas en un gridView
 */

public class AdaptadorDeVideos extends BaseAdapter {
    private Context context;
    private ArrayList<Bicycle> videosBicicletas;

    public AdaptadorDeVideos(Context context) {
        this.context = context;
        videosBicicletas = new ArrayList<Bicycle>();
        crearItemsDevideosBicicletas();
    }

    @Override
    public int getCount() {
        return videosBicicletas.size();
    }

    public Bicycle getItem(int position) {
        return videosBicicletas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
        }

        ImageView imagenVideo = (ImageView) view.findViewById(R.id.imagen_video);
        TextView nombreCoche = (TextView) view.findViewById(R.id.nombre_video);

        final Bicycle item = getItem(position);
        imagenVideo.setImageResource(item.getIdDrawable());
        Glide.with(imagenVideo.getContext())
                .load(item.getIdDrawable())
                .into(imagenVideo);
        nombreCoche.setText(item.getNombre());

        return view;
    }

    public void crearItemsDevideosBicicletas(){
        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/VIDEOS");
        for (File f : folderTfg.listFiles()) {
            if (f.isFile() && !f.getName().equals("info.txt")) {
                String name = f.getName();
                videosBicicletas.add(new Bicycle(name, R.drawable.video0));
            }
        }
    }


}
