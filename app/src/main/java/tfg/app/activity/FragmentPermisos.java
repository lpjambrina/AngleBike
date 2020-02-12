package tfg.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import tfg.lol.R;


/**
 * Created by Laura on 22/04/2017.
 */
public class FragmentPermisos extends Fragment implements AdapterView.OnItemClickListener {

    private static final String[] PERMISSIONS = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    public FragmentPermisos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_permisos, container, false);

        Button btn =(Button)rootView.findViewById(R.id.botonPermisos);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                requestPermissions(PERMISSIONS, 1);

            }
        });

        return rootView;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    HomeFragment home = new HomeFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, home);
                    fragmentTransaction.commit();
                }
                else {

                    Boolean permisoFile = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    Boolean permisoCamera = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    Boolean permisoMicro = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO);

                    if(!permisoFile || !permisoCamera || !permisoMicro){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Se ha seleccionado 'No volver a preguntar' y hay permisos importantes no activados. Se debe hacer desde ajustes")
                                .setTitle("ADVERTENCIA")
                                .setCancelable(true)
                                .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getActivity().getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });


                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Graph item = (Graph) parent.getItemAtPosition(position);

        Intent intent = new Intent(getActivity(), ActividadGraph.class);
        intent.putExtra("EXTRA_PARAMETRO_ID", item.getNombre());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            new Pair<View, String>(view.findViewById(R.id.imagen_graph),
                                    ActividadGraph.VIEW_NAME_HEADER_IMAGE)
                    );

            ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
        }
        else
            startActivity(intent);
    }
}