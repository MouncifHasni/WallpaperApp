package com.redzone.animewallpaperotaku.activities;


import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omega_r.libs.omegaintentbuilder.OmegaIntentBuilder;
import com.omega_r.libs.omegaintentbuilder.downloader.DownloadCallback;
import com.omega_r.libs.omegaintentbuilder.handlers.ContextIntentHandler;
import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.models.Wallpaper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {
    String iduser = HomeActivity.username;
    ImageView imageView;
    ProgressBar progressBar;
    Wallpaper favlist;
    boolean favourite= false,check;
    Bitmap bitmap2 ;
    private String w_cat,w_id,w_title,wallpaper,w_desc;
    int width, height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.selected_image);
        progressBar = findViewById(R.id.progressbarimg);

        Intent intent = getIntent();
        check = intent.getBooleanExtra("checked", false);
        w_cat = intent.getStringExtra("wallpaper_cat");
        wallpaper = intent.getStringExtra("wallpaper");
        w_id = intent.getStringExtra("wallpaper_id");
        w_desc = intent.getStringExtra("wallpaper_desc");
        w_title = intent.getStringExtra("wallpaper_title");
        favlist = new Wallpaper(w_id, w_title, w_desc, wallpaper, w_cat);

        Picasso.get().load(wallpaper).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });

        final View r = findViewById(android.R.id.content);
        //****************************************************************************
       com.getbase.floatingactionbutton.FloatingActionButton fabsave = findViewById(R.id.fab_save);
        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).asBitmap().load(wallpaper).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            Picasso.get().load(wallpaper).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                    try {

                                        String root = Environment.getExternalStorageDirectory().toString();
                                        File myDir = new File(root + "/Anime Wallpaper Otaku");

                                        if (!myDir.exists()) {
                                            myDir.mkdir();
                                        }
                                        String name = w_id + ".jpg";
                                        myDir = new File(myDir, name);
                                        FileOutputStream out = new FileOutputStream(myDir);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                        out.flush();
                                        out.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                            //On Finish Saving:
                            Snackbar.make(r, "Wallpaper Saved", Snackbar.LENGTH_INDEFINITE).setAction("Done", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            Uri uri = saveWallpaperAndGetURI(resource, w_id);
                            if (uri != null) {
                                intent.setDataAndType(uri, "image/*");
                                getBaseContext().startActivity(Intent.createChooser(intent, "Wallpaper World"));
                                Snackbar.make(r, "Wallpaper Saved", Snackbar.LENGTH_INDEFINITE).setAction("Done", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                            }
                        }
                    }
                });

            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabshare = findViewById(R.id.fab_share);
        fabshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).asBitmap().load(wallpaper).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        new OmegaIntentBuilder(getBaseContext()).share()
                                .filesUrls(wallpaper).download(new DownloadCallback() {
                            @Override
                            public void onDownloaded(boolean b, @NotNull ContextIntentHandler contextIntentHandler) {
                                contextIntentHandler.startActivity();
                            }
                        });

                        /*Intent intent = new Intent(Intent.ACTION_SEND);

                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM,getlocalBitmapUri(resource));
                        getBaseContext().startActivity(Intent.createChooser(intent,"Wallpaper World"));*/
                    }
                });
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabset_background = findViewById(R.id.fab_background);
        fabset_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).asBitmap().load(wallpaper).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        GetScreenWidthHeight();
                        SetBitmapSize(resource);

                        WallpaperManager wallpaperManager =
                                WallpaperManager.getInstance(getBaseContext().getApplicationContext());


                        try {
                            wallpaperManager.setBitmap(bitmap2);
                            wallpaperManager.suggestDesiredDimensions(width, height);
                            Snackbar.make(r,"Background was set",Snackbar.LENGTH_INDEFINITE).setAction("Done", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

    }
        private Uri saveWallpaperAndGetURI(Bitmap bitmap,String id){

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat
                        .shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);


                } else {

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                return null;
            }
        File folder = new File(Environment.getExternalStorageDirectory().toString(),"/Anime Wallpaper Otaku");
        folder.mkdir();

        File file = new File(folder,id + ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();

            return  FileProvider.getUriForFile(getBaseContext(),getBaseContext().getPackageName() + ".provider",file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Uri getlocalBitmapUri(Bitmap bmp){
        Uri bmpUri = null;

        try {
            File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "wallpaper_World"+System.currentTimeMillis()+".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,90,out);
            out.close();
           bmpUri = FileProvider.getUriForFile(getBaseContext(),getBaseContext().getPackageName() + ".provider",file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }



    public void GetScreenWidthHeight() {

        DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }
    public void SetBitmapSize(Bitmap btmap){

        bitmap2 = Bitmap.createScaledBitmap(btmap, width, height, false);

    }


   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

       if(check==true){
           inflater.inflate(R.menu.menu_imgactive,menu);
           return true;
       }
       inflater.inflate(R.menu.menu_img, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseReference dbfavs = FirebaseDatabase.getInstance().getReference("users").child(iduser)
                .child("favourite").child(w_cat);

        switch (item.getItemId()){
            case R.id.btn_favimg :
                favAction(favourite,item);
                break;

            case R.id.btn_favimg_default :
                favAction2(check,item);
                break;
            case android.R.id.home:
                this.finish();

                return true;
        }
        return true;
    }

    void favAction(boolean isfav,MenuItem item){
        DatabaseReference dbfavs = FirebaseDatabase.getInstance().getReference("users").child(iduser)
                .child("favourite").child(w_cat);


        if(!isfav){
            item.setIcon(R.drawable.ic_fav_active);
            dbfavs.child(w_id).setValue(favlist);
        }else{
            item.setIcon(R.drawable.ic_fav_default);
            dbfavs.child(w_id).setValue(null);
        }
        favourite = !favourite;
    }
    void favAction2(boolean isfav,MenuItem item){
        DatabaseReference dbfavs = FirebaseDatabase.getInstance().getReference("users").child(iduser)
                .child("favourite").child(w_cat);


        if(isfav==false){
            item.setIcon(R.drawable.ic_fav_active);
            dbfavs.child(w_id).setValue(favlist);
        }else{
            item.setIcon(R.drawable.ic_fav_default);
            dbfavs.child(w_id).setValue(null);
        }
        isfav = !isfav;
        check = !check;
    }

}
