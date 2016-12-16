package joao.splitride.app.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joao.splitride.R;
import joao.splitride.app.custom.CustomTwitterLoginButton;
import joao.splitride.app.custom.StringSimplifier;
import joao.splitride.app.main.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private ParseUser parseUser;
    private String name, email;
    private Button login, register, cancel;
    private CustomTwitterLoginButton twitterLoginButton;
    private TextInputLayout usernameWrapper, emailWrapper, passwordWrapper;
    private LinearLayout parentLayout;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        emailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        login = (Button) findViewById(R.id.button_login);
        register = (Button) findViewById(R.id.register_button);
        cancel = (Button) findViewById(R.id.register_cancel);
        twitterLoginButton = (CustomTwitterLoginButton) findViewById(R.id.twitter_button);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                name = session.getUserName();

                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        email = result.data;

                        ParseQuery<ParseUser> query_users = ParseUser.getQuery();
                        query_users.whereEqualTo("email", email);

                        try {
                            ParseUser user = query_users.getFirst();

                            getUserDetailsFromParse(user);

                        } catch (ParseException e) {

                            if (e.getCode() == 101) {
                                String pictureUrl = "https://twitter.com/" + name + "/profile_image?size=bigger";
                                ParseUser user = new ParseUser();

                                try {
                                    user.setUsername(name);
                                    user.setPassword(getSaltString());
                                    user.signUp();

                                    new ProfilePhotoAsync(pictureUrl, user).execute();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else
                                e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.twitter_login_fail) + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.twitter_login_fail) + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_login:
                hideKeyboard();
                doLogin();

                break;

            case R.id.register_button:
                if (register.getText().toString().equals(getResources().getString(R.string.register))) {
                    emailWrapper.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);

                    register.setText(getResources().getString(R.string.ok));
                } else {

                    doRegister();
                }

                break;

            case R.id.register_cancel:
                login.setVisibility(View.VISIBLE);
                register.setText(getResources().getString(R.string.register));
                emailWrapper.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);

                break;

        }
    }

    public void onFacebookLogin(View v){
        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {

                if (user == null) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.fb_user_cancel), Toast.LENGTH_LONG).show();
                } else if (user.isNew()) {
                    getUserDetailsFromFB(user);
                } else {
                    getUserDetailsFromParse(user);
                }
            }
        });
    }

    // Facebook Stuff
    private void saveNewUser(Bitmap bitmap, final ParseUser parseUser) {

        parseUser.put("username", name);
        parseUser.put("email", email);
        parseUser.put("password", getSaltString());

//        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            final byte[] data = stream.toByteArray();
            String thumbName = parseUser.getUsername().replaceAll("\\s+", "");

            StringSimplifier stringSimplifier = new StringSimplifier();

            final ParseFile parseFile = new ParseFile(stringSimplifier.simplifiedString(thumbName) + "_thumb.jpg", data);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e != null) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        parseUser.put("profileThumb", parseFile);

                        //Finally save all the user details
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e1) {
                                if (e1 != null) {
                                    Toast.makeText(LoginActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.new_user) + parseUser.getUsername() + getResources().getString(R.string.signed_up), Toast.LENGTH_SHORT).show();

                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userID", parseUser.getObjectId());
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("username", parseUser.getUsername());
                                    intent.putExtra("email", parseUser.getEmail());
                                    intent.putExtra("image", data);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                }
            });
        }

    }

    private void getUserDetailsFromFB(final ParseUser user) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture.type(large)");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            email = response.getJSONObject().getString("email");
                            name = response.getJSONObject().getString("name");

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            String pictureUrl = data.getString("url");
                            new ProfilePhotoAsync(pictureUrl, user).execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void getUserDetailsFromParse(ParseUser parseUser) {

        //Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            byte[] data = parseFile.getData();

            Toast.makeText(LoginActivity.this, getResources().getString(R.string.welcome) + parseUser.getUsername(), Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", parseUser.getObjectId());
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", parseUser.getUsername());
            intent.putExtra("email", parseUser.getEmail());
            intent.putExtra("image", data);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        return salt.toString();

    }

    private void doLogin() {
        String username = usernameWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();

        if (username.isEmpty()) {
            usernameWrapper.setError(getResources().getString(R.string.insert_username));
        } else if (password.isEmpty()) {
            passwordWrapper.setError(getResources().getString(R.string.insert_password));
        } else if (!validatePassword(password)) {
            passwordWrapper.setError(getResources().getString(R.string.error_incorrect_password));
        } else {
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);

            //Set progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getResources().getString(R.string.please_wait));
            progressDialog.setMessage(getResources().getString(R.string.performing_login));
            progressDialog.show();

            ParseUser user = new ParseUser();

            user.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    progressDialog.dismiss();

                    if (e != null) {
                        Snackbar.make(parentLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userID", user.getObjectId());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    // NORMAL LOGIN & REGISTER
    private void doRegister() {

        String username = usernameWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();
        email = emailWrapper.getEditText().getText().toString();

        // Validating the form
        if (username.isEmpty()) {
            usernameWrapper.setError(getResources().getString(R.string.insert_username));
        } else if (password.isEmpty()) {
            passwordWrapper.setError(getResources().getString(R.string.insert_password));
        } else if (email.isEmpty()) {
            emailWrapper.setError(getResources().getString(R.string.insert_email));
        } else if (!validateEmail(email)) {
            emailWrapper.setError(getResources().getString(R.string.error_invalid_email));
        } else if (!validatePassword(password)) {
            passwordWrapper.setError(getResources().getString(R.string.error_invalid_password));
        } else {
            usernameWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);

            //Set progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getResources().getString(R.string.please_wait));
            progressDialog.setMessage(getResources().getString(R.string.performing_registration));
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
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    private void hideKeyboard() {
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

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        public Bitmap bitmap;
        String url;
        ParseUser user;

        public ProfilePhotoAsync(String url, ParseUser user) {
            this.url = url;
            this.user = user;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveNewUser(bitmap, user);
        }
    }

}
