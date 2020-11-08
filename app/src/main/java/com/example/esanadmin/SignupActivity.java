package com.example.esanadmin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    String sId,sPw, sPw_chk,sName, sEmail,subject;
    EditText et_id, et_pw, et_pw_chk,et_email, et_name;
    String url = "http://13.209.232.72/";
    Context context;
    int serverResponseCode = 0;
    private static final int PICK_FROM_ALBUM = 1;
    File tempFile;
    String fileurl;
    ArrayList<String> file = new ArrayList<>();
    public final String PREFERENCE = "salt";
    String[] items = {"국어", "수학", "영어", "과학", "사회", "제2외국어","한문","예체능","기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        et_id = (EditText) findViewById(R.id.createid);
        et_pw = (EditText) findViewById(R.id.createpw);
        et_pw_chk = (EditText) findViewById(R.id.checkpw);
        et_email = (EditText) findViewById(R.id.email);
        et_name = (EditText) findViewById(R.id.name);
        Button joinBtn = (Button) findViewById(R.id.join);
        context = this;
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //API에 만들어져 있는 R.layout.simple_spinner...를 씀
                getApplicationContext(),android.R.layout.simple_spinner_item, items
        );
        //미리 정의된 레이아웃 사용
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // 스피너 객체에다가 어댑터를 넣어줌
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 선택되면
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = items[position];
            }

            // 아무것도 선택되지 않은 상태일 때
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_Join(v,context, subject);
            }
        });






    }


    public void bt_Join(View view, final Context context, String subject)
    {
        new Thread(new Runnable() {
            public void run() {

                /* 버튼을 눌렀을 때 동작하는 소스 */
                sId = et_id.getText().toString();
                sPw = et_pw.getText().toString();
                sPw_chk = et_pw_chk.getText().toString();
                sEmail = et_email.getText().toString();
                sName = et_name.getText().toString();



                if (sId.isEmpty() || sPw.isEmpty() || sEmail.isEmpty() || sName.isEmpty()) {
                    Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_LONG).show();

                } else {
                    if (sPw.equals(sPw_chk)) {
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                            Toast.makeText(SignupActivity.this, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                        } else {

                                if (sId.trim().length() < 5) {
                                    Toast.makeText(SignupActivity.this, "아이디는 최소 5자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (!Pattern.matches("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$", sPw)) {
                                        Toast.makeText(SignupActivity.this, "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(file.isEmpty()) {
                                            registDB registDB = new registDB(sId, sPw, sEmail, sName, context,subject);
                                            registDB.execute();
                                        }else{

                                            Log.e("mmm", String.valueOf(serverResponseCode));
                                            if (serverResponseCode == 200) {
                                                registDB registDB = new registDB(sId, sPw, sEmail, sName, context,subject);
                                                registDB.execute();
                                            }
                                        }

                                    }
                                }

                        }


                    } else {
                        Toast.makeText(context, "패스워드가 일치하지 않습니다", Toast.LENGTH_LONG).show();

                    }
                }
            }
        }).start();

    }

    public class registDB extends AsyncTask<Void, Integer, Integer> {
        String sId, sPw, sEmail, sName, subject;
        Context context;
        int finishcode = 0;
        public registDB(String sId, String sPw, String sEmail, String sName, Context context, String subject) {
            this.sId = sId;
            this.sPw = sPw;
            this.sEmail = sEmail;
            this.sName = sName;
            this.context = context;
            this.subject = subject;
        }

        @Override
        protected Integer doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "&u_email=" + sEmail + "&u_name=" + sName + "&u_subject=" + subject;
            try {
                /* 서버연결 */
                URL home = new URL(
                        url+"teacher_join.php");
                HttpURLConnection conn = (HttpURLConnection) home.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
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
                    finishcode = 9;
                    Log.e("RESULT","성공적으로 처리되었습니다!");

                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Log.e("dd DATA",sId+sEmail+sName+sPw);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return finishcode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == 9) {
                Intent intent = new Intent(context,LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
                Log.e("설마", String.valueOf(integer));
            }else{
                Log.e("dd", String.valueOf(integer));
                Toast.makeText(context,"에러 발생! 아이디 혹은 이메일이 중복되었습니다.",Toast.LENGTH_LONG).show();
                Log.e("asdf",subject);
                super.onPostExecute(integer);
            }
        }
    }





}