package com.example.fitastic.diet;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitastic.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GainBfast#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GainBfast extends Fragment {

//    private FirebaseRecyclerOptions<dietModel> options;
//    private TextView title;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GainBfast() {
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
    public static GainBfast newInstance(String param1, String param2) {
        GainBfast fragment = new GainBfast();
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
        return v;
    }
}

//       // title = v.findViewById(R.id.titleGainBfast);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Diets").child("GainBfast");
//        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
////        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//
//        options = new FirebaseRecyclerOptions.Builder<dietModel>().setQuery(ref, dietModel.class).build();
//        System.out.println("here???????");
//        FirebaseRecyclerAdapter<dietModel, viewHolder> adapter = new FirebaseRecyclerAdapter<dietModel, viewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull dietModel model) {
//                System.out.println("aici" + viewHolder.class.getName());
//                holder.name.setText(model.getName());
//                holder.cal.setText(model.getCal());
//            }
//
//
//            @NonNull
//            @Override
//            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_diet_layout, parent, false);
//                return new viewHolder(v);
//            }
//        };
//
//        adapter.startListening();
//        recyclerView.setAdapter(adapter);
//
//        return v;
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
//}