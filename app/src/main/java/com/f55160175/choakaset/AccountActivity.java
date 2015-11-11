package com.f55160175.choakaset;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    static final int DATE_DIALOG_ID = 0;
    private int mYear,mMonth,mDay;
    EditText acc_date;

    ArrayList<String> listItem = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ListView list = (ListView) findViewById(R.id.list_items);

        final Spinner dropdownCrop = (Spinner) findViewById(R.id.spinnerCrop);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,listItem);
        dropdownCrop.setAdapter(arrayAdapter);

        dropdownCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String cropName = dropdownCrop.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),cropName,Toast.LENGTH_SHORT).show();

                SharedPreferences shared = getSharedPreferences("set", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("crop_name",cropName);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

        String[] data  = {"20/05/2015", "ซื้อปุ๋ย, 500", "ซื้อเมล็ดพันธุ์, 1000",
                        "23/06/2015", "ได้รับเงินช่วยเหลือ, 2000", "ค่าไถ่ดิน, 150"
                        };

        int[] type = { 0, 1, 0, 1, 0, 1, 0, 1 };
        int size = data.length;
        for (int i = 0; i < size; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            // According to different needs can construct more complex data, the structure of a data
            map.put("data", data[i]);
            listItem.add(map);
        }

        AccountAdapter listItemAdapter = new AccountAdapter(this, listItem, type);
        list.setAdapter(listItemAdapter);

        Typeface font = Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");

        final Button addAccount = (Button) findViewById(R.id.addAccButton);
        addAccount.setTypeface(font);

        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this, R.style.AboutDialog);
                builder.setTitle("บัญชี");
                String[] menu = new String[]{"รายรับ","รายจ่าย"};

                builder.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            final Dialog incomeDialog = new Dialog(AccountActivity.this);
                            incomeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            incomeDialog.setContentView(R.layout.add_account);

                            Button btnSubmit = (Button) incomeDialog.findViewById(R.id.submitAccount);
                            final Button btnCancel = (Button) incomeDialog.findViewById(R.id.cancelAccount);
                            acc_date = (EditText) incomeDialog.findViewById(R.id.timeAccount);
                            final EditText acc_detail = (EditText) incomeDialog.findViewById(R.id.detail);
                            final EditText acc_price = (EditText) incomeDialog.findViewById(R.id.accountPrice);

                            Calendar c=Calendar.getInstance();
                            mYear=c.get(Calendar.YEAR);
                            mMonth=c.get(Calendar.MONTH);
                            mDay=c.get(Calendar.DAY_OF_MONTH);

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            acc_date.setText( sdf.format(c.getTime()));

                            acc_date.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDialog(DATE_DIALOG_ID);
                                }
                            });

                            String date = acc_date.getText().toString();
                            String dateSplit[] = date.split("/");
                            final String newDate = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];

                            final String cost_type = "รายรับ";

                            SharedPreferences shared = getSharedPreferences("set", Context.MODE_PRIVATE);
                            final String crop_name = shared.getString("crop_name","false");

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("acc_detail",acc_detail.getText().toString()));
                                    params.add(new BasicNameValuePair("acc_price",acc_price.getText().toString()));
                                    params.add(new BasicNameValuePair("acc_date",newDate));
                                    params.add(new BasicNameValuePair("acc_cost_type", cost_type));
                                    params.add(new BasicNameValuePair("crop_name",crop_name));

                                    addAccountTask task = new addAccountTask(params);
                                    task.execute(Urls.server_path+"add_account.php");
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    incomeDialog.dismiss();
                                }
                            });

                            incomeDialog.show();
                            incomeDialog.getWindow().setLayout(1000,1000);

                        } else {
                            final Dialog outcomeDialog = new Dialog(AccountActivity.this);
                            outcomeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            outcomeDialog.setContentView(R.layout.add_account);

                            Button btnSubmit = (Button) outcomeDialog.findViewById(R.id.submitAccount);
                            Button btnCancel = (Button) outcomeDialog.findViewById(R.id.cancelAccount);
                            acc_date = (EditText) outcomeDialog.findViewById(R.id.timeAccount);
                            final EditText acc_detail = (EditText) outcomeDialog.findViewById(R.id.detail);
                            final EditText acc_price = (EditText) outcomeDialog.findViewById(R.id.accountPrice);

                            Calendar c=Calendar.getInstance();
                            mYear=c.get(Calendar.YEAR);
                            mMonth=c.get(Calendar.MONTH);
                            mDay=c.get(Calendar.DAY_OF_MONTH);

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            acc_date.setText( sdf.format(c.getTime()));

                            acc_date.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDialog(DATE_DIALOG_ID);
                                }
                            });

                            String date = acc_date.getText().toString();
                            String dateSplit[] = date.split("/");
                            final String newDate = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];

                            final String cost_type = "รายจ่าย";

                            SharedPreferences shared = getSharedPreferences("set", Context.MODE_PRIVATE);
                            final String crop_name = shared.getString("crop_name","false");

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("acc_detail",acc_detail.getText().toString()));
                                    params.add(new BasicNameValuePair("acc_price",acc_price.getText().toString()));
                                    params.add(new BasicNameValuePair("acc_date",newDate));
                                    params.add(new BasicNameValuePair("acc_cost_type", cost_type));
                                    params.add(new BasicNameValuePair("crop_name",crop_name));

                                    addAccountTask task = new addAccountTask(params);
                                    task.execute(Urls.server_path+"add_account.php");
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    outcomeDialog.dismiss();
                                }
                            });

                            outcomeDialog.show();
                            outcomeDialog.getWindow().setLayout(1000,1000);
                        }
                    }
                });
                builder.setNegativeButton("ยกเลิก", null);
                builder.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spinnerTask task = new spinnerTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
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

    private class addAccountTask extends AsyncTask<String,Void,String> {
        String params;

        public addAccountTask(List<NameValuePair> p) {
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
                    Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
                    startActivity(intent);
                }else {
                    System.out.println("fail");
                    Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
                    startActivity(intent);
                }
            }catch (JSONException e){
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
            acc_date.setText(new StringBuilder().append(mDay).append("/").append(mMonth+1).append("/").append(mYear));

        }

    };
}
