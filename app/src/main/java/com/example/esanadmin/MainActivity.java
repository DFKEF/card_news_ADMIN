package com.example.esanadmin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final String PREFERENCE = "userinfo";
    String home = "http://35.234.10.58/";

    String user,bitmap,title,date, teacher, subject,content,getid, name;
    ArrayList<Item> list = new ArrayList<>();
    ArrayList<String> namelist = new ArrayList<>();
    int imgnum,code;
    URLConnector task;
    RecyclerAdapter adapter;
    RecyclerView rview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        final String result = pref.getString("userID","");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if(result.isEmpty()) {
            Intent io = new Intent(this,LoginActivity.class);
            startActivity(io);
        }else{
            getname getname = new getname(result);
            getname.execute();
        }



        init();

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                Task(name);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    private void init(){
        rview = (RecyclerView)findViewById(R.id.rview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        rview.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(this, list);
        rview.addItemDecoration(new ItemDecoration(this));
        rview.setAdapter(adapter);;
    }

    private void nameParse(String result) throws JSONException {
        String one =  result.replace("\"", "");
        String two = one.replace("name","");
        String th = two.replace(":","");
        name = th.replace(" ","");
        Log.e("uID" , name);
        Task(name);
    }
    private void Task(String name){
        task = new URLConnector(home+"parselist.php");
        task.start();
        try{
            task.join();
        }
        catch(InterruptedException e){

        }
        String result = task.getResult();

        try {
            Parse(result,name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Parse(String result,String name) throws JSONException {
        JSONObject root = new JSONObject(result);

        JSONArray ja = root.getJSONArray("result");
        Log.e("uID" , name);
        for(int i = 0; i < ja.length();i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            teacher = jo.getString("teacher");
            String valid = jo.getString("verify");

                if(valid.equals("N")) {
                    if(teacher.equals(name)){
                        user = jo.getString("user");
                        bitmap = home+"cards/"+jo.getString("bitmap");
                        title = jo.getString("title");
                        date = jo.getString("date");
                        subject = jo.getString("subject");
                        content = jo.getString("content");
                        imgnum = jo.getInt("imgnum");
                        getid = jo.getString("userID");
                        Log.e("test" , user);
                        list.add(new Item(user,bitmap,title,date,subject,content,imgnum, getid));
                        adapter.notifyDataSetChanged();
                        rview.setAdapter(adapter);
                    }
                }
            }
        }


    private void startTask(String data){
        String result = data;

        try {
            nameParse(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class getname extends AsyncTask<Void, String , String  > {

        String data = "";
        String sId;
        int imgnum;
        int finishcode = 0;

        public getname(String sId) {
            this.sId = sId;
        }
        @Override
        protected String   doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            String param = "u_id=" + sId;
            try {
                // 서버연결
                URL url = new URL(home
                        +"teacher_name.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                // 안드로이드 -> 서버 파라메터값 전달
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                // 서버 -> 안드로이드 파라메터값 전달
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA",data);

                if(data.isEmpty()) {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Log.e("dd DATA",sId);
                    return data;
                }
                else {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    Log.e("name",data);
                    return data;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return data;
        }

        @Override
        protected void onPostExecute(String  data) {
            if(data.isEmpty()) {
                Log.e("name",data);
                Toast.makeText(getApplicationContext(),"오류!",Toast.LENGTH_LONG).show();
            }else {
                Log.e("name",data);
                startTask(data);
            }
        }
    }

    public class SetVerify extends AsyncTask<Void, Integer, Integer > {

        String data = "";
        String sId;
        int imgnum;
        int finishcode = 0;
        int integer;

        public SetVerify(int integer) {
            this.integer = integer;
        }
        @Override
        protected Integer  doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            String param = "id=" + list.get(integer).getid+ "&bitmap" + list.get(integer).bitmap;
            try {
                // 서버연결
                URL url = new URL(home
                        +"article_verify.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                // 안드로이드 -> 서버 파라메터값 전달
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                // 서버 -> 안드로이드 파라메터값 전달
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA",data);


                if(data.equals("0000")) {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    finishcode = 9;
                }
                else {
                    Log.e("RESULT","에러우 발생! ERRCODE = " + data);
                    finishcode = 0;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return finishcode;
        }

        @Override
        protected void onPostExecute(Integer finishcode) {
            if(finishcode==9) {

            }else{

            }
        }
    }
}