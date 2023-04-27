package com.redzone.animewallpaperotaku.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.activities.ImageActivity;
import com.redzone.animewallpaperotaku.models.Wallpaper;

import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.CategoryViewHolder> {
    private Context mCtx;
    private List<Wallpaper> wallpaperList;
    private String iduser;
    private InterstitialAd interstitialAd;

    public WallpapersAdapter(Context mCtx, List<Wallpaper> categoryList, String iduser) {
        this.mCtx = mCtx;
        this.wallpaperList = categoryList;
        this.iduser = iduser;

        interstitialAd = new InterstitialAd(mCtx);
        interstitialAd.setAdUnitId("ca-app-pub-1884895970372141/6250882346");
        interstitialAd.loadAd(new AdRequest.Builder().build());

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_wallpapers,viewGroup,false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {
        Animation controller= AnimationUtils.loadAnimation(mCtx,R.anim.scale);

        categoryViewHolder.cardView.setAnimation(controller);

        Wallpaper w = wallpaperList.get(i);
        Glide.with(mCtx).load(w.url).apply(new RequestOptions().placeholder(R.drawable.loading)).into(categoryViewHolder.imageView);

        if(w.isfavourite){
            categoryViewHolder.checkBox1.setChecked(true);

        }
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
        ImageView imageView;
        CheckBox checkBox1 ;
        CardView cardView;
        Intent intent = new Intent(mCtx,ImageActivity.class);

        public CategoryViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.container_wallpapers);
            imageView = view.findViewById(R.id.wallpaper_image_view);
            checkBox1 = view.findViewById(R.id.check_box_fav);

            imageView.setOnClickListener(this);
            checkBox1.setOnCheckedChangeListener(this);

        }

        @Override
        public void onClick(View v) {
            if(interstitialAd.isLoaded()){
                interstitialAd.show();
            }else {
                interstitialAd.loadAd(new AdRequest.Builder().build());

            }
            int p =getAdapterPosition();
            Wallpaper w = wallpaperList.get(p);

            intent.putExtra("wallpaper",w.url);
            intent.putExtra("wallpaper_id",w.id);
            intent.putExtra("wallpaper_title",w.title);
            intent.putExtra("wallpaper_desc",w.desc);
            intent.putExtra("wallpaper_cat",w.category);

            mCtx.startActivity(intent);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position =getAdapterPosition();
            Wallpaper w = wallpaperList.get(position);

            DatabaseReference dbfavs = FirebaseDatabase.getInstance().getReference("users").child(iduser)
                    .child("favourite").child(w.category);
            if(isChecked){
                intent.putExtra("checked",true);
                dbfavs.child(w.id).setValue(w);

            }else{
                intent.putExtra("checked",false);
                dbfavs.child(w.id).setValue(null);

            }
        }
    }
}
