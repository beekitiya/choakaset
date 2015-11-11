package com.f55160175.choakaset;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;

public class ProblemActivity extends AppCompatActivity {

    Problem[] problems;

    ArrayList<String> listItem = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spinnerTask task = new spinnerTask();
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Typeface font = Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");

        final Button probButton = (Button) findViewById(R.id.addProbButton);
        probButton.setTypeface(font);

        SharedPreferences sharedUserId = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String user_id = sharedUserId.getString("user_id","false");

        final Spinner dropdownCrop = (Spinner) findViewById(R.id.spinnerCrop);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,listItem);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdownCrop.setAdapter(arrayAdapter);

        dropdownCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String item = dropdownCrop.getSelectedItem().toString();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id",user_id));
                params.add(new BasicNameValuePair("crop_name", item));
                System.out.println(params);

                showProblemTask task = new showProblemTask(params);
                task.execute(Urls.server_path + "get_problem.php");

                probButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ProblemActivity.this, R.style.AboutDialog);
                        builder.setTitle("ประเภทหัวข้อ");
                        String[] menu = new String[]{"ความรู้","ปัญหา","รีวิว"};

                        builder.setItems(menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    String topicType = "ความรู้";

                                    Intent intent = new Intent();
                                    intent.putExtra("crop_name", item);
                                    intent.putExtra("topic_type", topicType);
                                    intent.setClass(ProblemActivity.this, AddProblemActivity.class);
                                    startActivity(intent);
                                } else if (i == 1) {
                                    String topicType = "ปัญหา";

                                    Intent intent = new Intent();
                                    intent.putExtra("crop_name", item);
                                    intent.putExtra("topic_type", topicType);
                                    intent.setClass(ProblemActivity.this, AddProblemActivity.class);
                                    startActivity(intent);
                                } else {
                                    String topicType = "รีวิว";

                                    Intent intent = new Intent();
                                    intent.putExtra("crop_name", item);
                                    intent.putExtra("topic_type", topicType);
                                    intent.setClass(ProblemActivity.this, AddProblemActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.setNegativeButton("ยกเลิก", null);
                        builder.show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_problem, menu);
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

    private class showProblemTask extends AsyncTask<String,Void,String> {
        String params;

        public showProblemTask(List<NameValuePair> p) {
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
                System.out.println(result);
                if (json.getInt("success") == 1){
                    JSONArray problem = json.getJSONArray("topic");
                    System.out.println(problem);
                    problems = new Problem[problem.length()];
                    for(int i=0;i<problem.length();i++){
                        JSONObject m = problem.getJSONObject(i);
                        problems[i] = new Problem(m);
                    }
                }

                ListView listProblem = (ListView) findViewById(R.id.list_items);
                ProblemAdapter adapter = new ProblemAdapter(getApplicationContext(),problems);
                listProblem.setAdapter(adapter);

                listProblem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                        final View v = view;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ProblemActivity.this, R.style.AboutDialog);
                        builder.setTitle("ตัวเลือก");
                        String[] menu = new String[]{"แก้ไข","ลบ"};

                        builder.setItems(menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent goEdit = new Intent(getApplicationContext(), EditProblemActivity.class);
                                    goEdit.putExtra("tp_id",problems[position].getId());
                                    goEdit.putExtra("tp_title",problems[position].getTopic());
                                    goEdit.putExtra("tp_detail",problems[position].getDeatil());
                                    goEdit.putExtra("tpt_name",problems[position].getType());
                                    startActivity(goEdit);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProblemActivity.this, R.style.AboutDialog);
                                    builder.setMessage("คุณต้องการลบปัญหานี้หรือไม่?");
                                    builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("tp_id", problems[position].getId()));
                                            System.out.println(params);

                                            deleteProblemTask task = new deleteProblemTask(params);
                                            task.execute(Urls.server_path + "del_problem.php");
                                            onResume();
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
                        });
                        builder.setNegativeButton("ยกเลิก", null);
                        builder.show();
                        return false;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class spinnerTask extends AsyncTask<Void,Void,Void> {
        ArrayList<String> list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            InputStream is = null;
            String result = "";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Urls.server_path + "get_spinnerCrop.php");
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"utf-8"));
                String line = "";
                while ((line=bufferedReader.readLine()) != null) {
                    result+=line;
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = new JSONArray(result);
                System.out.println(result);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(jsonObject.getString("crop_name"));
                    //list.add(jsonObject.getString("crop_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listItem.addAll(list);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private class deleteProblemTask extends AsyncTask<String,Void,String> {
        String params;

        public deleteProblemTask(List<NameValuePair> p) {
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
                Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
 }
