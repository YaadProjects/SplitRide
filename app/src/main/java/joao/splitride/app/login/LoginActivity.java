package joao.splitride.app.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joao.splitride.R;
import joao.splitride.app.main.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Button login, register, cancel;
    private TextInputLayout usernameWrapper, emailWrapper, passwordWrapper;
    private LinearLayout parentLayout;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        emailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        login = (Button) findViewById(R.id.button_login);
        register = (Button) findViewById(R.id.register_button);
        cancel = (Button) findViewById(R.id.register_cancel);


        login.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        String username, password, email;

        switch(v.getId()){

            case R.id.button_login: hideKeyboard();
                                    username = usernameWrapper.getEditText().getText().toString();
                                    password = passwordWrapper.getEditText().getText().toString();
                                    doLogin(username, password);

                                    break;

            case R.id.register_button:  if(register.getText().toString().equals("Registar")){
                                            emailWrapper.setVisibility(View.VISIBLE);
                                            cancel.setVisibility(View.VISIBLE);
                                            login.setVisibility(View.GONE);

                                            register.setText("OK");
                                        }else{
                                            username = usernameWrapper.getEditText().getText().toString();
                                            password = passwordWrapper.getEditText().getText().toString();
                                            email = emailWrapper.getEditText().getText().toString();

                                            doRegister(username, password, email);

                                        }

                                        break;

            case R.id.register_cancel:  login.setVisibility(View.VISIBLE);
                                        register.setText("Registar");
                                        emailWrapper.setVisibility(View.GONE);
                                        cancel.setVisibility(View.GONE);

                                        break;
        }
    }

    private void doLogin(String username, String password){

        if(username.isEmpty()){
            usernameWrapper.setError("Introduza um username.");
        }else if(password.isEmpty()){
            passwordWrapper.setError("Introduza uma password.");
        } else if (!validatePassword(password)) {
            passwordWrapper.setError("Not a valid password!");
        } else {
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);

            //Set progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Por favor espere");
            progressDialog.setMessage("A realizar o login.");
            progressDialog.show();

            ParseUser user = new ParseUser();

            user.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        // Show the error message
                        Snackbar.make(parentLayout, e.getMessage(), Snackbar.LENGTH_LONG)
                                .show(); // Don’t forget to show!
                    } else {
                        // Start an intent for the dispatch activity
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userID", user.getObjectId());
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    private void doRegister(String username, String password, String email){

        // Validating the form

        if(username.isEmpty()){
            usernameWrapper.setError("Introduza um username.");
        }else if(password.isEmpty()){
            passwordWrapper.setError("Introduza uma password.");
        }else if (email.isEmpty()){
            emailWrapper.setError("Introduza um email.");
        }else if (!validateEmail(email)) {
            emailWrapper.setError("Not a valid email address!");
        } else if (!validatePassword(password)) {
            passwordWrapper.setError("Not a valid password!");
        } else {
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);
        }

        //Set progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A realizar o registo.");
        progressDialog.show();


        final ParseUser user = new ParseUser();
        user.put("username", username);
        user.put("password", password);
        user.put("email", email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Snackbar.make(parentLayout, e.getMessage(), Snackbar.LENGTH_LONG)
                    .show(); // Don’t forget to show!
                } else {
                    // Start an intent for the dispatch activity
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userID", user.getObjectId());
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    private void hideKeyboard(){
        View view = getCurrentFocus();

        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

}
