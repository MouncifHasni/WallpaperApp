package com.redzone.animewallpaperotaku.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.activities.HomeActivity;
import com.redzone.animewallpaperotaku.adapters.CategoriesAdapter;
import com.redzone.animewallpaperotaku.models.Category;
import com.redzone.animewallpaperotaku.models.ItemsNb;

import java.util.ArrayList;
import java.util.List;

public class FragmentCategories extends Fragment {
    private ProgressBar progressBare;
    private DatabaseReference dbcategory,dbimages;
    private List<Category> categoryList;
    private CategoriesAdapter adapter;
    private List<ItemsNb> itemsNbList;
    TextView textView,textitems;
    RecyclerView recyclerView;
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.fragment_categories,container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdView = view.findViewById(R.id.adView);
        MobileAds.initialize(getContext(),"ca-app-pub-1884895970372141~1135638101");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        progressBare = view.findViewById(R.id.progressbar);
        progressBare.setVisibility(View.VISIBLE);
        categoryList = new ArrayList<>();
        itemsNbList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        textView = view.findViewById(R.id.nodata_txt);
        adapter = new CategoriesAdapter(getActivity(),categoryList);
        recyclerView.setAdapter(adapter);
        textitems = view.findViewById(R.id.nb_items);

        textView.setVisibility(View.VISIBLE);

        dbimages = FirebaseDatabase.getInstance().getReference("images");
        dbcategory = FirebaseDatabase.getInstance().getReference("categories");


        dbimages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x;String nbs;
                for(DataSnapshot category:dataSnapshot.getChildren()){
                    x=0;
                    for(DataSnapshot wallpapersnapshot:category.getChildren()){
                        x++;
                    }
                    nbs = Integer.toString(x);
                    ItemsNb itemsNb = new ItemsNb(category.getKey(),nbs);
                    itemsNbList.add(itemsNb);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        dbcategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nb;
                if(dataSnapshot.exists()) {
                    progressBare.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String desc = ds.child("desc").getValue(String.class);
                        String url = ds.child("url").getValue(String.class);

                        nb = compareCategory(name);

                        Category c = new Category(name, desc, url,nb);


                        categoryList.add(c);


                    }
                    //runAnimation(recyclerView);
                    adapter.notifyDataSetChanged();
                    //recyclerView.scheduleLayoutAnimation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void runAnimation(RecyclerView recyclerView){
        Context context = recyclerView.getContext();

        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_falldown);

        recyclerView.setLayoutAnimation(controller);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((HomeActivity)getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(searchItem,MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(searchItem,searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String userInput = s.toLowerCase();
                List<Category> newList = new ArrayList<>();
                for(Category cat:categoryList){
                    if(cat.name.toLowerCase().contains(userInput)){
                        newList.add(cat);
                    }

                }
                adapter.updatelist(newList);
                return true;
            }
        });
    }

    private String compareCategory(String name){
        for(ItemsNb f: itemsNbList){
            if(f.category.equals(name)){

                return f.items;
            }

        }
        return "0";

    }


}
