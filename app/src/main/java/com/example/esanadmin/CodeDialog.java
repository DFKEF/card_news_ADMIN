package com.example.esanadmin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;

public class CodeDialog extends Dialog {

    private Context context;
    private CodeClickListener CodeClickListener;
    private String sId;
    private TextView tvTitle, tvNegative, tvPositive;
    public final String PREFERENCE = "userinfo";

    public CodeDialog(@NonNull Context context, String sId, CodeClickListener CodeClickListener) {
        super(context);
        this.context = context;
        this.sId = sId;
        this.CodeClickListener = CodeClickListener;
    }

    public void createDialog(CodeDialog codeDialog)
    {
        // dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        codeDialog.setCanceledOnTouchOutside(false);
        codeDialog.setCancelable(true);
        codeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        codeDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_dialog);

        tvPositive = findViewById(R.id.okbtn);
        tvNegative = findViewById(R.id.cancelbtn);



        tvPositive.setOnClickListener(v -> {
            EditText e1 = (EditText) findViewById(R.id.first);
            EditText e2 = (EditText) findViewById(R.id.second);
            EditText e3 = (EditText) findViewById(R.id.third);
            EditText e4 = (EditText) findViewById(R.id.fourth);
            String s1,s2,s3,s4,sFinal;
            s1 = e1.getText().toString();
            s2 = e2.getText().toString();
            s3 = e3.getText().toString();
            s4 = e4.getText().toString();
            sFinal = s1+s2+s3+s4;
            // 저장버튼 클릭
            this.CodeClickListener.onPositiveClick(sFinal);
            if(CodeClickListener.onPositiveClick(sFinal)==9) {
                Log.d("TEST", "아아아");
                dismiss();
                SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                SharedPreferences.Editor editor = pref.edit();
                // key값에 value값을 저장한다.
                // String, boolean, int, float, long 값 모두 저장가능하다.
                editor.putString("userID",sId);
                // 메모리에 있는 데이터를 저장장치에 저장한다.
                editor.commit();
                Intent i = new Intent(context,MainActivity.class);
                context.startActivity(i);
            }else{
                Toast.makeText(getContext(),"코드가 불일치합니다.",Toast.LENGTH_LONG).show();
            }

        });
        tvNegative.setOnClickListener(v -> {
            // 취소버튼 클릭
            this.CodeClickListener.onNegativeClick();
            dismiss();
        });
    }
}
