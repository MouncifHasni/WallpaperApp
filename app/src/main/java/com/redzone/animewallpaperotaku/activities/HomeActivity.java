package com.redzone.animewallpaperotaku.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.redzone.animewallpaperotaku.R;
import com.redzone.animewallpaperotaku.fragments.FragmentAbout;
import com.redzone.animewallpaperotaku.fragments.FragmentCategories;
import com.redzone.animewallpaperotaku.fragments.FragmentFavourite;
import com.redzone.animewallpaperotaku.fragments.FragmentHome;
import com.redzone.animewallpaperotaku.fragments.FragmentSetting;

import java.util.Random;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    String MyID;
    public static  final String Shared_Prefs = "sharedPrefs";
    public static final String TEXT = "text";
    public static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadData();

        if(username=="NULL"){  MyID = GenerateString(16);
            saveData();
            loadData();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                    new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home :
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                        new FragmentHome()).commit();
                break;
            case R.id.nav_categories :
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                        new FragmentCategories()).commit();
                break;
            case R.id.nav_fav :
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                        new FragmentFavourite()).commit();
                break;
            case R.id.nav_setting :
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                        new FragmentSetting()).commit();
                break;
            case R.id.nav_about :
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                        new FragmentAbout()).commit();
                break;
            case R.id.nav_rating:
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.nav_shareapp:
                Intent a = new Intent(Intent.ACTION_SEND);
                String appPackagename = getApplicationContext().getPackageName();
                String strApplink="";
                try{
                    strApplink = "https://play.google.com/store/apps/details?id=" +appPackagename;
                }catch (ActivityNotFoundException e){
                    strApplink = "https://play.google.com/store/apps/details?id=" +appPackagename;
                }
                //sharing part
                a.setType("text/link");
                String sharebody = "Hey! Download this App and get free HD Anime Wallpapers"+"\n"+""+strApplink;
                String shareSub="APP NAME/TITLE";
                a.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                a.putExtra(Intent.EXTRA_TEXT,sharebody);
                startActivity(getIntent().createChooser(a,"Share Using"));


                break;


        }
            drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackpresed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Shared_Prefs,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT,MyID);
        editor.apply();
        Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();

    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Shared_Prefs,MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT,"NULL");

    }

    private String GenerateString(int lenght){
        char [] chars ="AZERTYUIOPQSDFGHJKLMWXCVBN1234567890".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for(int i =0;i<lenght;i++){
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);

        return stringBuilder.toString();

    }

}
