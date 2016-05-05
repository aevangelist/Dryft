package com.alelievangelista.dryft.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.api.PlaceListAdapter;
import com.alelievangelista.dryft.data.PlacesContract;

public class MyTourFragment extends Fragment {

    private PlaceListAdapter placeListAdapter;
    private Toolbar toolbar;

    private ListView mListView;
    private String mSelectionClauseSaved =  PlacesContract.Places.IS_SAVED + " = ?";
    private String[] mArgsYes = new String[]{"0"};

    public MyTourFragment() {
        // Required empty public constructor
    }

    public static MyTourFragment newInstance(String param1, String param2) {
        MyTourFragment fragment = new MyTourFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_tour, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.opaque_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mListView = (ListView) view.findViewById(R.id.my_tour_list_view);

        //Set up cursor
        Cursor cursor = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseSaved, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );

        //Set up custom adapter
        placeListAdapter = new PlaceListAdapter(getActivity(), cursor, 0);
        mListView.setAdapter(placeListAdapter);
        mListView.setItemsCanFocus(false);

        return view;

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
