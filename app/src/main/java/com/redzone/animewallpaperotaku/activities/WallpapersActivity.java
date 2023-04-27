package com.redzone.animewallpaperotaku.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
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
import com.redzone.animewallpaperotaku.adapters.WallpapersAdapter;
import com.redzone.animewallpaperotaku.models.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {
    String MyID = HomeActivity.username;
     List<Wallpaper> wallpaperList;
     public static List<Wallpaper> favList;
     DatabaseReference dbwallpaper,dbfavs;
     WallpapersAdapter adapter;
     RecyclerView recyclerView;
     ProgressBar progressBar;
     TextView textView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        mAdView = findViewById(R.id.adView);
        MobileAds.initialize(this,"ca-app-pub-1884895970372141~1135638101");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        textView = findViewById(R.id.nodata_txt);
        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new WallpapersAdapter(this,wallpaperList,MyID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        recyclerView.setAdapter(adapter);
        textView.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.VISIBLE);

        dbwallpaper = FirebaseDatabase.getInstance().getReference("images").child(category);
        dbfavs = FirebaseDatabase.getInstance().getReference("users").child(MyID).child("favourite")
        .child(category);


        fetchFavwallpapers(category);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void runAnimation(RecyclerView recyclerView){
        Context context = recyclerView.getContext();

        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_falldown);

        recyclerView.setLayoutAnimation(controller);


    }

    private void fetchFavwallpapers(final String category){
        progressBar.setVisibility(View.VISIBLE);
        dbfavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                if(dataSnapshot.exists()){
                    for(DataSnapshot wallpapersnapshot : dataSnapshot.getChildren()){
                        String id = wallpapersnapshot.getKey();
                        String title = wallpapersnapshot.child("title").getValue(String.class);
                        String desc = wallpapersnapshot.child("desc").getValue(String.class);
                        String thumb = wallpapersnapshot.child("url").getValue(String.class);


                        Wallpaper w = new Wallpaper(id,title,desc,thumb,category);
                        favList.add(w);

                    }

                }
                fetchWallpapers(category);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private  void fetchWallpapers(final String category){

        progressBar.setVisibility(View.VISIBLE);
        dbwallpaper.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.exists()){
                    for(DataSnapshot wallpapersnapshot : dataSnapshot.getChildren()){
                        String id = wallpapersnapshot.getKey();
                        String title = wallpapersnapshot.child("title").getValue(String.class);
                        String desc = wallpapersnapshot.child("desc").getValue(String.class);
                        String thumb = wallpapersnapshot.child("url").getValue(String.class);


                        Wallpaper w = new Wallpaper(id,title,desc,thumb,category);

                        if(isFavourite(w)){
                            w.isfavourite = true;
                        }
                        wallpaperList.add(w);

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

    private boolean isFavourite(Wallpaper w){
        for(Wallpaper f: favList){
            if(f.id.equals(w.id)){
                return true;
            }

        }
        return false;

    }


}
