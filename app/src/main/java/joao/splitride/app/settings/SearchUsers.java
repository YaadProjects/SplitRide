package joao.splitride.app.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.SearchUserListAdapter;

public class SearchUsers extends AppCompatActivity {

    private ListView users_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All users");
        setSupportActionBar(toolbar);

        users_list = (ListView) findViewById(R.id.search_list);

        ParseQuery<ParseUser> query_users = ParseUser.getQuery();

        query_users.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                // initiate the listadapter
                SearchUserListAdapter myAdapter = new SearchUserListAdapter(SearchUsers.this, R.layout.custom_line_list_view_checkbox, objects);

                // assign the list adapter
                users_list.setAdapter(myAdapter);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}