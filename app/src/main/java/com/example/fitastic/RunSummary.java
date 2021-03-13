package com.example.fitastic;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.fitastic.utility.RunDbUtility;
import com.example.fitastic.viewmodels.RunSummaryViewModel;
import com.example.fitastic.viewmodels.StartFragViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunSummary extends Fragment {

    private static String TAG = "RunSummary"; 
    
    private RunSummaryViewModel mViewModel;

    private NavController controller;

    private Button exitBtn;
    private ImageView imageView;

    private TextView distanceLabel;
    private TextView speedLabel;
    private TextView timeLabel;
    private TextView dateLabel;
    private TextView pointsLabel;

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
        Log.d(TAG, "onCreate: ");
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
        Log.d(TAG, "onCreateView: ");
        View root = inflater.inflate(R.layout.fragment_run_summary, container, false);

        // initialise gui components
        imageView = root.findViewById(R.id.runImgPlaceholder);
        exitBtn = root.findViewById(R.id.exitSummaryBtn);
        distanceLabel = root.findViewById(R.id.distanceLabel);
        speedLabel = root.findViewById(R.id.paceLabel);
        timeLabel = root.findViewById(R.id.timeLabel);
        dateLabel = root.findViewById(R.id.dateLabelRunSummary);
        pointsLabel = root.findViewById(R.id.pointsTextViewSummary);
        mViewModel.initialiseEpochTimes();

        return root;
    }

    public ArrayList<String> recentRun;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
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
        double dist = Double.parseDouble(recentRun.get(1)) / 1000;
        double time = Double.parseDouble(recentRun.get(2)) / 60;

        BigDecimal dba = new BigDecimal(dist).setScale(2, RoundingMode.HALF_UP);
        dist = dba.floatValue();

        BigDecimal db = new BigDecimal(time).setScale(2, RoundingMode.HALF_UP);
        time = db.floatValue();

        // date
        if (recentRun.size() == 5)
            dateLabel.setText(RunDbUtility.convertEpochToDate(Long.valueOf(recentRun.get(4))).toString());
        else
            dateLabel.setText(RunDbUtility.convertEpochToDate(Long.valueOf(recentRun.get(3))).toString());
        // set text to corresponding stat
        distanceLabel.setText(String.valueOf(dist));
        timeLabel.setText(String.valueOf(time));
        speedLabel.setText(String.valueOf(dist/time) + "m/s");

        int points = calculatePointsForRun((int) dist);
        int rewards = calculateRewardsForRun((int) dist);

        MainRepository.addPointsForRun(points);
        MainRepository.addRewardsForRun(rewards);

        pointsLabel.setText("Points: " + points);
    }

    // sets image on run summary
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setImage() {
        Log.d(TAG, "setImage: ");
        Bitmap bitmap = null;
        if (!recentRun.isEmpty()) {
            bitmap = RunDbUtility.stringToBitmap(recentRun.get(0));

            if (imageView.getMeasuredHeight() <= 0 || imageView.getMeasuredWidth() <= 0) {
                Log.d(TAG, "setImage: " + imageView.getMeasuredWidth());
            } else {
                // scale bitmap to image view so whole route can be seen
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                        (int) (imageView.getMeasuredWidth() - (imageView.getMeasuredWidth() * 0.5)),
                        (int) (imageView.getMeasuredHeight() - (imageView.getMeasuredHeight() * 0.5)),
                        false));
            }
        }
    }

    // points for run depending on distance add to avail points and tierpoints
    // +1 reward for every 5km

    private int calculatePointsForRun(int distance) {
        int distanceInMeters = distance * 1000;
        int maxPoints = 500, minPoints = 100, points = 0;
        int maxDivisor = 40, minDivisor = 25, divisor;
        double maxMultiplier = 1.6, minMultiplier = 1.1, multiplier;

        divisor = (int) (Math.random() * (maxDivisor - minDivisor)) + minDivisor;
        multiplier = (Math.random() * (maxMultiplier - minMultiplier)) + minMultiplier;

        points = (int) ((distanceInMeters / divisor) * multiplier);

        if (points > 500)
            points = maxPoints;

        return points < minPoints ? minPoints : points;
    }

    private int calculateRewardsForRun(int distance) {
        int rewards = 0;
        if (distance > 5)
            rewards = 5;
        if(distance > 10)
            rewards = 10;
        if (distance > 15)
            rewards = 15;
        if (distance > 20)
            rewards = 21;
        return rewards;
    }


    public void exitSummary() {
        controller.navigate(R.id.action_runSummary_to_startFrag);
    }
}