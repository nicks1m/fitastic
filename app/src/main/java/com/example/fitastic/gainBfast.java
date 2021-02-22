package com.example.fitastic;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link gainBfast#newInstance} factory method to
 * create an instance of this fragment.
 */
public class gainBfast extends Fragment {

    private DatabaseReference ref;
    private FirebaseRecyclerOptions<dietModel> options;
    private FirebaseRecyclerAdapter<dietModel, viewHolder> adapter;
    private RecyclerView recyclerView;
    private TextView title;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public gainBfast() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment gainBfast.
     */
    // TODO: Rename and change types and number of parameters
    public static gainBfast newInstance(String param1, String param2) {
        gainBfast fragment = new gainBfast();
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
        View v = inflater.inflate(R.layout.fragment_gain_bfast, container, false);

       // title = v.findViewById(R.id.titleGainBfast);
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Diets").child("GainBfast");
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        options = new FirebaseRecyclerOptions.Builder<dietModel>().setQuery(ref, dietModel.class).build();
        adapter = new FirebaseRecyclerAdapter<dietModel, viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull dietModel model) {
                holder.textName.setText(model.getName());
                holder.textCal.setText(model.getCal());
            }

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_diet_layout, parent, false);
                return new viewHolder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

        return v;
    }
}