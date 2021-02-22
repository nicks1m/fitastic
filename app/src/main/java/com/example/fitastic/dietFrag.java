package com.example.fitastic;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dietFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dietFrag extends Fragment {

    private Button lossBfast;
    private Button lossLunch;
    private Button gainBfast;
    private Button gainLunch;
    private NavController controller;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ExpandableListView expandableListView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public dietFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dietFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static dietFrag newInstance(String param1, String param2) {
        dietFrag fragment = new dietFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_diet, container, false);

        controller = Navigation.findNavController(container);

        lossBfast = (Button) v.findViewById(R.id.lossBreakfastButton);
        lossLunch = (Button) v.findViewById(R.id.lossLunchButton);
        gainBfast = (Button) v.findViewById(R.id.gainBreakfastButton);
        gainLunch = (Button) v.findViewById(R.id.gainLunchButton);


        lossBfast.setOnClickListener(v1 -> {
            openLossBfast(v1);
        });

        lossLunch.setOnClickListener(v1 -> {
            openLossLunch(v1);
        });

        gainBfast.setOnClickListener(v1 -> {
            openGainBfast(v1);
        });

        gainLunch.setOnClickListener(v1 -> {
            openGainLunch(v1);
        });

        return v;

    }

    public void openLossBfast(View v){
        controller.navigate(R.id.action_dietFrag_to_lossBfast);
    }
    public void openLossLunch(View v){
        controller.navigate(R.id.action_dietFrag_to_lossLunch);
    }
    public void openGainBfast(View v){
        controller.navigate(R.id.action_dietFrag_to_gainBfast);
    }
    public void openGainLunch(View v){
        controller.navigate(R.id.action_dietFrag_to_gainLunch);
    }
}