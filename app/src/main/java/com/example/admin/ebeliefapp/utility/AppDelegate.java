package com.example.admin.ebeliefapp.utility;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.admin.ebeliefapp.Constants.Tags;
import com.example.admin.ebeliefapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the application class containing all the common methods.
 */
public class AppDelegate extends Application {
    public static AppDelegate mInstance;
    public static ProgressDialog mProgressDialog;

    public static boolean CheckEmail(@NonNull String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mInstance = this;
    }

    public static boolean haveNetworkConnection(Context mContext) {
        return haveNetworkConnection(mContext, true);
    }

    public static boolean haveNetworkConnection(@Nullable Context mContext, boolean showAlert) {
        boolean isConnected = false;
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        if (mContext == null) {
            return false;
        } else {
            ConnectivityManager cm = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            isConnected = haveConnectedWifi || haveConnectedMobile;
            if (isConnected) {
                return isConnected;
            } else {
                if (showAlert) {
                    AppDelegate.showToast(mContext, "Please make sure that your device is " +
                            "connected to active internet connection.");
                }
            }
            return isConnected;
        }
    }

    public static void showToast(@Nullable Context mContext, String Message) {
        try {
            if (mContext != null)
                Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
            else
                Log.e("tag", "context is null at showing toast.");
        } catch (Exception e) {
            Log.e("tag", "context is null at showing toast.", e);
        }
    }


    /**
     * Method to Hide Soft Input Keyboard
     *
     * @param mContext
     * @param view
     */

    public static void HideKeyboardMain(@NonNull Activity mContext, @NonNull View view) {

        try {


            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            // R.id.search_img
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        } catch (Exception e) {
            //Utility.debug(1, TAG, "Exception in executing HideKeyboardMain()");
            e.printStackTrace();
        }
    }


