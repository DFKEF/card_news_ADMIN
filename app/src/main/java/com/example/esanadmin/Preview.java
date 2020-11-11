package com.example.esanadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Preview extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context;
    String url = "http://13.209.232.72/";
    ArrayList<String> bitmaplist = new ArrayList<>();
    ArrayList<Item> list = new ArrayList<>();
    String bitmaprow, bitmap;
    TextView titletxt, contenttxt, contentSub, contentUser;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doFullScreen();
        setContentView(R.layout.activity_preview);
        Intent i = getIntent();
        String user = i.getStringExtra("user");
        String title = i.getStringExtra("title");
        String date = i.getStringExtra("date");
        bitmaprow = i.getStringExtra("bitmap");
        String subject = i.getStringExtra("subject");
        String content = i.getStringExtra("content");
        String profile = i.getStringExtra("profile");
        String getid = i.getStringExtra("id");
        int imgnum = i.getIntExtra("imgnum",0);
        list.add(new Item(user,bitmaprow,title,date,subject,content,imgnum, getid));
        for(int j=1; j<=imgnum; j++) {
            bitmap = bitmaprow.replace("_1.jpg","_"+j+".jpg");
            Log.e("dhjdh",bitmap);
            bitmaplist.add(bitmap);
        }

        context = this;

        titletxt = (TextView) findViewById(R.id.content_title);
        contenttxt = (TextView) findViewById(R.id.content_text);
        contentSub = (TextView) findViewById(R.id.content_subject);
        contentUser = (TextView) findViewById(R.id.content_user);

        titletxt.setText(title);
        contenttxt.setText(content);
        contentSub.setText(subject+" | "+date);
        contentUser.setText(user);

        ImageView profileimg = (ImageView) findViewById(R.id.profileImg);
        profileimg.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileimg.setClipToOutline(true);
        }
        Glide.with(this)
                .load(profile)
                .centerCrop()
                .override(50,50)
                .into(profileimg);

        new init().execute();

        apply = (Button) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Preview.this);

                builder.setTitle("검토").setMessage("정말 승인하시겠습니까?");

                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SetVerify setVerify = new SetVerify();
                        setVerify.execute();
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        Button delete = (Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {AlertDialog.Builder builder = new AlertDialog.Builder(Preview.this);

                builder.setTitle("검토").setMessage("정말 삭제하시겠습니까?");

                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Delete setD = new Delete();
                        setD.execute();
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }


    private void doFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    public class init extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerView = (RecyclerView) findViewById(R.id.image_rview);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            ImageRecycler adapter = new ImageRecycler(getApplicationContext(),bitmaplist,list);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    public class SetVerify extends AsyncTask<Void, Integer, Integer > {

        String data = "";
        String sId;
        String imgurl;
        int finishcode = 0;

        public SetVerify() {
        }
        @Override
        protected Integer  doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            imgurl = list.get(0).bitmap.replaceAll("http://13.209.232.72/cards/","");
            String param = "id=" + list.get(0).getid+ "&bitmap=" + imgurl;
            Log.e("봐봐",param);
            try {
                // 서버연결
                URL home = new URL(url
                        +"article_verify.php");
                HttpURLConnection conn = (HttpURLConnection) home.openConnection();
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
                Toast.makeText(getApplicationContext(),"정상적으로 처리되었습니다!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"오류가 발생했습니다!\n계속 발생한다면 개발자에게 문의해주세요.",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class Delete extends AsyncTask<Void, Integer, Integer > {

        String data = "";
        String sId;
        String imgurl;
        int finishcode = 0;

        public Delete() {
        }
        @Override
        protected Integer  doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            imgurl = list.get(0).bitmap.replaceAll("http://13.209.232.72/cards/","");
            String param = "id=" + list.get(0).getid+ "&bitmap=" + imgurl;
            Log.e("봐봐",param);
            try {
                // 서버연결
                URL home = new URL(url
                        +"article_delete.php");
                HttpURLConnection conn = (HttpURLConnection) home.openConnection();
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
                Toast.makeText(getApplicationContext(),"정상적으로 처리되었습니다!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"오류가 발생했습니다!\n계속 발생한다면 개발자에게 문의해주세요.",Toast.LENGTH_LONG).show();
            }
        }
    }
}