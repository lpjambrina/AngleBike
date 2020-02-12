package tfg.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import tfg.lol.R;


public class ActividadCropImage extends AppCompatActivity {

    public String EXTRA_PARAMETRO_ID = "";
    public float EXTRA_PARAMETRO_X;
    public float EXTRA_PARAMETRO_Y;
    public float EXTRA_PARAMETRO_WIDTH;
    public float EXTRA_PARAMETRO_HEIGHT;
    private float conversionX;
    private float conversionY;
    public static final String VIEW_NAME_HEADER_IMAGE = "name";
    private ImageView FrameExtendido;
    private DragRectView view = null;
    private Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.actividad_crop_image);

        // Escondemos el navigationBarColor
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();

            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            w.getDecorView().setSystemUiVisibility(flags);
        }

        Bundle extras = null;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
        }

        if(extras == null) {
            EXTRA_PARAMETRO_ID = "";
        } else {
            // Obtener el nombre del frame que he seleccionado en AdaptadorDeFrames
            EXTRA_PARAMETRO_ID = extras.getString("EXTRA_PARAMETRO_ID");
        }

        FrameExtendido = (ImageView) findViewById(R.id.imagerecortada);

        view = (DragRectView) findViewById(R.id.rectangulo);
        if (null != view) {
            view.setOnUpCallback(new DragRectView.OnUpCallback() {

                public void onRectFinished(final Rect rect) {
                    Toast.makeText(getApplicationContext(), " punto AZ(" + rect.left + ", " + rect.top + ")" + "WaH ("+ rect.width()+ ", " + rect.height()+")"
                            , Toast.LENGTH_LONG).show();

                    EXTRA_PARAMETRO_X =rect.left/conversionX;
                    EXTRA_PARAMETRO_Y = rect.top/conversionY;
                    EXTRA_PARAMETRO_WIDTH = rect.width()/conversionX;
                    EXTRA_PARAMETRO_HEIGHT = rect.height()/conversionY;
                }
            });
        }
        cargarFrameExtendido();

        final FloatingActionButton btnBack = (FloatingActionButton) findViewById(R.id.btnBack);
        final FloatingActionButton btnNext = (FloatingActionButton) findViewById(R.id.btnNext);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), FrameDetalle.class);

                intent.putExtra("EXTRA_PARAMETRO_ID", EXTRA_PARAMETRO_ID);
                intent.putExtra("EXTRA_PARAMETRO_X",EXTRA_PARAMETRO_X);
                intent.putExtra("EXTRA_PARAMETRO_Y",EXTRA_PARAMETRO_Y);
                intent.putExtra("EXTRA_PARAMETRO_WIDTH",EXTRA_PARAMETRO_WIDTH);
                intent.putExtra("EXTRA_PARAMETRO_HEIGHT",EXTRA_PARAMETRO_HEIGHT);

               /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ActivityOptionsCompat activityOptions =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    ActividadCropImage.this, , FrameDetalle.VIEW_NAME_HEADER_IMAGE)
                            );

                    ActivityCompat.startActivity(ActividadCropImage.this, intent, activityOptions.toBundle());
                }
                else*/
                startActivity(intent);
                ActividadCropImage.this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });


    }

    private void cargarFrameExtendido() {
        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/IMAGENES/"+ "/" + EXTRA_PARAMETRO_ID);
        if(!folderTfg.isDirectory()) {
            myBitmap = BitmapFactory.decodeFile(folderTfg.getAbsolutePath());
            float height_or = myBitmap.getHeight();
            float width_or = myBitmap.getWidth();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float width = size.x;
            float height = size.y;
            if (width > width_or)
                conversionX = width / width_or;
            else
                conversionX = width_or / width;
            if (height > height_or)
                conversionY = height / height_or;
            else
                conversionY = height_or / height;
            FrameExtendido.setImageBitmap(myBitmap);
        }

    }

}
