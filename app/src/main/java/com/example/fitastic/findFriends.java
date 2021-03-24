package com.example.fitastic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class findFriends extends AppCompatActivity {


    private Toolbar mToolbar;
    private ImageButton searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;
    private ValueEventListener mListener;

    private DatabaseReference allUsersDatabaseRef;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Update Post");

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = (ImageButton) findViewById(R.id.home_search_btn);
        searchInputText = (EditText) findViewById(R.id.home_search_et);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String searchBoxInput = searchInputText.getText().toString();

                searchPeopleAndFriends(searchBoxInput);

            }
        });

    }


    private void searchPeopleAndFriends(String searchBoxInput) {

        Query searchPeopleAndFriendsQuery = allUsersDatabaseRef.orderByChild("display name")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");


        super.onStart();
        FirebaseRecyclerOptions<findFriendsDB> options =
                new FirebaseRecyclerOptions.Builder<findFriendsDB>()
                        .setQuery(searchPeopleAndFriendsQuery, findFriendsDB.class)
                        .build();

       FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<findFriendsDB, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull findFriendsDB model) {
                holder.myName.setText(model.getDisplay_name());
                holder.myStatus.setText(model.getBmi());

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_display_layout,parent, false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);

                return viewHolder;
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class  FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        TextView myName, myStatus, myDob;


        public FindFriendsViewHolder (@NonNull View itemView)
        {
            super(itemView);

            myName = itemView.findViewById(R.id.all_users_profile_name);
            myStatus = itemView.findViewById(R.id.all_users_status);
            myDob = itemView.findViewById(R.id.all_users_dob);

        }
    }


    public static class findFriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public findFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

    }

}



