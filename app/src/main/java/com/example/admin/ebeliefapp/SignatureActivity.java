package com.example.admin.ebeliefapp;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kyanogen.signatureview.SignatureView;

public class SignatureActivity extends AppCompatActivity {
    SignatureView signatureView;
    Button saveButton, clearButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        signatureView = (SignatureView) findViewById(R.id.signature_view);
        saveButton = (Button)findViewById(R.id.saveButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        imageView = (ImageView)findViewById(R.id.imageView);

        //disable both buttons at start
        saveButton.setEnabled(true);
        clearButton.setEnabled(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write code for saving the signature here
                Bitmap bitmap = signatureView.getSignatureBitmap();
                imageView.setImageBitmap(bitmap);

                Toast.makeText(SignatureActivity.this, "Signature Saved", Toast.LENGTH_SHORT).show();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
                Toast.makeText(getApplicationContext(),
                        "Clear canvas", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}