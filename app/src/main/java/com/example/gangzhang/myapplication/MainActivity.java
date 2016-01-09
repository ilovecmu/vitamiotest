package com.example.gangzhang.myapplication;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private VideoPlayerSettingFragment vf ;
    private  PictureLookUp pf ;
    private SettingFragment sf;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

//        time = System.currentTimeMillis();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Log.d("myapp","postion="+position);


        switch (position){
            case 2:

                if(sf==null){
                    sf = new SettingFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, (Fragment) sf)
                        .commit();
                break;
            case 1:
                if(pf==null){
                    pf = new PictureLookUp();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, (Fragment) pf)
                        .commit();
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setType("image/*");
//                File appDir = new File(Environment.getExternalStorageDirectory(), "Player");
//                Uri uri = Uri.parse(Environment.getExternalStorageDirectory()+"Player");
//                intent.setDataAndType(uri,"image/*");
//                startActivity(intent);
                break;
            case 0:
                if(vf==null){
                    vf = new VideoPlayerSettingFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, (Fragment) vf)
                        .commit();
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
        Log.d("myapp","onSectionAttached ="+mTitle);

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(null);
        actionBar.setDisplayUseLogoEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("myapp","activity onCreateOptionsMenu");
        Log.d("myapp","activyt="+mNavigationDrawerFragment.isDrawerOpen());
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - time > 3000){
            Toast.makeText(this.getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        }else {
            super.onBackPressed();
        }

    }
}
