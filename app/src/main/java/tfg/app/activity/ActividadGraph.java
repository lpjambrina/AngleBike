package tfg.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import tfg.lol.R;


public class ActividadGraph extends AppCompatActivity  {

    private String EXTRA_PARAM_ID = "";

    public static final String VIEW_NAME_HEADER_IMAGE = "imagen_compartida";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_graph);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = null;
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
        }

        EXTRA_PARAM_ID = extras.getString("EXTRA_PARAMETRO_ID");
        GraphView graph = (GraphView) findViewById(R.id.graph);
        FileInputStream In= null;
        String[] separadas=null;
        DataPoint[] puntos;
        try {
            In = new FileInputStream(Environment.getExternalStorageDirectory() + "/TFGBicy/GRAFICOS/"+EXTRA_PARAM_ID);
            int length = (int) new File(Environment.getExternalStorageDirectory() + "/TFGBicy/GRAFICOS/"+EXTRA_PARAM_ID).length();
            byte[] buffer = new byte[length];
            In.read(buffer, 0, length);
            String str = new String(buffer, "UTF-8");
            separadas = str.split(System.lineSeparator());
            In.close();
        }
        catch(FileNotFoundException e) {
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        puntos= new DataPoint[separadas.length];
        String[] splitaux;
        for(int i=0; i<separadas.length;i++) {
           splitaux=separadas[i].split("-");
            if (splitaux[1].equals(""))
                splitaux[1]=splitaux[2];

            puntos[i]= new DataPoint(Double.parseDouble(splitaux[0]), Double.parseDouble(splitaux[1]));

        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(puntos);

        graph.addSeries(series);
        //graph.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        series.setThickness(5);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<>(puntos);
        graph.addSeries(series2);
        series2.setColor(Color.RED);
        series2.setSize(10);
        series2.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series2, DataPointInterface dataPoint) {

                String[] split = EXTRA_PARAM_ID.split("angles.txt");
                String nombreVideo = split[0]+"analizado";

                Intent intent = new Intent(getApplication(), ActividadResult.class);

                intent.putExtra("EXTRA_PARAM_ID", nombreVideo);
                intent.putExtra("SEGUNDOS",dataPoint.getX());

                startActivity(intent);
            }
        });

    }


}
