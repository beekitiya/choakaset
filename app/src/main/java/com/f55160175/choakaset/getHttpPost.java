package com.f55160175.choakaset;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by User on 9/7/2015.
 */
public class getHttpPost {
    public static String makeHttpRequest(String urls,List<NameValuePair> params){
        String strResult="";
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(urls);
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                strResult = readStream(entity.getContent());
            }else {
                Log.e("Log", "Failed error: " + statusLine.getReasonPhrase());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public static String readStream(InputStream in){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();

    }
}
