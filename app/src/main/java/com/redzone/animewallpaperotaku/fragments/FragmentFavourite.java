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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.activities.HomeActivity;
import com.redzone.animewallpaperotaku.adapters.WallpapersAdapter;
import com.redzone.animewallpaperotaku.models.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavourite extends Fragment {
    String MyID = HomeActivity.username;
    private List<Wallpaper> favwalls;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    WallpapersAdapter adapter;
    DatabaseReference dbfavs;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.fragment_favourite,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favwalls = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressbar);
        adapter = new WallpapersAdapter(getActivity(),favwalls,MyID);
        textView = view.findViewById(R.id.nodata_txt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        recyclerView.setAdapter(adapter);

        dbfavs = FirebaseDatabase.getInstance().getReference("users").child(MyID).child("favourite");
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        dbfavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    textView.setVisibility(View.GONE);
                    for (DataSnapshot category : dataSnapshot.getChildren()) {

                        for (DataSnapshot wallpapersnapshot : category.getChildren()) {
                            String id = wallpapersnapshot.getKey();
                            String title = wallpapersnapshot.child("title").getValue(String.class);
                            String desc = wallpapersnapshot.child("desc").getValue(String.class);
                            String thumb = wallpapersnapshot.child("url").getValue(String.class);


                            Wallpaper w = new Wallpaper(id, title, desc, thumb, category.getKey());
                            w.isfavourite = true;
                            favwalls.add(w);
                        }
                    }
                    runAnimation(recyclerView);
                    adapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
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
}
