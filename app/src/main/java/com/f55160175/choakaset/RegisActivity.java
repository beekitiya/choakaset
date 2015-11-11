package com.f55160175.choakaset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisActivity extends FragmentActivity {
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;

    CircleImageView profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        profilepic = (CircleImageView) findViewById(R.id.profileupload);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImage();
                /*Intent galley = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galley,SELECT_PICTURE);*/
            }
        });

        final EditText fname = (EditText) findViewById(R.id.fnameText);
        final EditText lname = (EditText) findViewById(R.id.lnameText);
        final EditText username = (EditText) findViewById(R.id.usernameRegis);
        final EditText email = (EditText) findViewById(R.id.emailRegis);
        final EditText passRegis = (EditText) findViewById(R.id.passwordRegis);
        final EditText confirmPass = (EditText) findViewById(R.id.confirmPass);
        Button btnRegis = (Button) findViewById(R.id.buttonRegis);
        Button btnCancel = (Button) findViewById(R.id.buttonCancel);

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();

                final String name = firstname+" "+lastname;

                if(!validateEmail(email.getText().toString().trim())){
                    email.setError("รูปแบบ E-mail ไม่ถูกต้อง");
                    email.requestFocus();
                } else if(!validatePassword(passRegis.getText().toString(),confirmPass.getText().toString())) {
                    confirmPass.setError("รหัสผ่านไม่ตรงกัน");
                    confirmPass.requestFocus();
                } else {
                    
                    //uploadImage();
                    /*Bitmap bitmap = ((BitmapDrawable) profilepic.getDrawable()).getBitmap();
                    new registerTask(bitmap, firstname,name,username.getText().toString(),email.getText().toString(),passRegis.getText().toString()).execute();*/
                    SharedPreferences sharedImage = getSharedPreferences("image", Context.MODE_PRIVATE);
                    final String pic = sharedImage.getString("img_profile",null);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("member_id",firstname));
                    params.add(new BasicNameValuePair("name",name));
                    params.add(new BasicNameValuePair("username",username.getText().toString()));
                    params.add(new BasicNameValuePair("email",email.getText().toString()));
                    params.add(new BasicNameValuePair("password",passRegis.getText().toString()));
                    params.add(new BasicNameValuePair("picture",pic));
                    System.out.println(params);

                    RegisterTask task = new RegisterTask(params);
                    task.execute(Urls.server_path+"register.php");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goback = new Intent(RegisActivity.this, LoginActivity.class);
                startActivity(goback);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regis, menu);
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

    private class RegisterTask extends AsyncTask<String,Void,String> {
        String params;

        public RegisterTask(List<NameValuePair> p) {
            params = URLEncodedUtils.format(p, "utf-8");
            System.out.println(params);
        }

        @Override
        protected String doInBackground(String... urls){
            System.out.println(urls[0] + "?" + params);
            System.out.println("eiei");
            return JsonHttp.getWebText(urls[0] + "?" + params);
        }
        @Override
        protected void onPostExecute(String result){
            try {
                System.out.println("hi");
                JSONObject json = new JSONObject(result);
                if(json.getInt("success") == 1){
                    System.out.println("hello");
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }else if(json.getInt("success") == 2) {
                    System.out.println("same");
                    Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
               }else {
                    System.out.println("fail");
                    Toast.makeText(getApplicationContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    protected boolean validatePassword(String pass, String confirm) {
        if(pass.equals(confirm)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean validateEmail(String email) {
        String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == SELECT_FILE) {
                onSelectFromGallery(data);
            } else if(requestCode == REQUEST_CAMERA) {
                onCaptureImage(data);
            }
        }

        /*if(requestCode == RESULT_OK) {
            if(requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if(Build.VERSION.SDK_INT < 19) {
                    String selectedImagePath = getPath(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    setImage(bitmap);
                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                        setImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }*/
        /*if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profilepic.setImageURI(selectedImage);
        }*/
        /*if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                profilepic.setImageBitmap(image);
                String imageDta = encodeToBase64(image);

                final List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("image",imageDta));

                new AsyncTask<ApiConnetor, Long, Boolean>() {
                    @Override
                    protected Boolean doInBackground(ApiConnetor... apiConnetors) {
                        return apiConnetors[0].uploadImageToserver(params);
                    }
                }.execute(new ApiConnetor());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static String encodeToBase64(Bitmap image) {
        System.gc();

        if(image == null) return null;

        Bitmap imagex = image;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagex.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        String imageEnode = Base64.encodeToString(bytes, Base64.DEFAULT);
        return imageEnode;
    }

    /*public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }*/

    /*private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String>{

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisActivity.this, "Uploading...", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();

                data.put(UPLOAD_KEY, uploadImage);
                String result = rh.sendPostRequest(Urls.server_path+"register.php",data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }*/

    /*private class registerTask extends AsyncTask<Void, Void, Void> {
        Bitmap image;
        String email;
        String password;
        String name;
        String username;
        String firstname;

        public registerTask(Bitmap image, String firstname, String name,String username, String email, String password) {
            this.image = image;
            this.email = email;
            this.password = password;
            this.name = name;
            this.firstname = firstname;
            this.username = username;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("picture",encodeImage));
            params.add(new BasicNameValuePair("username",username));
            params.add(new BasicNameValuePair("member_id",firstname));
            params.add(new BasicNameValuePair("name",name));
            params.add(new BasicNameValuePair("email",email));
            params.add(new BasicNameValuePair("password", password));
            System.out.println(params);

            HttpParams httpParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(Urls.server_path+"register.php");

            try{
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                client.execute(httpPost);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "สมัครสมาชิกสำเร็จ", Toast.LENGTH_SHORT).show();
        }
    }

    private HttpParams getHttpRequestParams() {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
        return httpRequestParams;
    }*/

    private void selectedImage() {
        final String[] items = new String[]{"Take a photo", "Choose from gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
                }
            }
        });
        builder.setNegativeButton("ยกเลิก",null);
        builder.show();
    }

    private void onCaptureImage(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        System.out.println(destination);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profilepic.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGallery(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        System.out.println(selectedImagePath);

        Bitmap bm;
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;*/
        bm = BitmapFactory.decodeFile(selectedImagePath);

        profilepic.setImageBitmap(bm);

        String image = encodeToBase64(bm);
        System.out.println(image);

        SharedPreferences sharedUserId = getSharedPreferences("image", Context.MODE_PRIVATE);
        SharedPreferences.Editor sendImage = sharedUserId.edit();
        sendImage.putString("img_profile", image);
        sendImage.commit();
    }
}
