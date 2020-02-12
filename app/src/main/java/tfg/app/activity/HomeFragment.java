package tfg.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import tfg.lol.R;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;


/**
 * Created by Laura on 05/03/2017.
 */


public class HomeFragment extends Fragment implements SurfaceHolder.Callback {

    // Chronometer
    Chronometer crono;

    //EMPIECE CAMARA
    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;

    private SurfaceHolder holder;
    private SurfaceView surface;

    private Camera mCamera;
    private Camera.Size selected;
    private int orientacion;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int MY_PERMISSIONS_REQUEST_AUDIO = 3;
    private String fileName = null;

    private static final int PERMISSION_ALL = 1;
    private static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private int numVideo = 0;
    private boolean recording = false;
    private int permissionCheckAlmacenamiento;
    private int permissionCheckCamera;
    private int permissionCheckAudio;

    public HomeFragment() {
        // Required empty public constructor
    }


    // FIN CAMARA

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Empieza CAMARA

        /**
         * inicializamos la "superficie" donde se reproducir la vista previa de la grabacion
         * y luego el video ya grabado
         */
        surface = (SurfaceView)rootView.findViewById(R.id.imagenGrabacion);
        holder = surface.getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);

        //comprueba todos los permisos
//        if(!hasPermissions(getContext(), PERMISSIONS)){
//            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
//        }

        // Inicializo el Chronometer
        crono = (Chronometer) rootView.findViewById(R.id.counter);
        crono.setVisibility(View.INVISIBLE);

        // Inicializo los buttons
        final FloatingActionButton btnRec = (FloatingActionButton)rootView.findViewById(R.id.btnRec);
        final FloatingActionButton btnStop = (FloatingActionButton)rootView.findViewById(R.id.btnStop);

        // final FloatingActionButton btnPlay = (FloatingActionButton)rootView.findViewById(R.id.btnPlay);


        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (hasPermissions(getContext(), PERMISSIONS) && memoriaLibre()) {

                    // LECTURA DEL ARCHIVO "info.txt"
                    FileInputStream In= null;

                    try {
                        In = new FileInputStream(Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS/" + "info.txt");
                        int length = (int) new File(Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS/info.txt").length();
                        byte[] buffer = new byte[length];
                        In.read(buffer, 0, length);
                        if(length == 0) {
                            numVideo = 0;
                        }
                        else{
                            String str = new String(buffer, "UTF-8");
                            numVideo = Integer.parseInt(str);
                            System.out.print("este es el numero" + numVideo);
                        }

                        In.close();
                    }
                    catch(FileNotFoundException e) {
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }


                    btnRec.setEnabled(false);
                    btnRec.setVisibility(View.INVISIBLE);
                    btnStop.setEnabled(true);
                    btnStop.setVisibility(View.VISIBLE);

                    // Chronometer
                    crono.setVisibility(View.VISIBLE);
                    crono.setBase(SystemClock.elapsedRealtime());
                    crono.start();

                    mCamera.unlock();
                    prepareRecorder();

                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DATE);
                    int month = c.get(Calendar.MONTH)+1;
                    int year = c.get(Calendar.YEAR);

                    fileName = Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS/" + year + month + day + numVideo +".mp4"; // Ruta para la grabacion
                    numVideo++;

                    // ESCRITURA DEL ARCHIVO info.txt

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS/" + "info.txt");
                        byte[] buffer = String.valueOf(numVideo).getBytes();
                        fos.write(buffer, 0, buffer.length);
                        fos.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(fos != null)
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }

                    mediaRecorder.setOutputFile(fileName);

                    mediaRecorder.setOrientationHint(orientacion);

                    try {
                        mediaRecorder.prepare();

                    } catch (IllegalStateException e) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaRecorder.start();
                    recording = true;
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                }
            }
        });

        /**
         * Boton para detener
         */
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Buttons
                btnStop.setEnabled(false);
                btnStop.setVisibility(View.INVISIBLE);
                btnRec.setEnabled(true);
                btnRec.setVisibility(View.VISIBLE);
                //btnPlay.setEnabled(true);

                // Chronometer
                crono.stop();


                if (recording) {
                    recording = false;
                    mediaRecorder.stop();
                    mediaRecorder.reset();

                    /**
                     * Si se est� reproduciendo, detenemos la reproducci�n y reiniciamos la configuraci�n
                     */
                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                crono.setVisibility(View.INVISIBLE);

                primerFrame();



            }




        });


        // Inflate the layout for this fragment
        return rootView;
    }



    public static boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean memoriaLibre(){

        String freeBytes = bytesToHuman(FreeMemory()); //memoria libre en bytes

        boolean libre;
        String[] splitByte =  freeBytes.split(" ");

        if(splitByte[1].equals("Mb")|| splitByte[1].equals("Gb")|| splitByte[1].equals("Tb")|| splitByte[1].equals("Pb")|| splitByte[1].equals("Eb")){

            if(splitByte[1].equals("Mb") && Double.parseDouble(splitByte[0]) < 70) { // Si son Mb y si son menos que 10 Mb entonces no hay suficiente espacio

                libre = false;
            }
            else
                libre = true;

        }
        else
            libre = false;

        if(!libre){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Espacio insuficiente en memoria")
                    .setTitle("Información")
                    .setCancelable(true)
                    .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ver Almacenamiento", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS), 0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        return libre;
    }

    public long FreeMemory()
    {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long   Free   = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize()); //multiplica el numero de bloques libres por el tamaño de cada bloques en bytes
        return Free;
    }

    public static String bytesToHuman(long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return String.valueOf(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return String.valueOf((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return String.valueOf((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return String.valueOf((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return String.valueOf((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return String.valueOf((double)size / Pb) + " Pb";
        if (size >= Eb)                 return String.valueOf((double)size / Eb) + " Eb";

        return "???";
    }


    // EMPIEZA CAMERA


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes(); // tamaños soportados por la pantalla de la vista previa

        //Funciona en el movil de Laura con 1, con 0 en otros
        //selected = sizes.get(1);
        selected = getOptimalPreviewSize(sizes, width, height);

        params.setPreviewSize(selected.width, selected.height); //Introduce los tamaños optimos previamente buscados

        mCamera.setParameters(params); // asigna los parametros a la camara
        mCamera.startPreview(); // empieza la vista previa
    }



    /**
     * Inicializamos los recursos asociados a las variables para administrar la grabaci�n y reproducci�n.
     * Se verifica si las variables son nulas (para ejecutar este c�digo solo una vez) y luego de
     * inicializarlas se coloca el SurfaceHolder como display para la vista previa de la grabaci�n y
     * para la vista de la reproducci�n
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mCamera = Camera.open();

        orientacion = setCameraDisplayOrientation(mCamera); // obtengo la orientacion correcta de mi dispositivo
        FileWriter ori=null;
        try {
            ori = new FileWriter(Environment.getExternalStorageDirectory() + "/TFGBicy/ANALISIS/orientacion.txt");
            ori.write(String.valueOf(orientacion));
            ori.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.setDisplayOrientation(orientacion);

        try
        {
            mCamera.setPreviewDisplay(this.holder);

        } catch(Exception e){

        }
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setPreviewDisplay(this.holder.getSurface());
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(holder);
        }
    }

    /**
     * Liberamos los recursos asociados a las variables para administrar la grabaci�n y reproducci�n
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        mediaRecorder.release();
        mediaPlayer.release();
        Log.i("PREVIEW","surfaceDestroyed");
    }


    public void prepareRecorder(){
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        }
        else{
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }
        */

        mediaRecorder.setVideoSize(selected.width, selected.height);
    }


    private int setCameraDisplayOrientation(android.hardware.Camera camera) {

        Camera.Parameters parameters = camera.getParameters();

        android.hardware.Camera.CameraInfo camInfo = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (camInfo.orientation - degrees + 360) % 360;
        }

        return result;
    }

    private int getBackFacingCameraId() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                cameraId = i;
                break;
            }
        }

        return cameraId;
    }



    //Busca el tamaño optimo de la vista de la pantalla
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.05;

        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    //FIN CAMERA

    private void primerFrame() {

        FFmpegFrameGrabber grab = new FFmpegFrameGrabber(fileName);
        org.bytedeco.javacv.Frame captured_frame;
        OpenCVFrameConverter.ToMat convertertoMat= new OpenCVFrameConverter.ToMat();
        opencv_core.IplImage image= new opencv_core.IplImage();

        //Variables para el nombre
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        int antiguonum=numVideo-1;
        String inputImage = Environment.getExternalStorageDirectory()+"/TFGBicy/IMAGENES/" + year + month + day + antiguonum +".jpg";

        try {
            grab.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        try {
            captured_frame=grab.grabImage();
            image=convertertoMat.convertToIplImage(captured_frame);
        } catch (FrameGrabber.Exception e){
            e.printStackTrace();
        }

        //Se guarda el primer frame
        cvSaveImage(inputImage,image);
        Mat or= Imgcodecs.imread(inputImage);
        Mat girada = new Mat();
        girarMat(or,girada,orientacion);
        Imgcodecs.imwrite(inputImage,girada);
        //Se liberan recursos
        grab = null;
        captured_frame = null;
        convertertoMat= null;
        image=null;
        c=null;
       System.gc();

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

    //Metodo original de conversion para posterior analisis en OpenCV, no usa por haber decidido analizar sin convertir
    private void conversionVideo() {
        long startTime = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);

        //FFmpegFrameGrabber grab = new FFmpegFrameGrabber(fileName);
        FFmpegFrameGrabber grab = new FFmpegFrameGrabber(Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS/out.avi");
        org.bytedeco.javacv.Frame captured_frame;
        int antiguonum=numVideo-1;
        try {
            grab.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        String videoMod = Environment.getExternalStorageDirectory()+"/TFGBicy/VIDEOS_MOD/" + year + month + day + antiguonum +".avi";
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoMod,grab.getImageWidth(),grab.getImageHeight());
        recorder.setVideoCodec(8);
        double fps = recorder.getFrameRate();
        recorder.setFrameRate(fps);
        int frames;
        long secs;
        int i=0;
        try {
            recorder.start();
            frames = grab.getLengthInFrames();
            // secs = grab.getLengthInTime();
            captured_frame = grab.grabImage();
            while (i < frames) {
                try {

                    if ((i%30) == 1)
                        System.gc();
                    if (captured_frame.image == null) {
                        System.out.println("!!! Failed cvQueryFrame");
                        break;
                    }
                    //AQUI LO GRABA
                    recorder.record(captured_frame);

                    captured_frame = grab.grabImage();
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    i++;
                }
            }
            recorder.stop();
            recorder.release();
            grab.stop();
            grab.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoCapture capture = new VideoCapture(videoMod);
        Mat primerFrame = new Mat();
        Mat girada = new Mat();
        capture.read(primerFrame);
        Point center = new Point( primerFrame.cols()/2, primerFrame.rows()/2);
        Mat M = Imgproc.getRotationMatrix2D(center,-90,1);
        Rect bbox = new RotatedRect(center,primerFrame.size(),-90).boundingRect();
        M.put(0, 2,  M.get(0,2)[0] + bbox.width/2.0 - center.x);
        M.put(1, 2,  M.get(1,2)[0] + bbox.height/2.0 - center.y);
        Imgproc.warpAffine(primerFrame,girada,M,bbox.size());
        Imgcodecs.imwrite(Environment.getExternalStorageDirectory()+"/TFGBicy/IMAGENES/" + year + month + day + antiguonum +".jpg",girada);
        capture.release();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Terminado el proceso en " + totalTime/1000 + " segundos");
    }


}