package joao.splitride.app.settings;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.SearchUserListAdapter;
import joao.splitride.app.entities.UsersByCalendars;

public class SearchUsers extends AppCompatActivity implements View.OnClickListener {

    private ListView users_list;
    private Button ok, cancel;
    private SearchUserListAdapter myAdapter;
    private ArrayList<String> currentCalendarUsers = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All users");
        setSupportActionBar(toolbar);

        ok = (Button) findViewById(R.id.ok_button);
        cancel = (Button) findViewById(R.id.cancel_button);

        users_list = (ListView) findViewById(R.id.search_list);

        sharedPreferences = this.getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber utilizadores.");
        progressDialog.show();

        ParseQuery<UsersByCalendars> usersByCalendarsQuery = ParseQuery.getQuery("UsersByCalendar");
        usersByCalendarsQuery.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        usersByCalendarsQuery.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> objects, ParseException e) {

                for (UsersByCalendars usersByCalendars : objects) {
                    currentCalendarUsers.add(usersByCalendars.getUserID());
                }

                ParseQuery<ParseUser> query_users = ParseUser.getQuery();

                query_users.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        ArrayList<ParseUser> searchableUsers = new ArrayList<>();

                        for (ParseUser user : objects) {

                            if (!currentCalendarUsers.contains(user.getObjectId())) {
                                searchableUsers.add(user);
                            }
                        }

                        myAdapter = new SearchUserListAdapter(SearchUsers.this, R.layout.custom_line_list_view_checkbox, searchableUsers);

                        users_list.setAdapter(myAdapter);
                        progressDialog.dismiss();


                    }
                });

            }
        });

        Intent intent = getIntent();
        handleIntent(intent);


        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_users_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default


        return true;
    }

    public void onNewIntent(Intent intent) {

        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            ParseQuery<ParseUser> query_users = ParseUser.getQuery();
            query_users.whereContains("username", query);

            query_users.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);

            query_users.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {

                    if (e == null) {

                        // initiate the listadapter
                        myAdapter = new SearchUserListAdapter(SearchUsers.this, R.layout.custom_line_list_view_checkbox, objects);

                        // assign the list adapter
                        users_list.setAdapter(myAdapter);
                    }

                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ok_button:

                for (ParseUser user : myAdapter.selected) {
                    UsersByCalendars newUser = new UsersByCalendars();
                    newUser.setDefault(false);
                    newUser.setCalendarID(sharedPreferences.getString("calendarID", ""));
                    newUser.setUserID(user.getObjectId());

                    newUser.saveInBackground();
                }

                setResult(1);
                finish();

                break;

            case R.id.cancel_button:
                finish();
                break;
        }
    }
}