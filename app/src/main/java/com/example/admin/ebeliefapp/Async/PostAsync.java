package com.example.admin.ebeliefapp.Async;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import com.example.admin.ebeliefapp.Constants.ServerRequestConstants;
import com.example.admin.ebeliefapp.Constants.Tags;
import com.example.admin.ebeliefapp.Interfaces.OnReciveServerResponse;
import com.example.admin.ebeliefapp.Models.PostAysnc_Model;
import com.example.admin.ebeliefapp.utility.AppDelegate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * BMA_POSTASYNC class is used to execute POST Request By Using this class we
 * can send Data(image, text, multimedia content etc) on Server
 */

@SuppressWarnings("deprecation")
public class PostAsync extends AsyncTask<String, Void, String> {

    /**
     * BMA_GETASYNC Members Declarations
     */
    private OnReciveServerResponse mOnReciveServerResponse;
    private String str_async_type;
    private String str_PostApiName;
    private String str_PostRequestURL;
    private ArrayList<PostAysnc_Model> arr_PostModels;
    private Fragment fragment;
    @SuppressWarnings("unused")
    private Context mContext;
    private String str_status;

    /**
     * Constructor Implementations
     */
    public PostAsync(Context context,
                     OnReciveServerResponse onReciveServerResponse, String mPostApiName,
                     String mPostRequestURL, ArrayList<PostAysnc_Model> mPostModelArray,
                     Fragment fragment, String str_async_type) {
        this.mOnReciveServerResponse = onReciveServerResponse;
        this.str_PostApiName = mPostApiName;
        this.str_PostRequestURL = mPostRequestURL;
        this.arr_PostModels = mPostModelArray;
        this.fragment = fragment;
        this.mContext = context;
        this.str_async_type = str_async_type;
    }

    /**
     * Constructor Implementations
     */
    public PostAsync(Context context,
                     OnReciveServerResponse onReciveServerResponse,
                     String mPostRequestURL, ArrayList<PostAysnc_Model> mPostModelArray,
                     Fragment fragment, String str_async_type) {
        this.mOnReciveServerResponse = onReciveServerResponse;
        this.str_PostApiName = mPostRequestURL;
        this.str_PostRequestURL = mPostRequestURL;
        this.arr_PostModels = mPostModelArray;
        this.fragment = fragment;
        this.mContext = context;
        this.str_async_type = str_async_type;
    }


    public static HttpParams getHttpParameters() {
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, 60000);
        return httpParameters;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpClient mHttpClient = new DefaultHttpClient(getHttpParameters());
            HttpContext mHttpContext = new BasicHttpContext();
            HttpPost mHttpPost = new HttpPost(str_PostRequestURL);
            HttpGet mHttpGet = new HttpGet(str_PostRequestURL);

            MultipartEntity mMultipartEntity = null;

            for (int i = 0; i < arr_PostModels.size(); i++) {
                if (arr_PostModels.get(i).getStr_PostParamType().equalsIgnoreCase((ServerRequestConstants.Key_PostFileValue))) {
                    if (mMultipartEntity == null)
                        mMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    if (arr_PostModels.get(i).getObj_PostParamValue() != null) {
                        mMultipartEntity.addPart(arr_PostModels.get(i).getStr_PostParamKey(), new FileBody(new File((String) arr_PostModels.get(i).getObj_PostParamValue())));
                        mMultipartEntity.addPart(arr_PostModels.get(i).getStr_PostParamKey(), new StringBody((String)arr_PostModels.get(i).getObj_PostParamValue()));

                        AppDelegate.LogUP("param => " + arr_PostModels.get(i).getStr_PostParamKey() + " = " + arr_PostModels.get(i).getObj_PostParamValue());
                    }
                }
            }

