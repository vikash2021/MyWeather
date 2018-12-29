package com.vkk.myweather;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    EditText t1;
    TextView t2;
    public void findWeather(View view)
    {
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(t1.getWindowToken(),0);
        try {
            String encodedCityName=URLEncoder.encode(t1.getText().toString(),"UTF-8");
            DownloadTask task=new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1=(EditText) findViewById(R.id.t1);
        t2=(TextView) findViewById(R.id.t2);

        }

       public class DownloadTask extends AsyncTask<String, Void, String>
       {

           @Override
           protected String doInBackground(String... urls) {

               String result="";
               URL url;
               HttpURLConnection urlConnection = null;
               try {
                   url=new URL(urls[0]);
                   urlConnection = (HttpURLConnection)url.openConnection();
                   InputStream in=urlConnection.getInputStream();
                   InputStreamReader reader=new InputStreamReader(in);
                   int data=reader.read();
                   while(data!=-1)
                   {
                       char current=(char)data;
                       result +=current;
                       data=reader.read();
                   }
                   return result;
               } catch (Exception e) {
                   Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);
               }

               return null;
           }

           @Override
           protected void onPostExecute(String s) {
               super.onPostExecute(s);

               try {
                   String message = "";
                   JSONObject jsonObject = new JSONObject(s);
                   String weatherInfo = jsonObject.getString("weather");
                   JSONArray arr = new JSONArray(weatherInfo);
                   for(int i=0;i<arr.length();i++)
                   {
                       JSONObject jsonPart = arr.getJSONObject(i);
                       String main="";
                       String description="";
                       main=jsonPart.getString("main");
                       description=jsonPart.getString("description");
                       if(main !=""&& description !="")
                       {
                           message += main+":"+description+"\r\n";
                       }
                   }

                   if(message !="")
                   {
                       t2.setText(message);

                   }else
                   {
                       Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);
                   }

               } catch (JSONException e) {

                   Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);
               }

           }
       }




    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }


}
