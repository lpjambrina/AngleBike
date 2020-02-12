package tfg.app.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

import tfg.lol.R;


/**
 * Actividad que muestra la imagen del item extendida
 */
public class ActividadDetalle extends AppCompatActivity {

    private String EXTRA_PARAM_ID = "";
    private double SEGUNDOS = 0;
    public static final String VIEW_NAME_HEADER_IMAGE = "imagen_compartida";
    private  VideoView videoExtendido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * Para quitar la barra de estado y hora
        * */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.actividad_detalle);

        Bundle extras = null;
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
        }
        if(extras == null) {
            EXTRA_PARAM_ID = "";
        }
        else {
            // Obtener el nombre de video de la bicicleta
            EXTRA_PARAM_ID = extras.getString("EXTRA_PARAM_ID");
            SEGUNDOS = extras.getDouble("SEGUNDOS");
        }

        //usarToolbar();
        videoExtendido = (VideoView)findViewById(R.id.video_extendido);
        cargarImagenExtendida();
    }

    private void cargarImagenExtendida() {

        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/VIDEOS");
        videoExtendido.setVideoPath(folderTfg+"/"+EXTRA_PARAM_ID);

        videoExtendido.setMediaController(new MediaController(this));

        if(SEGUNDOS == 0){

            videoExtendido.start();
            videoExtendido.requestFocus();
        }
        else {

            double castSegund = SEGUNDOS * 1000;
            int miliSegundos = (int)castSegund;

            videoExtendido.seekTo(miliSegundos);
            videoExtendido.pause();
            videoExtendido.requestFocus();
        }


    }

    private void usarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set the toolbar title
        getSupportActionBar().setTitle(EXTRA_PARAM_ID);
    }

}