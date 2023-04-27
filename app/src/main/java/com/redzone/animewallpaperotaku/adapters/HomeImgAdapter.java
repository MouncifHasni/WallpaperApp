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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.activities.ImageActivity;
import com.redzone.animewallpaperotaku.models.Wallpaper;

import java.util.List;

public class HomeImgAdapter extends RecyclerView.Adapter<HomeImgAdapter.CategoryViewHolder> {
    private Context mCtx;
    private List<Wallpaper> wallpaperList;
    private String iduser;
    private InterstitialAd interstitialAd;


    public HomeImgAdapter(Context mCtx, List<Wallpaper> categoryList, String iduser) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_homewallpapers,viewGroup,false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {

        Animation controller= AnimationUtils.loadAnimation(mCtx,R.anim.scale);

        categoryViewHolder.cardView.setAnimation(controller);

        Wallpaper w = wallpaperList.get(i);
        Glide.with(mCtx).load(w.url).apply(new RequestOptions().placeholder(R.drawable.loading)).into(categoryViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        CardView cardView;

        public CategoryViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.wallpaper_image_view);
            cardView = view.findViewById(R.id.container_home);
            imageView.setOnClickListener(this);

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

            Intent intent = new Intent(mCtx,ImageActivity.class);
            intent.putExtra("wallpaper",w.url);
            intent.putExtra("wallpaper_title",w.title);
            intent.putExtra("wallpaper_desc",w.desc);
            intent.putExtra("wallpaper_cat",w.category);
            intent.putExtra("wallpaper_id",w.id);

            if(w.isfavourite){
                intent.putExtra("checked",w.isfavourite);
            }

            mCtx.startActivity(intent);
        }


    }

}
