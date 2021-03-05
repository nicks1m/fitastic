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

        mViewModel.stats.observe(this, new Observer<ArrayList<String>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(ArrayList<String> strings) {
                recentRun = strings;
                setImage();
            }
        });

        MainRepository.epochTimes.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                mViewModel.getRecentRunStats(strings);
            }
        });

        MainRepository.recentRun.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                mViewModel.handleRecentRun(strings);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_run_summary, container, false);

        imageView = root.findViewById(R.id.runImgPlaceholder);
        exitBtn = root.findViewById(R.id.exitSummaryBtn);
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
                exitSummary();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setImage() {
        byte[] data = Base64.getDecoder().decode(recentRun.get(0));
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageView.setImageBitmap(bitmap);
    }

    public void exitSummary() {
        controller.navigate(R.id.action_runSummary_to_startFrag);
    }
}