package joao.splitride.app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Movement;

/**
 * Created by joaoferreira on 28/04/16.
 */
public class AddEditMovements extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout parentLayout;
    private Button ok, cancel;
    private Spinner payer, receiver;
    private EditText value;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_movement_layout);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        ok = (Button) findViewById(R.id.ok_button);
        cancel = (Button) findViewById(R.id.cancel_button);
        payer = (Spinner) findViewById(R.id.payer_spinner);
        receiver = (Spinner) findViewById(R.id.receiver_spinner);
        value = (EditText) findViewById(R.id.value_movement);

        sharedPreferences = this.getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Movement");
        setSupportActionBar(toolbar);

        ParseQuery<ParseUser> query_users = ParseUser.getQuery();

        query_users.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e==null){
                    ArrayList<String> users_names = new ArrayList<String>();

                    for(ParseUser user: objects){
                        users_names.add(user.getUsername());
                    }

                    ArrayAdapter<String> usersAdapter = new ArrayAdapter(AddEditMovements.this, android.R.layout.simple_dropdown_item_1line, users_names);
                    payer.setAdapter(usersAdapter);
                    receiver.setAdapter(usersAdapter);
                }

            }
        });


        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.ok_button:    String user_rec = receiver.getSelectedItem().toString();
                                    String user_pay = payer.getSelectedItem().toString();

                                    if(value.getText().toString().length()==0)
                                        Snackbar.make(parentLayout, "All fields are mandatory", Snackbar.LENGTH_LONG).show();
                                    else if(user_rec.equalsIgnoreCase(user_pay))
                                        Snackbar.make(parentLayout, "The receiver and the payer are the same", Snackbar.LENGTH_LONG).show();
                                    else{
                                        int amount = Integer.parseInt(value.getText().toString());

                                        saveMovement(user_rec, user_pay, amount);
                                        finish();
                                    }

                                    break;

            case R.id.cancel_button:    finish();
                                        break;
        }

    }

    private void saveMovement(String user_rec, String user_pay, int value){

        ParseQuery<ParseUser> query_users = ParseUser.getQuery();
        query_users.whereEqualTo("username", user_rec);

        try {
            ParseUser user_receiving = query_users.getFirst();

            query_users.whereEqualTo("username", user_pay);

            ParseUser user_paying = query_users.getFirst();

            Movement movement = new Movement();
            movement.setFromUserID(user_paying.getObjectId());
            movement.setToUserID(user_receiving.getObjectId());
            movement.setValue(value);
            movement.setCalendarID(sharedPreferences.getString("calendarID", ""));

            movement.saveInBackground();


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
