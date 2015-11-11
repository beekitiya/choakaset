package com.f55160175.choakaset;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCropFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCropFragment extends Fragment {

    EditText start_date, end_date;

    ArrayList<String> listItem = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    String irrigated = "";

    String newFromDate;
    String newToDate;
    String breedName;

    public AddCropFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_crop, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Spinner dropdownCrop = (Spinner) getView().findViewById(R.id.spinnerBreed);
        arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,listItem);
        dropdownCrop.setAdapter(arrayAdapter);

        dropdownCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                breedName = dropdownCrop.getSelectedItem().toString();
                Toast.makeText(getActivity(), breedName, Toast.LENGTH_SHORT).show();

                /*SharedPreferences shared = getActivity().getSharedPreferences("set", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("breed_name", breedName);
                editor.commit();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final EditText txtFarmName = (EditText) getView().findViewById(R.id.inFarmname);
        final EditText txtField = (EditText) getView().findViewById(R.id.field);
        final EditText txtNgarg = (EditText) getView().findViewById(R.id.ngarn);
        final EditText txtWah = (EditText) getView().findViewById(R.id.wah);
        final EditText txtProduct = (EditText) getView().findViewById(R.id.inProductQty);
        final CheckBox chkIrrigation = (CheckBox) getView().findViewById(R.id.checkIrrigated);
        Button btnSubmit = (Button) getView().findViewById(R.id.submitCrop);
        Button btnCancel = (Button) getView().findViewById(R.id.cancelCrop);

        start_date = (EditText) getView().findViewById(R.id.inBeginDate);
        end_date = (EditText) getView().findViewById(R.id.inEndDate);

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromDatePicker();
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToDatePicker();
            }
        });

        chkIrrigation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (chkIrrigation.isClickable()) {
                    irrigated = "1";
                } else {
                    irrigated = "0";
                }
            }
        });

        /*SharedPreferences shared = getActivity().getSharedPreferences("set", Context.MODE_PRIVATE);
        final String breed_name = shared.getString("breed_name", "false");*/

        SharedPreferences sharedUserId = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        final String user_id = sharedUserId.getString("user_id", null);

        SharedPreferences sharedLocate = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
        final String lat = sharedLocate.getString("lat", "false");
        final String lng = sharedLocate.getString("lng", "false");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("user_id",user_id));
                params.add(new BasicNameValuePair("crop_name",txtFarmName.getText().toString()));
                params.add(new BasicNameValuePair("breed_name",breedName));
                params.add(new BasicNameValuePair("field",txtField.getText().toString()));
                params.add(new BasicNameValuePair("ngarn",txtNgarg.getText().toString()));
                params.add(new BasicNameValuePair("wah",txtWah.getText().toString()));
                params.add(new BasicNameValuePair("product",txtProduct.getText().toString()));
                params.add(new BasicNameValuePair("begin_date",newFromDate));
                params.add(new BasicNameValuePair("crop_date",newToDate));
                params.add(new BasicNameValuePair("irrigated",irrigated));
                params.add(new BasicNameValuePair("lat",lat));
                params.add(new BasicNameValuePair("lng",lng));
                System.out.println(params);

                addCropTask task = new addCropTask(params);
                task.execute(Urls.server_path+"add_crop.php");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goBack = new Intent(getActivity(),MainActivity.class);
                startActivity(goBack);
            }
        });
    }

    private void showFromDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(fromdate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            start_date.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1)+ "/" + String.valueOf(year));

            String date = start_date.getText().toString();
            String dateSplit[] = date.split("/");
            newFromDate = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];
        }
    };

    private void showToDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(todate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            end_date.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year));

            String todate = end_date.getText().toString();
            String todateSplit[] = todate.split("/");
            newToDate = todateSplit[2] + "-" + todateSplit[1] + "-" + todateSplit[0];
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        spinnerTask task = new spinnerTask();
        task.execute();
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
                HttpPost httpPost = new HttpPost(Urls.server_path + "get_spinnerBreed.php");
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
                    list.add(jsonObject.getString("breed_name"));
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

    private class addCropTask extends AsyncTask<String,Void,String> {
        String params;

        public addCropTask(List<NameValuePair> p) {
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
                    Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }else {
                    System.out.println("fail");
                    Toast.makeText(getActivity(),json.getString("message"),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}

