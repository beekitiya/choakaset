package com.f55160175.choakaset;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private android.support.v7.widget.Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    static final int DATE_DIALOG_ID = 0;
    private int mYear,mMonth,mDay;
    EditText start_date;
    EditText end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_map_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
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
                        //Toast.makeText(getApplicationContext(),"Edit profile Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.manage:
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

        //new TokenApp().execute();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.151135, 101.490104), 6));

        UiSettings uis = googleMap.getUiSettings();
        uis.setZoomControlsEnabled(true);
        uis.setZoomGesturesEnabled(true);
        uis.setRotateGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);

        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
                if(googleMap!=null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,16.0f));
                }
            }
        };

        setUpMap(googleMap);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                AddCropFragment addCropFragment = new AddCropFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_map_container, addCropFragment);
                fragmentTransaction.commit();

                //addDialog.show();
                // Clears the previously touched position
                //googleMap.clear();

                // Animating to the touched position
                //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
                System.out.println(latLng.latitude + " : " + latLng.longitude);

                String lat = Double.toString(latLng.latitude);
                String lng = Double.toString(latLng.longitude);

                SharedPreferences shared = getSharedPreferences("location", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("lat",lat);
                editor.putString("lng",lng);
                editor.commit();

                /*Intent sendLatlng = new Intent(MainActivity.this,AddCropFragment.class);
                sendLatlng.putExtra("lat",lat);
                sendLatlng.putExtra("lng",lng);
                startActivity(sendLatlng);*/
            }
        });
    }

    public void setUpMap(final GoogleMap googleMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    retrieveAndAddCities(googleMap);
                } catch (IOException e) {
                    Log.e("error json","Cannot retieve city",e);
                    return;
                }
            }
        }).start();
    }

    protected void retrieveAndAddCities(final GoogleMap googleMap) throws IOException {
        SharedPreferences sharedUserId = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String user_id = sharedUserId.getString("user_id",null);
        System.out.println(user_id);
        if(user_id != null) {
            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                URL url = new URL(Urls.server_path+"markers.php"+"?user_id="+user_id);
                System.out.println(url);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    json.append(buff,0,read);
                }
            } catch (IOException e) {
                Log.e("Error json","Error connecting to service",e);
                throw new IOException("Error connection to service",e);
            } finally {
                if(conn != null){
                    conn.disconnect();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createMarkersFromJson(googleMap, json.toString());
                    } catch (JSONException e) {
                        Log.e("Error json", "Error processing json", e);
                    }
                }
            });
        }
        else if(user_id == null) {
            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                URL url = new URL(Urls.server_path+"marker_notlogin.php");
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    json.append(buff,0,read);
                }
            } catch (IOException e) {
                Log.e("Error json","Error connecting to service",e);
                throw new IOException("Error connection to service",e);
            } finally {
                if(conn != null){
                    conn.disconnect();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createMarkersFromJson(googleMap, json.toString());
                    } catch (JSONException e) {
                        Log.e("Error json", "Error processing json", e);
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void createMarkersFromJson(final GoogleMap googleMap,String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        System.out.println(json);
        List<Marker> markers = new ArrayList<Marker>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObj = jsonArray.getJSONObject(i);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                                .title(jsonObj.getString("crop_id"))
                                .position(new LatLng(
                                        jsonObj.getDouble("crop_latitude"),
                                        jsonObj.getDouble("crop_longitude")))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                );
                markers.add(marker);

                final Dialog infoDialog = new Dialog(MainActivity.this);
                infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                infoDialog.setContentView(R.layout.my_infowindow);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {


                        /*final TextView txtFarm = (TextView) findViewById(R.id.farmName);
                        final ImageView imgAccount = (ImageView) findViewById(R.id.imageAccount);
                        final TextView txtStt = (TextView) findViewById(R.id.txtStatus);
                        final TextView txtOwnName = (TextView) findViewById(R.id.ownName);
                        final TextView txtPlant = (TextView) findViewById(R.id.txtPlant);
                        final TextView txtPlantName = (TextView) findViewById(R.id.plantName);
                        final TextView txtArea = (TextView) findViewById(R.id.txtArea);
                        final TextView txtAreaQty = (TextView) findViewById(R.id.areaQty);
                        final TextView product = (TextView) findViewById(R.id.txtProduct);
                        final TextView txtProduct = (TextView) findViewById(R.id.productQty);

                        /*txtFarm.setText("ไร่ดอยคำ");
                        txtStatus.setText("พร้อมเก็บเกี่ยว");
                        txtLat.setText("1111111");
                        txtLng.setText("22222");

                        infoDialog.show();
                        infoDialog.getWindow().setLayout(1090, 1400);*/

                        return false;
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_login) {
            SharedPreferences sharedUserId = getSharedPreferences("user", Context.MODE_PRIVATE);
            final String user_id = sharedUserId.getString("user_id",null);
            System.out.println(user_id);

            if(user_id == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
           } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AboutDialog);
                builder.setMessage("คุณต้องการออกจากระบบหรือไม่?");
                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences settings = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.clear();
                        editor.commit();
                        Toast.makeText(getBaseContext(), "All data cleared!", Toast.LENGTH_SHORT).show();
                        finish();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        }

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //return true;
        // }
        return super.onOptionsItemSelected(item);
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        }

        return null;

    }
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            start_date.setText(new StringBuilder().append(mDay).append("/").append(mMonth+1).append("/").append(mYear));
            end_date.setText(new StringBuilder().append(mDay).append("/").append(mMonth+1).append("/").append(mYear));
        }

    };
}
