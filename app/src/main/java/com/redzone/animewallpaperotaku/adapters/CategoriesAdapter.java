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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.activities.WallpapersActivity;
import com.redzone.animewallpaperotaku.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>  {
    private Context mCtx;
    private List<Category> categoryList;
    private InterstitialAd interstitialAd;

    public CategoriesAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;

        interstitialAd = new InterstitialAd(mCtx);
        interstitialAd.setAdUnitId("ca-app-pub-1884895970372141/6250882346");
        interstitialAd.loadAd(new AdRequest.Builder().build());


    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_categories,viewGroup,false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {

        Animation controller= AnimationUtils.loadAnimation(mCtx,R.anim.slide_left);

        categoryViewHolder.cardView.setAnimation(controller);

        Category c = categoryList.get(i);
        categoryViewHolder.textView.setText(c.name);
        categoryViewHolder.textView2.setText("Items : "+c.nb);
        Glide.with(mCtx).load(c.thumb).apply(new RequestOptions().placeholder(R.drawable.loading)).into(categoryViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updatelist(List<Category> newList){
        categoryList = new ArrayList<>();
        categoryList.addAll(newList);
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView,textView2;
        ImageView imageView;
        CardView cardView;

        public CategoryViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.container_categories);
            textView2 = view.findViewById(R.id.nb_items);
            textView = view.findViewById(R.id.cat_name);
            imageView = view.findViewById(R.id.cat_image_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(interstitialAd.isLoaded()){
                interstitialAd.show();
            }else {
                interstitialAd.loadAd(new AdRequest.Builder().build());

            }
            int p =getAdapterPosition();
            Category c = categoryList.get(p);

            Intent intent = new Intent(mCtx,WallpapersActivity.class);
            intent.putExtra("category",c.name);
            mCtx.startActivity(intent);
        }
    }
}
