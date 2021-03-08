package com.example.fitastic;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunHistoryFragment extends Fragment {

    /* Run history fragment provides users a fragment to look at all past runs and statistics
    from specific runs. To achieve this, this frag would need to communicate with the Database to
    retrieve this data and display it on the frag
     */

    private NavController controller;

    private FrameLayout fragmentContainer;
    private Button backBtn;
    private Button startBtn;
    private Button statsBtn;
    private Button logsBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RunHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogRunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunHistoryFragment newInstance(String param1, String param2) {
        RunHistoryFragment fragment = new RunHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // non graphical initialisations done here
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    // returns array list of int[] holding average run stats
    public ArrayList<int[]> getRunStats() {
        /* Database data should be inputted in this method, its been encapsulated in an arraylist
        * just as an example of how the process will work */
        ArrayList<int[]> statistics = new ArrayList<>();

        // generate stats as an example (this should be retrieved from database)
        int[] a = {10, 10, 10};
        int[] b = {5, 5, 5};
        int[] c = {7, 7, 7};

        // add to arrayList
        statistics.add(a);
        statistics.add(b);
        statistics.add(c);
        // return arrayList
        return statistics;
    }

    // generate text view
    @SuppressLint("SetTextI18n")
    public TextView generateStat(Context context, int i) {
        TextView stat = new TextView(getContext());
        ArrayList<int[]> allStats = getRunStats();

        stat.setText("Speed: " + allStats.get(i)[0] + " \t" +
                "Distance: " + allStats.get(i)[1] + " \t" +
                "Time: " + allStats.get(i)[2] + " \t");

        stat.setTextSize(18);
        return stat;
    }

    // creates views depending on the data parsed from getRunStats()
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_history_run, container, false);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        startBtn = root.findViewById(R.id.runStartBtn);
        statsBtn = root.findViewById(R.id.runStatBtn);
        backBtn = root.findViewById(R.id.runLogsBackBtn);


        LinearLayout myLayout = (LinearLayout)root.findViewById(R.id.runHistoryRLayout);
        for (int i = 0; i < getRunStats().size(); i++) {
            TextView myStat = generateStat(getContext(), i);
            myLayout.addView(myStat, buttonParams);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        startBtn.setVisibility(View.INVISIBLE);
        logsBtn.setVisibility(View.INVISIBLE);
        statsBtn.setVisibility(View.INVISIBLE);
        */

        controller = Navigation.findNavController(view);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStart();
            }
        });

    }

    public void openStart() {
        controller.navigate(R.id.action_runHistoryFragment_to_startFrag);
    }
}

