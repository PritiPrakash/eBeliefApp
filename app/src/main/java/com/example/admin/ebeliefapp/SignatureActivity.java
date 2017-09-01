package com.example.admin.ebeliefapp;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.ebeliefapp.Constants.ServerRequestConstants;
import com.kyanogen.signatureview.SignatureView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

public class SignatureActivity extends AppCompatActivity {
    SignatureView signatureView;
    Button saveButton, clearButton, uploadButton;
    ImageView imageView;
    String sResponse;
    StringBuilder s = new StringBuilder();
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        signatureView = (SignatureView) findViewById(R.id.signature_view);
        saveButton = (Button)findViewById(R.id.saveButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        imageView = (ImageView)findViewById(R.id.imageView);

        //disable both buttons at start
        saveButton.setEnabled(true);
        clearButton.setEnabled(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write code for saving the signature here
                bitmap = signatureView.getSignatureBitmap();
                imageView.setImageBitmap(bitmap);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  SendHttpRequestTask t = new SendHttpRequestTask();

                String[] params = new String[]{ServerRequestConstants.BaseUrl, "img_sign", "Sign Upload"};
                t.execute(params);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String lattitude = params[1];
            String longitude = params[2];


            try {
//                Bitmap photo = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.logo);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                HttpClient client = new DefaultHttpClient();

                HttpPost post = new HttpPost(url);
                MultipartEntity multiPart = new MultipartEntity();
                multiPart.addPart("param1", new StringBody(lattitude));
                multiPart.addPart("param2", new StringBody(longitude));
//                multiPart.addPart("file", new ByteArrayBody(baos.toByteArray(), "logo.png"));
                multiPart.addPart("image", new StringBody(encodedImage1));
                post.setEntity(multiPart);

                HttpResponse response = client.execute(post);


                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));


                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                System.out.println("Response: " + s);

            } catch (Throwable t) {
                // Handle error here

                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
//            item.setActionView(null);
            Toast.makeText(SignatureActivity.this, "Sign Respo ::: " + s, Toast.LENGTH_SHORT).show();
        }


    }

}