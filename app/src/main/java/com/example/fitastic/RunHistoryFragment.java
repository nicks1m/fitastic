package com.example.fitastic;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunHistoryFragment extends Fragment {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // method to get sql data (speed distance time)
    public ArrayList<int[]> getRunStats() {
        ArrayList<int[]> statistics = new ArrayList<>();

        // generate stats as an example (this should be retrieved from sql)
        int[] a = {10, 10, 10};
        int[] b = {5, 5, 5};
        int[] c = {7, 7, 7};

        statistics.add(a);
        statistics.add(b);
        statistics.add(c);

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

    // handle creating dynamic elements
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        View rootView = inflater.inflate(R.layout.fragment_history_run, container, false);

        LinearLayout myLayout = (LinearLayout)rootView.findViewById(R.id.runHistoryRLayout);

        for (int i = 0; i < getRunStats().size(); i++) {
            TextView myStat = generateStat(getContext(), i);
            myLayout.addView(myStat, buttonParams);
        }

        return rootView;
    }
}

