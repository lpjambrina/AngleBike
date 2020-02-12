package tfg.app.activity;

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
 * Created by Laura on 22/04/2017.
 */


public class AdaptadorDeGraph extends BaseAdapter {
    private Context context;
    private ArrayList<Graph> graficos;

    public AdaptadorDeGraph(Context context) {
        this.context = context;
        graficos = new ArrayList<Graph>();
        crearItemsDeGraficos();
    }

    @Override
    public int getCount() {
        return graficos.size();
    }

    public Graph getItem(int position) {
        return graficos.get(position);
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
            view = inflater.inflate(R.layout.grid_item_graph, viewGroup, false);
        }

        ImageView imagenVideo = (ImageView) view.findViewById(R.id.imagen_graph);
        TextView nombreCoche = (TextView) view.findViewById(R.id.nombre_graph);

        final Graph item = getItem(position);
        imagenVideo.setImageResource(item.getIdDrawable());
        Glide.with(imagenVideo.getContext())
                .load(item.getIdDrawable())
                .into(imagenVideo);
        nombreCoche.setText(item.getNombre());

        return view;
    }

    public void crearItemsDeGraficos(){
        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/GRAFICOS");
        for (File f : folderTfg.listFiles()) {
            if (f.isFile() ) {
                String name = f.getName();
                graficos.add(new Graph(name, R.drawable.graph));
            }
        }
    }


}
