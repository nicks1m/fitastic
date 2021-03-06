package com.example.fitastic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fitastic.repositories.MainRepository;
import com.example.fitastic.viewmodels.RunSummaryViewModel;
import com.example.fitastic.viewmodels.StartFragViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunSummary extends Fragment {


    private RunSummaryViewModel mViewModel;

    private NavController controller;

    private Button exitBtn;
    private ImageView imageView;

    private TextView distanceLabel;
    private TextView speedLabel;
    private TextView timeLabel;

    public RunSummary() {
        // Required empty public constructor
    }

    public static RunSummary newInstance() {
        RunSummary fragment = new RunSummary();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(RunSummaryViewModel.class);

        // callback for when main repo gets all epoch times for this user
        MainRepository.epochTimes.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                // view model handles getting the most recent run
                mViewModel.getRecentRunStats(strings);
            }
        });

        // callback for when main repo gets most recent run stats
        MainRepository.recentRun.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                // view model handle stats and posts them into stats contained in view model
                mViewModel.handleRecentRun(strings);
            }
        });

        // callback for stats on view model
        mViewModel.stats.observe(this, new Observer<ArrayList<String>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(ArrayList<String> strings) {
                // saved the recent run
                recentRun = strings;
                // sets image to be route
                setImage();
                // adds the stats to the screen
                addStats();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_run_summary, container, false);

        // initialise gui components
        imageView = root.findViewById(R.id.runImgPlaceholder);
        exitBtn = root.findViewById(R.id.exitSummaryBtn);
        distanceLabel = root.findViewById(R.id.distanceLabel);
        speedLabel = root.findViewById(R.id.paceLabel);
        timeLabel = root.findViewById(R.id.timeLabel);
        mViewModel.initialiseEpochTimes();

        return root;
    }

    public ArrayList<String> recentRun;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);


        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate back to run page
                exitSummary();
            }
        });
    }

    // adds stats to run summary
    private void addStats() {
        // convert distance to 2 decimal places
        double dist = Double.parseDouble(recentRun.get(1));
        BigDecimal db = new BigDecimal(dist).setScale(2, RoundingMode.HALF_UP);
        dist = db.doubleValue();

        // set text to corresponding stat
        distanceLabel.setText(dist + "m");
        timeLabel.setText(recentRun.get(2) + "s");
        speedLabel.setText(recentRun.get(3) + "m/s");
    }

    // sets image on run summary
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setImage() {
        // convert coded string to byte array
        byte[] data = Base64.getDecoder().decode(recentRun.get(0));
        // decode byte array to bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        // scale bitmap to image view so whole route can be seen
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                (int) (imageView.getMeasuredWidth() - (imageView.getMeasuredWidth() * 0.25)),
                (int) (imageView.getMeasuredHeight() - (imageView.getMeasuredHeight() * 0.25)),
                false));
    }

    public void exitSummary() {
        controller.navigate(R.id.action_runSummary_to_startFrag);
    }
}