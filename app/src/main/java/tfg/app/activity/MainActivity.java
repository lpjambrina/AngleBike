package tfg.app.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;

import tfg.lol.R;

import static tfg.lol.R.id.toolbar;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    static{
        if(OpenCVLoader.initDebug()){
            Log.i("My app", "Biblioteca includa correctamente" );
        }
        else{
            Log.i("My app", "Bibliotecat NO INCLUIDA" );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * Para quitar la barra de estado y hora
        */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // Crea una carpeta en la Raiz del sistema
        File folderTfg = new File(Environment.getExternalStorageDirectory() + "/TFGBicy");

        if(!folderTfg.exists()){
            if(folderTfg.mkdir()){ // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion

                File folderVideos = new File(folderTfg.getPath() + "/VIDEOS");

                if(!folderVideos.exists()){
                    if(folderVideos.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion

                        File archInfo = new File(folderVideos.getPath(), "info.txt"); //crea el archivo de configuracion

                        try {
                            archInfo.createNewFile();
                        }
                        catch(IOException e){

                        }
                    }
                }

                File folderVidMod = new File(folderTfg.getPath() + "/VIDEOS_MOD");

                if(!folderVidMod.exists()) {
                    if (!folderVidMod.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                        //EXCEPCION
                    }
                }


                File folderImage = new File(folderTfg.getPath() + "/IMAGENES");

                if(!folderImage.exists()) {
                    if (!folderImage.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                        //EXCEPCION
                    }
                }

                File folderGraph = new File(folderTfg.getPath() + "/GRAFICOS");

                if(!folderGraph.exists()) {
                    if (!folderGraph.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                        //EXCEPCION
                    }
                }

                File folderIntermedios = new File(folderTfg.getPath() + "/ANALISIS");

                if(!folderIntermedios.exists()) {
                    if (!folderIntermedios.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                        //EXCEPCION
                    }
                }

            }
        }
        else if(folderTfg.exists()){

            File folderVideos = new File(folderTfg.getPath() + "/VIDEOS");

            if(!folderVideos.exists()){
                if(folderVideos.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion

                    File archInfo = new File(folderVideos.getPath(), "info.txt"); //crea el archivo de configuracion

                    try {
                        archInfo.createNewFile();
                    }
                    catch(IOException e){

                    }
                }
            }

            File folderVidMod = new File(folderTfg.getPath() + "/VIDEOS_MOD");

            if(!folderVidMod.exists()) {
                if (!folderVidMod.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                    //EXCEPCION
                }
            }


            File folderImage = new File(folderTfg.getPath() + "/IMAGENES");

            if(!folderImage.exists()) {
                if (!folderImage.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                    //EXCEPCION
                }
            }

            File folderGraph = new File(folderTfg.getPath() + "/GRAFICOS");

            if(!folderGraph.exists()) {
                if (!folderGraph.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                    //EXCEPCION
                }
            }
                File folderIntermedios = new File(folderTfg.getPath() + "/ANALISIS");

                if(!folderIntermedios.exists()) {
                    if (!folderIntermedios.mkdir()) { // esta funcion crea la carpeta, y devuelve un boolean con true o false informando si hubo o no un problema en la creacion
                        //EXCEPCION
                    }
                }


        }

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(Manifest.permission.CAMERA)) {
                displayView(0);
            } else {
                displayView(-1);
            }
        }
        else
            displayView(0);

    }

    public boolean checkPermission(String permiso){

        if((ActivityCompat.checkSelfPermission(this.getApplicationContext(), permiso) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        else {
            return false;
        }
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = "";

        boolean dialogo= true;
        String cadenaDialogo = "";

        switch (position) {
            case 0:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission(Manifest.permission.CAMERA)) {
                    dialogo = false;

                    fragment = new HomeFragment();
                    title = getString(R.string.title_home);
                } else
                    cadenaDialogo = "CAMARA";
                break;
            case 1:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    dialogo = false;
                    fragment = new FriendsFragment();
                    title = getString(R.string.title_friends);
                } else
                    cadenaDialogo = "ALMACENAMIENTO";
                break;
            case 2:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    dialogo = false;
                    fragment = new GalleryFragment();
                    title = getString(R.string.title_gallery);
                }
                else
                    cadenaDialogo = "ALMACENAMIENTO";
                break;
            case 3:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    dialogo = false;
                    fragment = new GraphsFragment();
                    title = getString(R.string.title_graph);
                }
                else
                    cadenaDialogo = "ALMACENAMIENTO";
                break;
            case 4:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    dialogo = false;
                    fragment = new ResultFragment();
                    title = getString(R.string.title_result);
                }
                else
                    cadenaDialogo = "ALMACENAMIENTO";
                break;
            default:
                dialogo = false;
                fragment = new FragmentPermisos();
                break;
        }

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && dialogo){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Necesitas permisos de "+ cadenaDialogo )
                    .setTitle("Bloqueado")
                    .setCancelable(true)
                    .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });


            AlertDialog alert = builder.create();
            alert.show();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }

    }

    /*
        Para main_activity_actions
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }
    /*
        Para folder
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.btnFolder:
                Toast.makeText(this, "Folder", Toast.LENGTH_LONG).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}