            if (arr_PostModels.size() > 0) {
                for (int i = 0; i < arr_PostModels.size(); i++) {
                    if (i == 0) {
                        str_PostRequestURL = str_PostRequestURL + "?" + arr_PostModels.get(i).getStr_PostParamKey() + "=" + arr_PostModels.get(i).getObj_PostParamValue();
                    } else if (!arr_PostModels.get(i).getStr_PostParamType().equalsIgnoreCase(ServerRequestConstants.Key_PostFileValue)) {
                        str_PostRequestURL = str_PostRequestURL + "&" + arr_PostModels.get(i).getStr_PostParamKey() + "=" + arr_PostModels.get(i).getObj_PostParamValue();
                    }
                }
                str_PostRequestURL = AppDelegate.getFilterdUrl(str_PostRequestURL);

                if (str_async_type.equalsIgnoreCase(Tags.POST)) {

                    AppDelegate.LogUA(Tags.POST + " = " + str_PostRequestURL);
                    mHttpPost = new HttpPost(str_PostRequestURL);

                   // mHttpPost.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(Credentials.BASIC_AUTH_USERNAME, Credentials.BASIC_AUTH_PASSWORD), "UTF-8", false));
                   // mHttpPost.addHeader(new BasicHeader(Credentials.HEADER_KEY_1, Credentials.HEADER_VALUE_1));
                   // mHttpPost.addHeader(new BasicHeader(Credentials.HEADER_KEY_2, Credentials.HEADER_VALUE_2));
                  //  mHttpPost.addHeader(new BasicHeader(Credentials.HEADER_KEY_4, Credentials.HEADER_VALUE_4));
                  //  mHttpPost.addHeader(new BasicHeader(Credentials.HEADER_KEY_5, Credentials.HEADER_VALUE_5));
                  //  mHttpPost.addHeader(new BasicHeader(Credentials.HEADER_KEY_6, Credentials.HEADER_VALUE_61));

                    if (mMultipartEntity != null) {
                        AppDelegate.LogUP("mMultipartEntity = " + mMultipartEntity);
                        mHttpPost.setEntity(mMultipartEntity);
                    }
                } else {
                    mHttpGet = new HttpGet(str_PostRequestURL);
                   /* mHttpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(Credentials.BASIC_AUTH_USERNAME, Credentials.BASIC_AUTH_PASSWORD), "UTF-8", false));
                    mHttpGet.addHeader(new BasicHeader(Credentials.HEADER_KEY_1, Credentials.HEADER_VALUE_1));
                    mHttpGet.addHeader(new BasicHeader(Credentials.HEADER_KEY_2, Credentials.HEADER_VALUE_2));
                    mHttpGet.addHeader(new BasicHeader(Credentials.HEADER_KEY_4, Credentials.HEADER_VALUE_4));
                    mHttpGet.addHeader(new BasicHeader(Credentials.HEADER_KEY_5, Credentials.HEADER_VALUE_5));
                    mHttpGet.addHeader(new BasicHeader(Credentials.HEADER_KEY_6, Credentials.HEADER_VALUE_6));
                    mHttpGet.addHeader(new BasicHeader(Credentials.HEADER_KEY_6, Credentials.HEADER_VALUE_6));*/

                    AppDelegate.LogUA(Tags.GET + " = " + str_PostRequestURL);
                }
            }

            HttpResponse mHttpResponse;
            if (str_async_type.equalsIgnoreCase(Tags.POST)) {
                mHttpResponse = mHttpClient.execute(mHttpPost, mHttpContext);
            } else {
                mHttpResponse = mHttpClient.execute(mHttpGet);
            }


            if (mHttpResponse != null) {
                HttpEntity mHttpEntity = mHttpResponse.getEntity();
                InputStream mInputStream = mHttpEntity.getContent();
                if (mInputStream != null) {
                    String mPostResult = AppDelegate.convertStreamToString(mInputStream);
                    if (mPostResult != null) {
                        return mPostResult;
                    } else {
                        AppDelegate.LogE("mPostResult = null for api = " + str_PostApiName);
                        return null;
                    }
                } else {
                    AppDelegate.LogE("mInputStream = null for api = " + str_PostApiName);
                    return null;
                }
            } else {
                AppDelegate.LogE("mHttpResponse = null for api = " + str_PostApiName);
                return null;
            }
        } catch (UnknownHostException unkownHostEx) {
            AppDelegate.LogE(unkownHostEx);
            AppDelegate.LogE("for api = " + str_PostApiName, unkownHostEx);
        } catch (Exception e) {
            AppDelegate.LogE("for api = " + str_PostApiName, e);
        }
        return null;
    }

    /**
     * This Method is called after the execution finish
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("status")) {
                if (jsonObject.getString("status").equalsIgnoreCase("404")) {
                    str_status = "failed";
                }

            } else {
                str_status = "success";
            }
        } catch (Exception e) {
            try {

                JSONArray jsonArray = new JSONArray(result);
                if (result.length() == 3 && jsonArray.length() == 0) {
                    str_status = "success";
                } else if (jsonArray.length() != 0)
                    str_status = "success";
                else {
                    str_status = "failed";
                }
            } catch (JSONException e1) {

                e1.printStackTrace();

            }

        }
        if (fragment == null) {
            AppDelegate.LogUR(result);
            if (mOnReciveServerResponse != null)
                this.mOnReciveServerResponse.setOnReciveResult(str_PostApiName, result, str_status);
            else AppDelegate.LogE("Interface is null at PostAsync class");
        } else if (fragment != null && fragment.isAdded()) {
            AppDelegate.LogUR(result);
            if (mOnReciveServerResponse != null)
                this.mOnReciveServerResponse.setOnReciveResult(str_PostApiName, result, str_status);
        } else {
            AppDelegate.getInstance(mContext).hideProgressDialog(mContext);
        }
    }
}
