package com.deji_cranium.unilorinleague;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.deji_cranium.unilorinleague.Database.DBImpl;
import com.deji_cranium.unilorinleague.Fragments.AssistsFragment;
import com.deji_cranium.unilorinleague.Fragments.BookmarksFragment;
import com.deji_cranium.unilorinleague.Fragments.CreditsFragment;
import com.deji_cranium.unilorinleague.Fragments.FixturesFragments;
import com.deji_cranium.unilorinleague.Fragments.GeneralNewsFragment;
import com.deji_cranium.unilorinleague.Fragments.LeagueTableFragment;
import com.deji_cranium.unilorinleague.Fragments.OtherSportsFragment;
import com.deji_cranium.unilorinleague.Fragments.TopScorersFragment;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Menu mMenu;
    private Toolbar toolbar;
    private Bundle bundle;
    private String activeFragment = null;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //close the database instance opened in the Details Activity
        if (DetailsActivity.mDb != null){
            DBImpl.getReadableDatabaseInstance(getBaseContext()).close();
            DBImpl.getWritableDatabaseInstance(getBaseContext()).close();

            DetailsActivity.mDb.close();
            DetailsActivity.mSqLiteDatabaseForWriting.close();
            DetailsActivity.mSqliteDatabaseForReading.close();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bundle = savedInstanceState;
        setDisplay();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.root_layout, GeneralNewsFragment.newInstance()).commit();
            activeFragment = "homeFragment";
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mMenu = menu;

        //I am checking the LatestNews item because it's automatically opened when activity is created

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.latest_news) {
                if (!activeFragment.equalsIgnoreCase("homeFragment")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, GeneralNewsFragment.newInstance()).commit();
                    activeFragment = "homeFragment";
                    toolbar.setTitle("Latest News");
                }
            // Handle the camera action

        }



        else if (id == R.id.fixtures){
                    if (!activeFragment.equalsIgnoreCase("fixturesFragment")){
                        toolbar.setTitle("Fixtures");
                        getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, FixturesFragments.newInstance()).commit();
                        activeFragment = "fixturesFragment";
                    }

            }


        else if (id == R.id.other_sport_news){
            if (!activeFragment.equalsIgnoreCase("otherSportNewsFragment")){
                toolbar.setTitle("General News");
                getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, OtherSportsFragment.newInstance()).commit();
                activeFragment = "otherSportNewsFragment";




            }
        }
        else if (id == R.id.bookmarks){
            if (!activeFragment.equalsIgnoreCase("bookmarksFragment")){
                toolbar.setTitle("Bookmarks");
                getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, BookmarksFragment.newInstance()).commit();
                activeFragment = "bookmarksFragment";
            }
        }


        else if (id == R.id.top_scorers){
            if(!activeFragment.equalsIgnoreCase("topScorersFragment")){
                toolbar.setTitle("Top Scorers");
                getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, TopScorersFragment.newInstance()).commit();
                activeFragment = "topScorersFragment";



            }
        }


        else if (id == R.id.league_table){
            if (!activeFragment.equalsIgnoreCase("leagueTableFragment")){
                toolbar.setTitle("League Table");
                getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, LeagueTableFragment.newInstance()).commit();
                activeFragment = "leagueTableFragment";


            }
        }

        else if (id == R.id.assists){
            if(!activeFragment.equalsIgnoreCase("assistsTableFragment")){
                toolbar.setTitle("Assists");
                getSupportFragmentManager().beginTransaction().replace(R.id.root_layout, AssistsFragment.newInstance()).commit();
                activeFragment = "assistsTableFragment";


            }
        }
        else if (id == R.id.about) {
            CreditsFragment creditsFragment = new CreditsFragment();
            creditsFragment.show(getSupportFragmentManager(), "creditsFragment");

        }



            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

    }


    private void setDisplay(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toast.makeText(getBaseContext(), "This is the updated version", Toast.LENGTH_LONG).show();
    }
}
