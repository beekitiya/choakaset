package com.f55160175.choakaset;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 11/1/2015.
 */
public class JsonHttp {

    public static String getWebText(String strUrl){
        String strResult = "";
        try{
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            strResult = readStreamData(con.getInputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
        return strResult;
    }

    public static String readStreamData(InputStream in){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try{
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine())!=null){
                sb.append(line+"\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
