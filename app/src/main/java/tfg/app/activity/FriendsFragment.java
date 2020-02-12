package tfg.app.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import tfg.lol.R;

/**
 * Created by Laura on 01/03/2017.
 */


public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private AdaptadorDeFrames adaptador;

    public FriendsFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        gridView = (GridView) rootView.findViewById(R.id.grid_Frame);
        adaptador = new AdaptadorDeFrames(rootView.getContext());
        gridView.setAdapter(adaptador);
        gridView.setOnItemClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
/*    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Frame item = (Frame) parent.getItemAtPosition(position);

        Intent intent = new Intent(getActivity(), FrameDetalle.class);
        intent.putExtra("EXTRA_PARAMETRO_ID", item.getNombre());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            new Pair<View, String>(view.findViewById(R.id.imagen_frame),
                                    FrameDetalle.VIEW_NAME_HEADER_IMAGE)
                    );

            ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
        }
        else
            startActivity(intent);
    }*/

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Frame item = (Frame) parent.getItemAtPosition(position);

        Intent intent = new Intent(getActivity(), ActividadCropImage.class);
        intent.putExtra("EXTRA_PARAMETRO_ID", item.getNombre());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            new Pair<View, String>(view.findViewById(R.id.imagen_frame),
                                    ActividadCropImage.VIEW_NAME_HEADER_IMAGE)
                    );

            ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
        }
        else
            startActivity(intent);
    }
}