    public static void hideKeyBoard(@Nullable final Activity mActivity, long timeAfter) {
        if (mActivity != null)
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    AppDelegate.hideKeyBoard(mActivity);
                }
            }, timeAfter);
    }

    public static void hideKeyBoard(@Nullable Activity mActivity) {
        if (mActivity == null)
            return;
        else {
            InputMethodManager inputManager = (InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            // check if no view has focus:
            View v = mActivity.getCurrentFocus();
            if (v == null)
                return;

            Log.e("Msg ", "hideKeyBoard viewNot null");
            inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    @Nullable
    public static String getHashKey(@NonNull Context mContext) {
        String str_HashKey = null;
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                str_HashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("HashKey = ", "" + str_HashKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Exceptio : ", "" + e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("Exceptio : ", "" + e);
        }
        return str_HashKey;
    }

    /**
     * @param TAG
     * @param Message
     * @param LogType
     */
    public static void Log(String TAG, String Message, int LogType) {
        switch (LogType) {
            // Case 1- To Show Message as Debug
            case 1:
                Log.d(TAG, Message);
                break;
            // Case 2- To Show Message as Error
            case 2:
                Log.e(TAG, Message);
                break;
            // Case 3- To Show Message as Information
            case 3:
                Log.i(TAG, Message);
                break;
            // Case 4- To Show Message as Verbose
            case 4:
                Log.v(TAG, Message);
                break;
            // Case 5- To Show Message as Assert
            case 5:
                Log.w(TAG, Message);
                break;
            // Case Default- To Show Message as System Print
            default:
                System.out.println(Message);
                break;
        }
    }


    public static void Log(String TAG, String Message) {
        AppDelegate.Log(TAG, Message, 1);
    }

    /* Function to show log for error message */
    public static void LogD(String Message) {
        AppDelegate.Log(Tags.DATE, "" + Message, 1);
    }

    /* Function to show log for error message */
    public static void LogE(Exception e) {
        if (e != null) {
            AppDelegate.LogE(e.getMessage());
            e.printStackTrace();
        } else {
            AppDelegate.Log(Tags.ERROR, "exception object is also null.", 2);
        }
    }

    /* Function to show log for error message */
    public static void LogE(OutOfMemoryError e) {
        if (e != null) {
            AppDelegate.LogE(e.getMessage());
            e.printStackTrace();
        } else {
            AppDelegate.Log(Tags.ERROR, "exception object is also null.", 2);
        }
    }

    /* Function to show log for error message */
    public static void LogE(String message, Exception exception) {
        if (exception != null) {
            AppDelegate.LogE("from = " + message + " => "
                    + exception.getMessage());
            exception.printStackTrace();
        } else {
            AppDelegate.Log(Tags.ERROR, "exception object is also null. at "
                    + message, 2);
        }
    }

    /**
     * Funtion to log with tag RESULT, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogI(String Message) {
        AppDelegate.Log(Tags.INTERNET, "" + Message, 1);
    }

    /**
     * Funtion to log with tag ERROR, you need to just pass the message. This
     * method is used to exeception .
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogE(String Message) {
        AppDelegate.Log(Tags.ERROR, "" + Message, 2);
    }

    /**
     * Funtion to log with tag URL_API, you need to just pass the message. This
     * method is used to log url of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogUA(String Message) {
        AppDelegate.Log(Tags.URL_API, "" + Message, 1);
    }

    /**
     * Funtion to log with tag URL_POST, you need to just pass the message. This
     * method is used to log post param of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogUP(String Message) {
        AppDelegate.Log(Tags.URL_POST, "" + Message, 1);
    }

    /**
     * Funtion to log with tag URL_RESPONSE, you need to just pass the message.
     * This method is used to log response of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogUR(String Message) {
        AppDelegate.Log(Tags.URL_RESPONSE, "URL_RESPONSE " + Message, 1);
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogT(String Message) {
        AppDelegate.Log(Tags.TEST, "" + Message, 1);
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogCh(String Message) {
        AppDelegate.Log("check", "" + Message, 1);
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @Message = pass your message that you want to log.
     * @int type = you need to pass int value to print in different color. 0 =
     * default color; 1 = fro print in exception style in red color; 2 =
     * info style in orange color;
     */
    public static void LogT(String Message, int type) {
        AppDelegate.Log(Tags.TEST, "" + Message, type);
    }

    /**
     * Funtion to log with tag PREF, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogP(String Message) {
        AppDelegate.Log(Tags.PREF, "" + Message, 1);
    }

    /**
     * Funtion to log with tag GCM, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    public static void LogGC(String Message) {
        AppDelegate.Log(Tags.GCM, "" + Message, 1);
    }

    public static String getFilterdUrl(String str_url) {
        if (str_url != null && str_url.length() > 0) {
            str_url = str_url.replace("[", "%5B");
            str_url = str_url.replace("@", "%40");
            str_url = str_url.replace(" ", "%20");
        }
        return str_url;
    }

    public static synchronized AppDelegate getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = (AppDelegate) mContext.getApplicationContext();
        }
        return mInstance;
    }


    public static void showProgressDialog(Activity mContext) {
        showProgressDialog(mContext, "", "");
    }

    public static void showProgressDialog(Activity mContext, String mTitle,
                                          String mMessage) {
        AppDelegate.hideKeyBoard(mContext);
        try {
            if (mContext != null) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    return;
                }
                mProgressDialog = new ProgressDialog(mContext, R.style.MyTheme);
                mProgressDialog.setCancelable(false);
                mProgressDialog.getWindow().setGravity(Gravity.CENTER);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog.show();
                } else {
                    mProgressDialog.show();
                }

            }
        } catch (Exception e) {
            AppDelegate.LogE(e);
        }

    }

    public static void hideProgressDialog(Context mContext) {
        try {
            if (mContext != null) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog = new ProgressDialog(mContext);
                    mProgressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            AppDelegate.LogE(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                AppDelegate.LogE(e);
            }
        }
        return sb.toString();
    }

}
