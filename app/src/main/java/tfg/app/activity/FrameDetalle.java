package tfg.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import tfg.lol.R;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;

public class FrameDetalle extends AppCompatActivity {

    public String EXTRA_PARAMETRO_ID = "";
    public static final String VIEW_NAME_HEADER_IMAGE = "nombre_transicion";
    private ImageView FrameExtendido;


    ProgressDialog mProgressDialog;

    public float EXTRA_PARAMETRO_X;
    public float EXTRA_PARAMETRO_Y;
    public float EXTRA_PARAMETRO_WIDTH;
    public float EXTRA_PARAMETRO_HEIGHT;

    // -- SEEKBARS
    private SeekBar seekBar1 = null;
    private SeekBar seekBar2 = null;
    private SeekBar seekBar3 = null;
    private SeekBar seekBar4 = null;
    private SeekBar seekBar5 = null;
    private SeekBar seekBar6 = null;


    private EditText text1 = null;
    private EditText text2 = null;
    private EditText text3 = null;
    private EditText text4 = null;
    private EditText text5 = null;
    private EditText text6 = null;

    //TOCCI

    private Mat imageInMat;
    private Bitmap myBitmap;
    private Bitmap myBitmapOriginal;


    int mr = 0;
    int mg = 0;
    int mb = 0;
    int Mr = 255;
    int Mg = 255;
    int Mb = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*
        * Para quitar la barra de estado y hora
        * */
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.frame_detalle);

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
        if (extras == null) {
            EXTRA_PARAMETRO_ID = "";
        } else {
            // Obtener el nombre del frame que he seleccionado en AdaptadorDeFrames
            EXTRA_PARAMETRO_ID = extras.getString("EXTRA_PARAMETRO_ID");
            EXTRA_PARAMETRO_X = extras.getFloat("EXTRA_PARAMETRO_X");
            EXTRA_PARAMETRO_Y = extras.getFloat("EXTRA_PARAMETRO_Y");
            EXTRA_PARAMETRO_WIDTH = extras.getFloat("EXTRA_PARAMETRO_WIDTH");
            EXTRA_PARAMETRO_HEIGHT = extras.getFloat("EXTRA_PARAMETRO_HEIGHT");
        }

        FrameExtendido = (ImageView) findViewById(R.id.frameDetalle);

        // --- SEEKBAR 1 --- //

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        seekBar4 = (SeekBar) findViewById(R.id.seekBar4);
        seekBar5 = (SeekBar) findViewById(R.id.seekBar5);
        seekBar6 = (SeekBar) findViewById(R.id.seekBar6);

        text1 = (EditText) findViewById(R.id.text1);
        text2 = (EditText) findViewById(R.id.text2);
        text3 = (EditText) findViewById(R.id.text3);
        text4 = (EditText) findViewById(R.id.text4);
        text5 = (EditText) findViewById(R.id.text5);
        text6 = (EditText) findViewById(R.id.text6);



        text1.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "255")});
        text2.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "255")});
        text3.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "255")});
        text4.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "255")});
        text5.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "255")});
        text6.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "255")});


        // -- SEEKBARS LISTENER -- //

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                mr = progressChanged;

                converter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                String numero = String.valueOf(progressChanged);
                text1.setText(numero.toCharArray(), 0, numero.length());

            }
        });


        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                mg = progressChanged;

                converter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                String numero = String.valueOf(progressChanged);
                text2.setText(numero.toCharArray(), 0, numero.length());

            }
        });


        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                mb = progressChanged;

                converter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                String numero = String.valueOf(progressChanged);
                text3.setText(numero.toCharArray(), 0, numero.length());

            }
        });


        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                Mr = progressChanged;

                converter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                String numero = String.valueOf(progressChanged);
                text4.setText(numero.toCharArray(), 0, numero.length());
            }
        });

        seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                Mg = progressChanged;

                converter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                String numero = String.valueOf(progressChanged);
                text5.setText(numero.toCharArray(), 0, numero.length());

            }
        });

        seekBar6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                Mb = progressChanged;

                converter();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String numero = String.valueOf(progressChanged);
                text6.setText(numero.toCharArray(), 0, numero.length());

            }
        });


        // -- TEXTS LISTENER -- //
        text1.setText("0");
        text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = text1.getText().toString();
                int progressChanged;

                if(str.equals(""))
                    progressChanged = 0;
                else
                    progressChanged = Integer.parseInt(str);

                seekBar1.setProgress(progressChanged);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text2.setText("0");
        text2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = text2.getText().toString();
                int progressChanged;

                if(str.equals(""))
                    progressChanged = 0;
                else
                    progressChanged = Integer.parseInt(str);

                seekBar2.setProgress(progressChanged);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text3.setText("0");
        text3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = text3.getText().toString();
                int progressChanged;

                if(str.equals(""))
                    progressChanged = 0;
                else
                    progressChanged = Integer.parseInt(str);

                seekBar3.setProgress(progressChanged);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text4.setText("255");
        text4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = text4.getText().toString();
                int progressChanged;

                if(str.equals(""))
                    progressChanged = 0;
                else
                    progressChanged = Integer.parseInt(str);

                seekBar4.setProgress(progressChanged);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text5.setText("255");
        text5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = text5.getText().toString();
                int progressChanged;

                if(str.equals(""))
                    progressChanged = 0;
                else
                    progressChanged = Integer.parseInt(str);

                seekBar5.setProgress(progressChanged);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text6.setText("255");
        text6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = text6.getText().toString();
                int progressChanged;

                if(str.equals(""))
                    progressChanged = 0;
                else
                    progressChanged = Integer.parseInt(str);

                seekBar6.setProgress(progressChanged);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final FloatingActionButton btnBackFrame = (FloatingActionButton) findViewById(R.id.btnBackFrame);
        final FloatingActionButton btnVideo = (FloatingActionButton) findViewById(R.id.btnGeneraVideo);

        btnBackFrame.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                FrameDetalle.super.onBackPressed();
                FrameDetalle.this.overridePendingTransition(R.anim.left_out, R.anim.right_out);
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                new ProgressDialogAsyncTask().execute();

            }

        });

        cargarFrameExtendido();

    }


    public class ProgressDialogAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(FrameDetalle.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle("Procesando...");
            mProgressDialog.setMessage("Analizando video ...");
            mProgressDialog.setProgressStyle(mProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(100);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            Double[] parameters = new Double[4];
            //myBitmap.recycle();
           // myBitmapOriginal.recycle();
            System.gc();
            try {
                parameters = analisisVideo(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(mProgressDialog.getProgress()<50)
                mProgressDialog.incrementProgressBy(1);
            publishProgress();
            try {
                reconstruirVideo(parameters[0],  parameters[1], parameters[2], parameters[3]);
            }
            catch (FFmpegFrameRecorder.Exception e)
            {
                reconstruirVideo3gp(parameters[0],  parameters[1], parameters[2], parameters[3]);
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(mProgressDialog.getProgress() == 50) {
                mProgressDialog.setMessage("Reconstruyendo video...");
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


    private void cargarFrameExtendido() {
        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/IMAGENES/");
        File folderImg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy/ANALISIS/");
        Mat original =  Imgcodecs.imread(folderTfg.getAbsolutePath() + "/" + EXTRA_PARAMETRO_ID);
        float rstart= EXTRA_PARAMETRO_Y;
        float rend=EXTRA_PARAMETRO_Y+EXTRA_PARAMETRO_HEIGHT;
        float cstart=EXTRA_PARAMETRO_X;
        float cend=EXTRA_PARAMETRO_X+EXTRA_PARAMETRO_WIDTH;
        Mat sub;
         if(rstart != 0 && rend!=0&& cstart != 0 && cend !=0)
         sub=original.submat(Math.round(rstart),Math.round(rend),Math.round(cstart),Math.round(cend));
        else
          sub=original;
        Imgcodecs.imwrite(folderImg.getAbsolutePath() + "/leer.jpg", sub);
        myBitmap = BitmapFactory.decodeFile(folderImg.getAbsolutePath() + "/leer.jpg");
        myBitmapOriginal = BitmapFactory.decodeFile(folderImg.getAbsolutePath() + "/leer.jpg");

        FrameExtendido.setImageBitmap(myBitmapOriginal);
    }


    public void converter(){

        imageInMat = new Mat(myBitmapOriginal.getWidth(), myBitmapOriginal.getHeight(), CvType.CV_8U);

        Utils.bitmapToMat(myBitmapOriginal, imageInMat);

        if(imageInMat.empty()== true)
        {System.out.println("Error no image found!!");}
        else
            System.out.println("Image loaded");

        Mat thresh = new Mat();

        Scalar lower = new Scalar(mb,mg,mr);

        Scalar higher = new Scalar(Mb,Mg,Mr);

        Mat mHSV = new Mat();

        Imgproc.cvtColor(imageInMat, mHSV, Imgproc.COLOR_BGR2HSV, 3);
        Imgproc.cvtColor(mHSV, mHSV, Imgproc.COLOR_HSV2RGB, 3);
        Core.inRange(mHSV, lower, higher, thresh);
        Imgproc.cvtColor(imageInMat, imageInMat, Imgproc.COLOR_BGR2HSV, 3);
        Imgproc.cvtColor(imageInMat, imageInMat, Imgproc.COLOR_HSV2RGB, 3);

        Imgproc.cvtColor(thresh, thresh, Imgproc.COLOR_GRAY2RGB, 3);
        Core.bitwise_and(thresh, imageInMat,thresh);
        Imgproc.cvtColor(thresh, thresh, Imgproc.COLOR_BGR2RGBA, 3);

        Utils.matToBitmap(thresh, myBitmap);

        FrameExtendido.setImageBitmap(myBitmap);

    }
    private Double[] analisisVideo(boolean reconstruccion) throws  Exception {
        long startTime = System.currentTimeMillis();
        Double[] ret = new Double[4];
        //Inicializacion del capturador
        String imagen = EXTRA_PARAMETRO_ID;
        String video =imagen.split("\\.")[0];
        FileWriter fw = null;
        try {
            fw = new FileWriter(Environment.getExternalStorageDirectory()+"/TFGBicy/GRAFICOS/"+video+"angles.txt");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        FFmpegFrameGrabber grab = new FFmpegFrameGrabber(Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS/"+video+".mp4");
        org.bytedeco.javacv.Frame captured_frame;


        try {
            grab.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        double frames = grab.getLengthInFrames();
        double FPS = grab.getFrameRate();
        double height = grab.getImageHeight();
        double width = grab.getImageWidth();
        ret[0]=width;
        ret[1]=height;
        ret[2]=FPS;
        ret[3]=frames;

        //Variables de las transformaciones
        Mat thresh=new Mat();
        Mat color = new Mat();
        Scalar lower = new Scalar(mb,mg,mr);
        Scalar higher = new Scalar(Mb,Mg,Mr);
        Mat hierarchy = new Mat() ;
        Vector<Point> centers = new Vector<>();
        Vector<Float> radii = new Vector<>();
        List<MatOfPoint> contours = new ArrayList<>();
        Scalar line = new Scalar(255,255,255);
        Size s = new Size(11,11);
        MatOfPoint2f search = new MatOfPoint2f();
        int count;
        double second=0.0;
        int loop=0;
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2,2));
        System.out.println("Empezado el proceso");
        System.out.println(frames);
        float rstart= EXTRA_PARAMETRO_Y;
        float rend=EXTRA_PARAMETRO_Y+EXTRA_PARAMETRO_HEIGHT;
        float cstart=EXTRA_PARAMETRO_X;
        float cend=EXTRA_PARAMETRO_X+EXTRA_PARAMETRO_WIDTH;
        OpenCVFrameConverter.ToMat convertertoMat= new OpenCVFrameConverter.ToMat();
        opencv_core.IplImage image= new opencv_core.IplImage();
        String inputImage = Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/vuelo.jpg";
        FileInputStream In= null;
        Integer orientacion=0;
        Mat ROI=null;
        Double angulo=0.0;
        //Pruebas correcion
        boolean failLocal=false;
        boolean failGlobal=false;
       // int failNumber=0;
        double previousAngle=0.0;
        double previousSecond=0.0;
        double progressOr=frames/50;
        double progress=progressOr;
        try {
            In = new FileInputStream(Environment.getExternalStorageDirectory() + "/TFGBicy/ANALISIS/orientacion.txt");
            int length = (int) new File(Environment.getExternalStorageDirectory() + "/TFGBicy/ANALISIS/orientacion.txt").length();
            byte[] buffer = new byte[length];
            In.read(buffer, 0, length);
            String str = new String(buffer, "UTF-8");
            orientacion = Integer.parseInt(str);
            In.close();
        }
        catch(FileNotFoundException e) {
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Comienza el analisis
        while(loop<frames)
        {
            if(loop==frames-1)
                System.out.print(false);
            second=loop/FPS;
            //Se lee el frame
            try {
                captured_frame=grab.grabImage();
                image=convertertoMat.convertToIplImage(captured_frame);
            } catch (FrameGrabber.Exception e) {
                loop++;
            }
        if(image!=null) {
            //Se guarda y se vuelve a leer ya en OpenCV
            cvSaveImage(inputImage, image);
            thresh = Imgcodecs.imread(inputImage);
            thresh = girarMat(thresh, thresh, orientacion);

            if (reconstruccion) {
                color = thresh.clone();
                if (rstart != 0 && rend != 0 && cstart != 0 && cend != 0) {
                    thresh = thresh.submat(Math.round(rstart), Math.round(rend), Math.round(cstart), Math.round(cend));
                    ROI = new Mat(color, new Rect(Math.round(cstart), Math.round(rstart), Math.round(cend - cstart), Math.round(rend - rstart)));
                }
                else
                    ROI = new Mat(color,new Rect(0,0,color.cols(),color.rows()));
            } else if (rstart != 0 && rend != 0 && cstart != 0 && cend != 0)
                thresh = thresh.submat(Math.round(rstart), Math.round(rend), Math.round(cstart), Math.round(cend));


            //Se difumina y se filtra para mejorar la precision
            Imgproc.GaussianBlur(thresh, thresh, s, 0, 0, Core.BORDER_DEFAULT);
            Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_CLOSE, kernel);

            //La imagen leida y una copia en color en caso de que se vaya a recomponer el video con el analisis


            //Se hace el filtro por color y se buscan los contornos
            Core.inRange(thresh, lower, higher, thresh);
            Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);


            count = contours.size();

            //Encontrados los contornos se busca el minimo circulo recubridor, filtrando puntos cercanos
            for (int i = 0; i < count; i++) {
                searchContourCircles(contours, centers, radii, i, search);
            }

            count = centers.size();
            if (count > 3)
                System.out.println("Hay mas de 3 contornos:" + count);

            //Para cada contorno se dibuja un circulo y se une con el punto anterior si se quiere reconstruir el video
            if (reconstruccion) {

                for (int i = 0; i < count; i++) {
                    if (rstart != 0 && rend != 0 && cstart != 0 && cend != 0) {
                        if (i > 0) Imgproc.line(ROI, centers.get(i), centers.get(i - 1), line, 4);
                        int radius = Math.round(radii.get(i));
                        Imgproc.circle(ROI, centers.get(i), radius, line, 4);
                    }
                    else {
                        if (i > 0) Imgproc.line(color, centers.get(i), centers.get(i - 1), line, 4);
                        int radius = Math.round(radii.get(i));
                        Imgproc.circle(color, centers.get(i), radius, line, 4);

                    }

                }
            }

            //Se calcula el angulo
            if (count >= 3) {

                angulo = angleBetween(centers.get(1), centers.get(0), centers.get(2));
                if (failLocal) {
                    try {
                        fw.write(Double.toString(previousSecond) + "-" + Double.toString((angulo + previousAngle) / 2) + System.lineSeparator());
                        fw.write(Double.toString(second) + "-" + Double.toString(angulo) + System.lineSeparator());
                        failLocal = false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    try {
                        fw.write(Double.toString(second) + "-" + Double.toString(angulo) + System.lineSeparator());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                if (reconstruccion) {
                    if (rstart != 0 && rend != 0 && cstart != 0 && cend != 0)
                    Imgproc.putText(ROI, Double.toString(angulo), centers.get(1), Core.FONT_ITALIC, 0.5, line);
                    else
                        Imgproc.putText(color, Double.toString(angulo), centers.get(1), Core.FONT_ITALIC, 0.5, line);
                }

            } else {
                failLocal = true;
                failGlobal = true;
            }

            //Se escribe el angulo actual en el documento de texto

            previousAngle = angulo;

            //Se escribe el angulo en el video si se va a reconstruir


            //Se limpian los elementos usados en el bucle
            centers.removeAllElements();
            radii.removeAllElements();
            hierarchy.release();
            contours.clear();


            //Checkeo para limpiar variables sin utilizar
            if (loop % 20 == 1)
                System.gc();

            //Se guarda imagen a imagen si se va a reconstruir despues
            if (reconstruccion) {
               // Imgcodecs.imwrite(Environment.getExternalStorageDirectory() + "/TFGBicy/ANALISIS/aa" + loop + ".jpg", ROI);
                Imgcodecs.imwrite(Environment.getExternalStorageDirectory() + "/TFGBicy/ANALISIS/r" + loop + ".jpg", color);
            }
        }
            loop++;
            previousSecond=second;
           // System.out.println("Quedan :"+(frames-loop));
            thresh.release();
            if(loop >= progress) {
                while(progress<loop){
                mProgressDialog.incrementProgressBy(1);
                progress+=progressOr;
                }
            }
        }
        try {
            fw.close();
            grab.close();
            grab.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Acaba el programa
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        if(!reconstruccion)
          System.out.println("Terminado el proceso(Analisis) en " + totalTime/1000 + " segundos");


        //Se liberan recursos

        grab = null;
        captured_frame = null;
        convertertoMat= null;
        fw = null;

        color.release();
        lower = null;
        higher  = null;
        hierarchy.release();
        centers.clear();

        radii.clear();
        contours.clear();
        line = null;
        s = null;
        kernel.release();

        if(image!=null) {
            image.close();
            image.release();
        }
        inputImage = null;

        System.gc();
        if(reconstruccion) {
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("Terminado el proceso(Analisis) en " + totalTime/1000 + " segundos");
        }

        /*if(failGlobal)
            Toast.makeText(getApplicationContext(), "Hubo un fallo en uno o varios frame, se calcularon aproximadamente. Intenta ajustar mejor los parametros", Toast.LENGTH_SHORT).show();*/
    return ret;
    }

    private void reconstruirVideo(double width, double height,double fps, double frames) throws FFmpegFrameRecorder.Exception {
        long startTime = System.currentTimeMillis();
        String imagen = EXTRA_PARAMETRO_ID;
        String video =imagen.split("\\.")[0];
        org.bytedeco.javacv.Frame captured_frame;
        double progressOr=frames/50;
        double progress=progressOr;
        //Creamos el archivo del video reconstruido
        String videoMod = Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS_MOD/"+video+"analizado.mp4";

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoMod,(int)height,(int)width);
        recorder.setFrameRate(fps);
        int i=0;
        OpenCVFrameConverter.ToMat convertertoMat= new OpenCVFrameConverter.ToMat();
        String inputImage = Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/r0.jpg";
            recorder.start();
            //Leemos la primera imagen
            opencv_core.IplImage image = cvLoadImage(inputImage);
            captured_frame=convertertoMat.convert(image);

            //Iniciamos la reconstruccion del video
            while (i < frames) {
                try {

                    if ((i%20) == 1)
                        System.gc();
                    if (captured_frame.image == null) {
                        System.out.println("!!! Failed cvQueryFrame");
                        break;
                    }
                    //Aqui se graba imagen a imagen del video reconstruido
                    recorder.record(captured_frame);
                    if(i >= progress) {
                        while(progress<i){
                            mProgressDialog.incrementProgressBy(1);
                            progress+=progressOr;
                        }
                    }
                    i++;
                    inputImage = Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/r"+i+".jpg";
                    image = cvLoadImage(inputImage);
                    captured_frame=convertertoMat.convert(image);

                } catch (Exception e) {
                    e.printStackTrace();
                    i++;
                }
            }
            recorder.stop();
            recorder.release();
        limpiarImagenes();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Terminado el proceso(Reconstruir) en " + totalTime/1000 + " segundos");
    }



    private void reconstruirVideo3gp(double width, double height,double fps, double frames) {
        long startTime = System.currentTimeMillis();
        double progressOr=frames/50;
        double progress=progressOr;
        String imagen = EXTRA_PARAMETRO_ID;
        String video =imagen.split("\\.")[0];
        org.bytedeco.javacv.Frame captured_frame;

        //Creamos el archivo del video reconstruido
        String videoMod = Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS_MOD/"+video+"analizado.3gp";

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoMod,(int)height,(int)width);
        recorder.setFrameRate(fps);
        int i=0;
        OpenCVFrameConverter.ToMat convertertoMat= new OpenCVFrameConverter.ToMat();
        String inputImage = Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/r0.jpg";
        try {
            recorder.start();
            //Leemos la primera imagen
            opencv_core.IplImage image = cvLoadImage(inputImage);
            captured_frame=convertertoMat.convert(image);

            //Iniciamos la reconstruccion del video
            while (i < frames) {
                try {

                    if ((i%20) == 1)
                        System.gc();
                    if (captured_frame.image == null) {
                        System.out.println("!!! Failed cvQueryFrame");
                        break;
                    }
                    //Aqui se graba imagen a imagen del video reconstruido
                    recorder.record(captured_frame);
                    if(i >= progress) {
                        while(progress<i){
                            mProgressDialog.incrementProgressBy(1);
                            progress+=progressOr;
                        }
                    }
                    i++;
                    inputImage = Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/r"+i+".jpg";
                    image = cvLoadImage(inputImage);
                    captured_frame=convertertoMat.convert(image);

                } catch (Exception e) {
                    e.printStackTrace();
                    i++;
                }
            }
            recorder.stop();
            recorder.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
        limpiarImagenes();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Terminado el proceso(Reconstruir) en " + totalTime/1000 + " segundos");
    }

    private void limpiarImagenes() {
        int i=0;
        File borrar = new File(Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/r"+i+".jpg");
        while(borrar.delete()){
            i++;
            borrar = new File(Environment.getExternalStorageDirectory()+"/TFGBicy/ANALISIS/r"+i+".jpg");
        }
    }

    private static boolean checkCoords(Point centers, Point c) {
        return !(Math.abs(centers.x-c.x)<20 || Math.abs(centers.y-c.y)<20);
    }

    private static void searchContourCircles( List<MatOfPoint> contours,Vector<Point> centers,Vector<Float> radii,int i,MatOfPoint2f search)
    {
        Point c = new Point();
        float[] r = new float[1];
        search.fromArray(contours.get(i).toArray());
        Imgproc.minEnclosingCircle(search, c, r);
        //centers.add(c);
        //radii.add(r[0]);
        if(centers.size()==0){
            centers.add(c);
            radii.add(r[0]);
        }
        else if (checkCoords(centers.get((centers.size()-1)),c)){
            centers.add(c);
            radii.add(r[0]);
        }
        c=null;
        search.release();
        r=null;

    }

    private static double angleBetween(Point center, Point current, Point previous) {

        double angle= Math.toDegrees(Math.atan2(current.x - center.x,current.y - center.y)-
                Math.atan2(previous.x- center.x,previous.y- center.y));
        if(angle<0)
            angle=Math.abs(angle);
        return (angle);
    }

    private Mat girarMat(Mat image, Mat girada, int orientacion)
    {
        Point center = new Point( image.cols()/2, image.rows()/2);
        Mat M = Imgproc.getRotationMatrix2D(center,-orientacion,1);
        Rect bbox = new RotatedRect(center,image.size(),-orientacion).boundingRect();
        M.put(0, 2,  M.get(0,2)[0] + bbox.width/2.0 - center.x);
        M.put(1, 2,  M.get(1,2)[0] + bbox.height/2.0 - center.y);
        Imgproc.warpAffine(image,girada,M,bbox.size());
        return girada;
    }
}

