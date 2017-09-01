package com.example.admin.ebeliefapp.view;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.ebeliefapp.R;

public class CustomeDialog extends Dialog implements

        android.view.View.OnClickListener {

    public Activity c;
    String s;
    public Dialog d;
    public Button yes, no;
    TextView txt_dia;

    public CustomeDialog(Activity a, String s) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.s=s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        txt_dia = (TextView)findViewById(R.id.txt_dia);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        txt_dia.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;

        }

    }
}