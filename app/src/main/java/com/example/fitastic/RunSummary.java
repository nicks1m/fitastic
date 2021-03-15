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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class RunSummary extends Fragment {

    /* Displays the run stats such as route image, speed, time... also adds points and rewards
    *  to the user for completing the run.
    *
    *  The route image is stored as encoded Base64 string in firebase so it is decoded using
    *  RunDbUtility to get it into a bitmap format so it can be displayed.
    */

    // debug
    private static String TAG = "RunSummary"; 

    // view model
    private RunSummaryViewModel mViewModel;

    // navigation
    private NavController controller;

    // holds recent run stats
    private ArrayList<String> recentRun;;

    // graphical components
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
        // debug
        Log.d(TAG, "onCreate: ");
        // get view model
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
        // debug
        Log.d(TAG, "onCreateView: ");

        // get root
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // debug
        Log.d(TAG, "onViewCreated: ");
        // get navigation
        controller = Navigation.findNavController(view);
        // exit button will return back to StartFrag when pressed
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

        // convert dist to two decimal places
        BigDecimal dba = new BigDecimal(dist).setScale(2, RoundingMode.HALF_UP);
        // convert time to two decimal places
        BigDecimal db = new BigDecimal(time).setScale(2, RoundingMode.HALF_UP);
        // write time
        time = db.floatValue();

        // date error handling
        if (recentRun.size() == 5)
            dateLabel.setText(RunDbUtility.convertEpochToDate(Long.valueOf(recentRun.get(4))).toString());
        else
            dateLabel.setText(RunDbUtility.convertEpochToDate(Long.valueOf(recentRun.get(3))).toString());

        // set text to corresponding stat
        distanceLabel.setText(String.valueOf(dba.floatValue()) + "km");
        timeLabel.setText(String.valueOf(db.floatValue()) + "min");

        // calculate speed using dist and time
        String speed = RunDbUtility.calculatePace(String.valueOf(dist), (String.valueOf(time)));

        speedLabel.setText(String.valueOf(speed));

        // calculate points and rewards for run
        int points = calculatePointsForRun((int) dist);
        int rewards = calculateRewardsForRun(dist);

        // add this to firebase
        MainRepository.addPointsForRun(points);
        MainRepository.addRewardsForRun(rewards);

        // add amount of points recieved to pont label
        pointsLabel.setText("Points: " + points);
    }

    // sets image on run summary
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setImage() {
        // debug
        Log.d(TAG, "setImage: ");
        // initialise bitmap
        Bitmap bitmap = null;
        if (!recentRun.isEmpty()) {
            // get encoded string
            bitmap = RunDbUtility.stringToBitmap(recentRun.get(0));

            // if imageView hasn't been properly loaded
            if (imageView.getMeasuredHeight() <= 0 || imageView.getMeasuredWidth() <= 0) {
                // debug
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

    // points for run depending on distance add to avail points and tier points
    private int calculatePointsForRun(int distance) {
        // get distance in meters
        int distanceInMeters = distance * 1000;
        // upper/lower bounds for points that can be received
        int maxPoints = 500, minPoints = 100, points = 0;
        // upper/lower bounds for distance divisor
        int maxDivisor = 40, minDivisor = 25, divisor;
        // upper/lower bounds for result multiplier
        double maxMultiplier = 1.6, minMultiplier = 1.1, multiplier;

        // find a random multiplier and divisor
        divisor = (int) (Math.random() * (maxDivisor - minDivisor)) + minDivisor;
        multiplier = (Math.random() * (maxMultiplier - minMultiplier)) + minMultiplier;

        // calculate points
        points = (int) ((distanceInMeters / divisor) * multiplier);

        // handle if points has gone over upper bound
        if (points > 500)
            points = maxPoints;

        // handle if points has gone lower than lower bound
        return points < minPoints ? minPoints : points;
    }

    // calculate if any rewards eligible for run
    private int calculateRewardsForRun(double distance) {
        // initialise distance
        int rewards = 0;
        // get corresponding award
        if (distance > 5)
            rewards = 5;
        if(distance > 10)
            rewards = 10;
        if (distance > 15)
            rewards = 15;
        if (distance > 21)
            rewards = 21;
        if (distance > 42)
            rewards = 42;
        return rewards;
    }

    // exit RunSummaryPage
    public void exitSummary() {
        controller.navigate(R.id.action_runSummary_to_startFrag);
    }
}