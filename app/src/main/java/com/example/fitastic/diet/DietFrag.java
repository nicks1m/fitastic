package com.example.fitastic.diet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.example.fitastic.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DietFrag extends Fragment implements View.OnClickListener {
    private List<Recipe> lstRecipe = new ArrayList<>();
    private List<Recipe> searchRecipe;
    private JSONArray testArr;
    private ImageButton searchBtn;
    private Button breakfastBtn, lunchBtn, dinnerBtn, favoritesBtn, ingredientSearchBtn;
    private TextView searchTv, emptyView;
    private RecyclerView myrv;
    private ProgressBar progressBar;

    private NavController controller;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_diethome, container, false);
        Toolbar mToolbarContact = RootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbarContact); //Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbarContact);
        progressBar = RootView.findViewById(R.id.progressbar2);
        emptyView = RootView.findViewById(R.id.empty_view2);
        myrv = RootView.findViewById(R.id.recyclerview);
        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        getRandomRecipes();
        controller = Navigation.findNavController(container);
        searchTv = RootView.findViewById(R.id.home_search_et);
        searchBtn = RootView.findViewById(R.id.home_search_btn);
        breakfastBtn = RootView.findViewById(R.id.home_breakfast_filter);
        lunchBtn = RootView.findViewById(R.id.home_lunch_filter);
        dinnerBtn = RootView.findViewById(R.id.home_dinner_filter);
        favoritesBtn = RootView.findViewById(R.id.buttonFavorites);
        ingredientSearchBtn = RootView.findViewById(R.id.buttonIngredientSearch);
        ingredientSearchBtn.setOnClickListener(this);
        favoritesBtn.setOnClickListener(this);
        breakfastBtn.setOnClickListener(this);
        lunchBtn.setOnClickListener(this);
        dinnerBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        searchTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!v.getText().toString().equals("")) {
                        emptyView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        myrv.setAlpha(0);
                        searchRecipe(v.getText().toString());
                    } else
                        Toast.makeText(getContext(), "Type something...", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return RootView;
    }

    private void searchRecipe(String search) {
        searchRecipe = new ArrayList<Recipe>();
        String URL = "https://api.spoonacular.com/recipes/search?query=" + search + "&number=30&instructionsRequired=true&apiKey=c957b6816ba048139fbc25a67d2cff33";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            testArr = (JSONArray) response.get("results");
                            Log.i("the search res is:", String.valueOf(testArr));
                            for (int i = 0; i < testArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = testArr.getJSONObject(i);
                                searchRecipe.add(new Recipe(jsonObject1.optString("id"), jsonObject1.optString("title"), "https://spoonacular.com/recipeImages/" + jsonObject1.optString("image"), Integer.parseInt(jsonObject1.optString("servings")), Integer.parseInt(jsonObject1.optString("readyInMinutes"))));
                            }
                            progressBar.setVisibility(View.GONE);
                            if (searchRecipe.isEmpty()) {
                                myrv.setAlpha(0);
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getContext(), searchRecipe);
                                myrv.setAdapter(myAdapter);
                                myrv.setItemAnimator(new DefaultItemAnimator());
                                myrv.setAlpha(1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getRandomRecipes() {
        String URL = " https://api.spoonacular.com/recipes/random?number=30&instructionsRequired=true&apiKey=c957b6816ba048139fbc25a67d2cff33";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            testArr = (JSONArray) response.get("recipes");
                            Log.i("the res is:", String.valueOf(testArr));
                            for (int i = 0; i < testArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = testArr.getJSONObject(i);
                                lstRecipe.add(new Recipe(jsonObject1.optString("id"), jsonObject1.optString("title"), jsonObject1.optString("image"), Integer.parseInt(jsonObject1.optString("servings")), Integer.parseInt(jsonObject1.optString("readyInMinutes"))));
                            }
                            progressBar.setVisibility(View.GONE);
                            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getContext(), lstRecipe);
                            myrv.setAdapter(myAdapter);
                            myrv.setItemAnimator(new DefaultItemAnimator());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        progressBar.setVisibility(View.GONE);
                        myrv.setAlpha(0);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {

        if (v == breakfastBtn) {
            searchRecipe("breakfast");
        } else if (v == lunchBtn) {
            searchRecipe("lunch");
        } else if (v == dinnerBtn) {
            searchRecipe("dinner");
        } else if (v == searchBtn) {
            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
            }
            if (!searchTv.getText().toString().toString().equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                myrv.setAlpha(0);
                searchRecipe(searchTv.getText().toString());
            } else
                Toast.makeText(getContext(), "Type something...", Toast.LENGTH_LONG).show();
        } else if (v == favoritesBtn) {
            controller.navigate(R.id.action_dietFrag_to_favoritesFragment2);
        } else if (v == ingredientSearchBtn) {
            controller.navigate(R.id.action_dietFrag_to_searchFragment);
        }
    }
}
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.log_out, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.main_log_out) {
//            FirebaseAuth.getInstance().signOut();
//            sendToLogin();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    private void sendToLogin() {
//        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
//        startActivity(loginIntent);
//        Objects.requireNonNull(getActivity()).finish();// The user can't come back to this page
//    }
//}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ExpandableListView;
//
//import com.example.fitastic.R;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link dietFrag#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class dietFrag extends Fragment {
//
//    private Button lossBfast;
//    private Button lossLunch;
//    private Button gainBfast;
//    private Button gainLunch;
//    private NavController controller;
//
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public dietFrag() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment dietFrag.
//     */
//    public static dietFrag newInstance(String param1, String param2) {
//        dietFrag fragment = new dietFrag();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View v = inflater.inflate(R.layout.fragment_diethome, container, false);

//        controller = Navigation.findNavController(container);
//
//        lossBfast = (Button) v.findViewById(R.id.lossBreakfastButton);
//        lossLunch = (Button) v.findViewById(R.id.lossLunchButton);
//        gainBfast = (Button) v.findViewById(R.id.gainBreakfastButton);
//        gainLunch = (Button) v.findViewById(R.id.gainLunchButton);
//
//
//        lossBfast.setOnClickListener(v1 -> {
//            openLossBfast(v1);
//        });
//
//        lossLunch.setOnClickListener(v1 -> {
//            openLossLunch(v1);
//        });
//
//        gainBfast.setOnClickListener(v1 -> {
//            openGainBfast(v1);
//        });
//
//        gainLunch.setOnClickListener(v1 -> {
//            openGainLunch(v1);
//        });

//        return v;
//
//    }

//    public void openLossBfast(View v){
//        controller.navigate(R.id.action_dietFrag_to_lossBfast);
//    }
//    public void openLossLunch(View v){
//        controller.navigate(R.id.action_dietFrag_to_lossLunch);
//    }
//    public void openGainBfast(View v){
//        controller.navigate(R.id.action_dietFrag_to_gainBfast);
//    }
//    public void openGainLunch(View v){
//        controller.navigate(R.id.action_dietFrag_to_gainLunch);
//    }
//}