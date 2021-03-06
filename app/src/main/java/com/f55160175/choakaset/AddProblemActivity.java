package com.f55160175.choakaset;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddProblemActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_problem);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        //Toast.makeText(getApplicationContext(),"Home selected",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.edit:
                        Toast.makeText(getApplicationContext(), "Edit profile Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.manage:
                        Toast.makeText(getApplicationContext(), "Manage crop Selected", Toast.LENGTH_SHORT).show();
                        Intent manage = new Intent(getApplicationContext(), ManageCrops.class);
                        startActivity(manage);
                        /*ManageCrop manageCrop = new ManageCrop();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_map_container,manageCrop);
                        fragmentTransaction.commit();*/
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer,R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Intent intent = getIntent();
        String txtname = intent.getStringExtra("crop_name");
        String txtType = intent.getStringExtra("topic_type");
        final TextView cropname = (TextView) findViewById(R.id.cropname);
        final TextView topictype = (TextView) findViewById(R.id.type);
        cropname.setText(txtname);
        topictype.setText(txtType);

        SharedPreferences sharedUserId = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String user_id = sharedUserId.getString("user_id","false");

        final EditText txtTopic = (EditText) findViewById(R.id.topic);
        final EditText txtDetail = (EditText) findViewById(R.id.probDetail);

        Button btnSubmit = (Button) findViewById(R.id.submitProb);
        Button btnCancel = (Button) findViewById(R.id.cancelProb);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type",topictype.getText().toString()));
                params.add(new BasicNameValuePair("topic",txtTopic.getText().toString()));
                params.add(new BasicNameValuePair("detail", txtDetail.getText().toString()));
                params.add(new BasicNameValuePair("crop_name", cropname.getText().toString()));
                params.add(new BasicNameValuePair("user_id",user_id));
                System.out.println(params);

                addProblemTask task = new addProblemTask(params);
                task.execute(Urls.server_path+"add_problem.php");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(getApplicationContext(), ProblemActivity.class);
                startActivity(back);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_problem, menu);
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

    private class addProblemTask extends AsyncTask<String,Void,String> {
        String params;

        public addProblemTask(List<NameValuePair> p) {
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
                    Intent intent = new Intent(getApplicationContext(),ProblemActivity.class);
                    startActivity(intent);
                }else {
                    System.out.println("fail");
                    Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),ProblemActivity.class);
                    startActivity(intent);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
