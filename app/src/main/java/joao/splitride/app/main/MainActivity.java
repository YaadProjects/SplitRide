package joao.splitride.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseUser;
import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;

import joao.splitride.R;
import joao.splitride.app.fragments.RoutesFragment;
import joao.splitride.app.fragments.Segments;
import joao.splitride.app.fragments.UsersFragment;
import joao.splitride.app.login.DispatchActivity;
import joao.splitride.app.settings.AddEditRoute;
import joao.splitride.app.settings.AddEditSegment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;
    private Segments segments;
    private RoutesFragment routesFragment;
    private UsersFragment usersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Segments segments_frag = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
                RoutesFragment routes_frag = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");

                if (segments_frag != null && segments_frag.isVisible()) {
                    Intent intent = new Intent(MainActivity.this, AddEditSegment.class);
                    startActivity(intent);
                }else if(routes_frag != null && routes_frag.isVisible()){
                    Intent intent = new Intent(MainActivity.this, AddEditRoute.class);
                    startActivity(intent);
                }
                else Log.d("frag", "calendar");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home){
            /*routesFragment = new RoutesFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.calendar1, routesFragment, "CALENDAR");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();*/
        }else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_routes) {
            routesFragment = new RoutesFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.calendar1, routesFragment, "ROUTES");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_persons) {
            usersFragment = new UsersFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.calendar1, usersFragment, "PERSONS");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.nav_segments) {
            segments = new Segments();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.calendar1, segments, "SEGMENTS");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
        REMOVE METHODS
     */

    public void removeOnClickHandler(View v){

        Segments segment = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
        RoutesFragment route = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");

        if (segment != null && segment.isVisible()) {
            segments.removeOnClickHandler(v);
        }else if(route != null && route.isVisible()){
            routesFragment.removeOnClickHandler(v);
        }

    }

    /*
        EDIT METHODS
     */

    public void editOnClickHandler(View v){

        Segments segment = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
        RoutesFragment route = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");

        if (segment != null && segment.isVisible()) {
            segments.editOnClickHandler(v);
        }else if(route != null && route.isVisible()){
            routesFragment.editOnClickHandler(v);
        }
    }
}
