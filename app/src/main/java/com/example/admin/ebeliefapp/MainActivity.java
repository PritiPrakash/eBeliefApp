package com.example.admin.ebeliefapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.ebeliefapp.Constants.ServerRequestConstants;
import com.example.admin.ebeliefapp.view.CustomeDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import static com.example.admin.ebeliefapp.PermissionUtils.CAMERA_PERMISSION_ID;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Button btn_capture_img, btn_geo_locn, btn_sign, btn_scan, btn_upload_data;
    ImageView img_viw_one, img_viw_two, img_viw_three, img_viewer;
    ListView list_notice;
    WebView webview_notice;
    TextView txt_mo_no;
    private Uri mUri;
    private static final int TAKE_PICTURE = 0;
    public static Bitmap bitmap1, bitmap2, bitmap3;
    private static final int SCAN = 49374;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    final Calendar calendar = Calendar.getInstance();
    String str_date, strLat = "bla", strLon = "bla";
    String imgname;
    NoticeListAdapter noticeListAdapter;
    ArrayList<String> noticeArray = new ArrayList<String>();
    int count = 0;
    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */


    String sResponse;
    StringBuilder s = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_mo_no = (TextView) findViewById(R.id.txt_mo_no);
        btn_capture_img = (Button) findViewById(R.id.btn_capture_img);
        btn_geo_locn = (Button) findViewById(R.id.btn_geo_locn);
        btn_sign = (Button) findViewById(R.id.btn_sign);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_upload_data = (Button) findViewById(R.id.btn_upload_data);
        img_viw_one = (ImageView) findViewById(R.id.img_viw_one);
        img_viw_two = (ImageView) findViewById(R.id.img_viw_two);
        img_viw_three = (ImageView) findViewById(R.id.img_viw_three);
        img_viewer = (ImageView) findViewById(R.id.img_viewer);
        list_notice = (ListView) findViewById(R.id.list_notice);
        webview_notice = (WebView) findViewById(R.id.webview_notice);

        appPermissionChecking();
        //additems in noticeArray...
        addItemInArray();

        btn_capture_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_date = String.valueOf(dateFormat.format(calendar.getTime()));
                imgname = "Pic" + str_date + "Belief.jpg";

                //camera intent...
                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                File f = new File(Environment.getExternalStorageDirectory() + "/BELIEF_IMG/", imgname);
                startActivityForResult(i, TAKE_PICTURE);
            }
        });

        btn_geo_locn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });


        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignatureActivity.class);
                startActivity(i);
            }
        });

        btn_upload_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* SendHttpRequestTask t = new SendHttpRequestTask();

                String[] params = new String[]{ServerRequestConstants.BaseUrl, strLat, strLon};
                t.execute(params);*/
                CustomeDialog customDialog = new CustomeDialog(MainActivity.this, "Hiiiii");
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.show();
            }
        });

        noticeListAdapter = new NoticeListAdapter(getApplicationContext(), noticeArray);
        list_notice.setAdapter(noticeListAdapter);

        list_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                webview_notice.getSettings().setJavaScriptEnabled(true);
                webview_notice.getSettings().setPluginState(WebSettings.PluginState.ON);
                webview_notice.setWebViewClient(new Callback());
                String pdfURL = null;
                if (position == 0)
                    pdfURL = "http://dl.dropboxusercontent.com/u/37098169/Course%20Brochures/AND101.pdf";
                else if (position == 1)
                    pdfURL = "https://drive.google.com/drive/folders/0B_8Ttd6cOnC0TDVzZzB5VGhBRkk";
                webview_notice.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdfURL);

            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void getUserSimNumber() {
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        txt_mo_no.setText(mPhoneNumber);
    }

    private void addItemInArray() {
        for (int i = 0; i <= 5; i++) {
            noticeArray.add(i, "Here is your link " + (i + 1));
        }
    }

    private void appPermissionChecking() {
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
        };
        PermissionUtils.requestPermissions(this, CAMERA_PERMISSION_ID, permissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    count++;
                    switchBtnColor(count, bitmap);
                    if (count >= 3) {
                        btn_capture_img.setBackgroundColor(Color.LTGRAY);
                        btn_capture_img.setClickable(false);
                    } else {
                        btn_capture_img.setClickable(true);
                    }
                }
            default:
                if (requestCode == SCAN) {
                    IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (scanningResult != null) {
                        String scanContent = scanningResult.getContents();
                        Toast.makeText(getApplicationContext(), "RESULT : " + scanContent, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
                    }
                }

        }
    }

    private void switchBtnColor(int count, Bitmap bitmap) {
        switch (count) {
            case 1:
                bitmap1 = bitmap;
                img_viw_one.setImageBitmap(bitmap);
                storeIMGonSDCard(bitmap, imgname);
                break;
            case 2:
                bitmap2 = bitmap;
                img_viw_two.setImageBitmap(bitmap);
                storeIMGonSDCard(bitmap, imgname);
                img_viw_two.setVisibility(View.VISIBLE);

                break;
            case 3:
                bitmap3 = bitmap;
                img_viw_three.setImageBitmap(bitmap);
                storeIMGonSDCard(bitmap, imgname);
                img_viw_three.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void storeIMGonSDCard(Bitmap bitmap, String imgname) {
        //creating directory...
        File sdcard = Environment.getExternalStorageDirectory();
        File f = new File(sdcard + "/BELIEF_IMG");
        if (!f.exists()) {
            f.mkdir();
        }
        //writing file on device internal storage...
        File outputFile = new File(f, imgname);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //get master sim number...
        getUserSimNumber();
        if (!checkPlayServices()) {
            Toast.makeText(getApplicationContext(), "Please install Google Play services.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }


    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


       /* if (mLocation != null) {
          ///  Toast.makeText(getApplicationContext(), "LAT : " + mLocation.getLatitude() + " , LON : " + mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }*/


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            Toast.makeText(getApplicationContext(), "LATttt : " + location.getLatitude() + " , LONnnn : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            strLat = String.valueOf(location.getLatitude());
            strLon = String.valueOf(location.getLongitude());
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String lattitude = params[1];
            String longitude = params[2];


            try {
//                Bitmap photo = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.logo);
                Bitmap photo = bitmap1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
            Toast.makeText(MainActivity.this, "Respo ::: " + s, Toast.LENGTH_SHORT).show();
        }


    }


}