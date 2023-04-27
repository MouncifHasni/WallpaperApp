package com.redzone.animewallpaperotaku.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.redzone.animewallpaperotaku.adapters.HomeImgAdapter;
import com.redzone.animewallpaperotaku.models.Wallpaper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentHome extends Fragment {

    String MyID = HomeActivity.username;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private List<Wallpaper> imglist,favwalls;
    HomeImgAdapter adapter;
    DatabaseReference dbimages,dbfavs;
    TextView textView;
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdView = view.findViewById(R.id.adView);
        MobileAds.initialize(getContext(),"ca-app-pub-1884895970372141~1135638101");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        favwalls = new ArrayList<>();
        imglist = new ArrayList<>();

        recyclerView =view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressbar);
        adapter = new HomeImgAdapter(getActivity(),imglist,MyID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        textView = view.findViewById(R.id.nodata_txt);
        textView.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(adapter);
        dbimages = FirebaseDatabase.getInstance().getReference("images");
        progressBar.setVisibility(View.VISIBLE);

        dbfavs = FirebaseDatabase.getInstance().getReference("users").child(MyID).child("favourite");


        dbfavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                for(DataSnapshot category:dataSnapshot.getChildren()){

                    for(DataSnapshot wallpapersnapshot:category.getChildren()){
                        String id = wallpapersnapshot.getKey();
                        String title = wallpapersnapshot.child("title").getValue(String.class);
                        String desc = wallpapersnapshot.child("desc").getValue(String.class);
                        String thumb = wallpapersnapshot.child("url").getValue(String.class);


                        Wallpaper w = new Wallpaper(id,title,desc,thumb,category.getKey());
                        favwalls.add(w);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        dbimages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                for(DataSnapshot category:dataSnapshot.getChildren()){

                    for(DataSnapshot wallpapersnapshot:category.getChildren()){
                        String id = wallpapersnapshot.getKey();
                        String title = wallpapersnapshot.child("title").getValue(String.class);
                        String desc = wallpapersnapshot.child("desc").getValue(String.class);
                        String thumb = wallpapersnapshot.child("url").getValue(String.class);


                        Wallpaper wl = new Wallpaper(id,title,desc,thumb,category.getKey());
                        if(isFavourite(wl)){
                            wl.isfavourite = true;
                        }
                        imglist.add(wl);
                    }


                }
                Collections.shuffle(imglist);
                //runAnimation(recyclerView);
                adapter.notifyDataSetChanged();
                //ecyclerView.scheduleLayoutAnimation();
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

    private boolean isFavourite(Wallpaper w){
        for(Wallpaper f: favwalls){
            if(f.id.equals(w.id)){
                return true;
            }

        }
        return false;

    }
}
