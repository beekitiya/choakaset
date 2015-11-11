package com.f55160175.choakaset;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.service.textservice.SpellCheckerService;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.media.RemotePlaybackClient;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.drive.internal.OpenContentsRequest;
import com.google.android.gms.fitness.data.Session;
import com.loopj.android.http.Base64;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends FragmentActivity {

    private TextView txtDetail;

    private CallbackManager callbackManager;

    /*private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //printKeyHash();

        /*AccessTokenTracker tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };

        tracker.startTracking();
        profileTracker.startTracking();*/

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissiomNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginButton.setReadPermissions(permissiomNeeds);
        //loginButton.setReadPermissions(Arrays.asList("user_photos", "email", "user_birthday", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        //loginButton.registerCallback(callbackManager,callback);
        //loginButton.registerCallback(callbackManager,callback);*/
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                System.out.println("onsuccess");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        Log.v("LoginActivity", graphResponse.toString());
                        String token = loginResult.getAccessToken().getToken();
                        System.out.println(token);
                        if(graphResponse != null) {
                            try {
                                final String id = jsonObject.getString("id");
                                final String name = jsonObject.getString("name");
                                //String email = jsonObject.getString("email");
                                //String FBemail = jsonObject.getString("email");
                                //String birthday = jsonObject.getString("birthday");
                                System.out.println("facebook: " + id + ", " + name + ", ");

                                try {
                                    final URL image_path = new URL("http://graph.facebook.com/"+id+ "/picture?type=normal");
                                    System.out.println("image::> " + image_path);

                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("idFB",id));
                                    params.add(new BasicNameValuePair("name", name));
                                    //params.add(new BasicNameValuePair("email", email));
                                    params.add(new BasicNameValuePair("picture",image_path.toString()));
                                    params.add(new BasicNameValuePair("token", token));
                                    System.out.println(params);

                                    addFacebookAccount task = new addFacebookAccount(params);
                                    task.execute(Urls.server_path+"fb_login.php");
                                }
                                catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Bundle parameter = new Bundle();
                parameter.putString("field", "id,name");
                request.setParameters(parameter);
                request.executeAsync();

                /*Intent goMain = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(goMain);*/
            }

            @Override
            public void onCancel() {
                System.out.println("oncancel");
            }

            @Override
            public void onError(FacebookException e) {
                System.out.println("onerror");
                Log.v("LoginActivity",e.getCause().toString());
            }
        });

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);

        Button btnLogin = (Button)findViewById(R.id.login);
        Button btnCancel = (Button)findViewById(R.id.cancel);
        Button btnRegis = (Button)findViewById(R.id.regis);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username.getText().toString()));
                params.add(new BasicNameValuePair("password", password.getText().toString()));
                System.out.println(params);

                LoginTask task = new LoginTask(params);
                task.execute(Urls.server_path+"login.php");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gohome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gohome);
            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goRegis = new Intent(getApplicationContext(), RegisActivity.class);
                startActivity(goRegis);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private  class LoginTask extends AsyncTask<String,Void,String> {
        String params;

        public LoginTask(List<NameValuePair> p) {
            params = URLEncodedUtils.format(p, "utf-8");
            System.out.println(params);
        }

        @Override
        protected String doInBackground(String... urls) {
            System.out.println(params);
            return  JsonHttp.getWebText(urls[0] + "?" + params);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.i("tagconvertstr", "["+result+"]");
                JSONObject json = new JSONObject(result);
                String id = json.getString("id");
                if (json.getInt("success") == 1) {
                    System.out.println("hello");
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedUserId = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorLogin = sharedUserId.edit();
                    editorLogin.putString("user_id", id);
                    editorLogin.commit();

                    Intent goMainMenu = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(goMainMenu);
                }
                else {
                    Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    /*Intent goMainMenu = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(goMainMenu);*/
                }
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this,"cannot connect with server", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void printKeyHash(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.f55160175.choakaset",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.toString());
        }
    }

    private class addFacebookAccount extends AsyncTask<String,Void,String> {
        String params;

        public addFacebookAccount(List<NameValuePair> p) {
            params = URLEncodedUtils.format(p, "utf-8");
            System.out.println(params);
        }

        @Override
        protected String doInBackground(String... urls) {
            System.out.println(urls[0] + "?" + params);
            return JsonHttp.getWebText(urls[0] + "?" + params);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                if(json.getInt("success") == 1){
                    System.out.println("hello");
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }else if(json.getInt("success") == 2) {
                    System.out.println("hi");
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
                else {
                    System.out.println("fail");
                    Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
