package com.example.admin.ebeliefapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

@TargetApi(VERSION_CODES.M)
public class PermissionUtils {

    private static final String NEVER_ASK_AGAIN_PREFIX = "never.ask.again";
    public static final int CAMERA_PERMISSION_ID = 1001;

    /**
     * Update the 'denied with never ask again' status before checking permissions grant statues (to handle the case in which user might have manually granted the permission via Settings)
     *
     * @param fragment
     * @param permissions
     * @return true if there is at least 1 permission is still denied with 'never ask again'
     */
    private static boolean updateDeniedWithNeverAskAgainStatus(Fragment fragment, String[] permissions) {
        boolean isDeniedWithNeverAskAgain = false;
        for (String permission : permissions) {
            int permissionCheckResult = ContextCompat.checkSelfPermission(fragment.getContext(), permission);

            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                // remove the 'never ask again denied' record for a granted permission
                removePermissionDeniedWithNeverAskAgain(fragment.getContext(), permission);
            } else if (isPermissionDeniedWithNeverAskAgain(fragment.getContext(), permission)) {
                isDeniedWithNeverAskAgain = true;
            }
        }
        return isDeniedWithNeverAskAgain;
    }

    /**
     * Check if a list of permissions has been granted, display dialogs in appropriate case
     *
     * @param fragment
     * @param permissions           is the list of permissions to be checked
     * @param permissionRequestCode
     * @param explanationTitle
     * @param explanationMessage
     * @param manualGrantTitle
     * @param manualGrantMessage
     * @return false if permission request is needed, true if all permissions in the list have been granted
     */
    public static boolean checkPermissions(Fragment fragment, String[] permissions, int permissionRequestCode, String explanationTitle, String explanationMessage, String manualGrantTitle, String manualGrantMessage) {
        boolean allPermissionGranted;

        // Check if at least 1 permission is denied with 'never ask again'
        boolean isDeniedWithNeverAskAgain = updateDeniedWithNeverAskAgainStatus(fragment, permissions);

        if (isDeniedWithNeverAskAgain) {
            allPermissionGranted = false;
            displayManualPermissionRequestDialog(fragment, manualGrantTitle, manualGrantMessage);
        } else {
            List<String> permissionsToRequest = new ArrayList<>();
            boolean needExplanation = checkPermissionsForExplanation(fragment, permissions,
                    permissionsToRequest);

            if (permissionsToRequest.size() > 0) {
                String[] requestedPermissions = permissionsToRequest.toArray(new String[permissionsToRequest.size()]);

                // If need explanation, it means at least 1 permission has not been granted
                if (needExplanation) {
                    displayPermissionExplanation(fragment, explanationTitle, explanationMessage, requestedPermissions, permissionRequestCode);
                } else {
                    // No explanation needed, we can request the permission.
                    fragment.requestPermissions(requestedPermissions, permissionRequestCode);
                }
                allPermissionGranted = false;
            } else {
                allPermissionGranted = true;
            }
        }

        return allPermissionGranted;
    }

    /**
     * Check a list of permissions to find out if an explanation for permissions request is needed (in case user has previously denied one of these permissions).
     * An explanation for the whole list is needed if there is at least 1 permission needs explanation
     * Also populate the permissionsToRequest list with the permissions in the list that haven't been granted
     *
     * @param fragment
     * @param permissions
     * @param permissionsToRequest
     * @return
     */
    private static boolean checkPermissionsForExplanation(Fragment fragment, String[] permissions, List<String> permissionsToRequest) {

        boolean needExplanation = false;

        for (String permission : permissions) {
            int permissionCheckResult = ContextCompat.checkSelfPermission(fragment.getContext(), permission);

            if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);

                if (!needExplanation && fragment.shouldShowRequestPermissionRationale(permission)) {
                    needExplanation = true;
                }
            } else {
                // Remove the 'never ask again denied' record for a granted permission (User might have manually granted the permission via Settings)
                removePermissionDeniedWithNeverAskAgain(fragment.getContext(), permission);
            }
        }

        return needExplanation;
    }

    /**
     * Check if an individual permission has been granted
     * Show permission explanation and/or request permission if not
     *
     * @param permission            name of permission
     * @param permissionRequestCode request code
     * @param explanationTitle      title for explanation dialog
     * @param explanationMessage    message for explanation dialog
     * @param manualGrantTitle      title for a manual permission grant request dialog
     * @param manualGrantMessage    message for a manual permission grant request dialog
     * @return
     */
    public static boolean checkIndividualPermission(Fragment fragment, String permission, int permissionRequestCode, String explanationTitle, String explanationMessage, String manualGrantTitle, String manualGrantMessage) {
        boolean permissionGranted;

        int permissionCheckResult = ContextCompat.checkSelfPermission(fragment
                .getContext(), permission);

        if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            if (isPermissionDeniedWithNeverAskAgain(fragment.getContext(), permission)) {
                displayManualPermissionRequestDialog(fragment, manualGrantTitle, manualGrantMessage);
            } else if (fragment.shouldShowRequestPermissionRationale(permission)) {
                // Display explanation for requesting camera permission
                displayPermissionExplanation(fragment, explanationTitle, explanationMessage, new String[]{permission}, permissionRequestCode);
            } else {
                // No explanation needed, we can request the permission.
                fragment.requestPermissions(new String[]{
                        permission}, permissionRequestCode);
            }
        } else {
            permissionGranted = true;
            // remove the 'never ask again denied' record for a granted permission (User might have manually granted the permission via Settings)
            removePermissionDeniedWithNeverAskAgain(fragment.getContext(), permission);
        }
        return permissionGranted;
    }

    /**
     * Display the explanation for requesting camera access
     *
     * @param fragment
     * @param title
     * @param message
     * @param permissions
     * @param permissionRequestCode
     */
    private static void displayPermissionExplanation(final Fragment fragment, String title, String message, final String[] permissions, final int permissionRequestCode) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (id) {
                    case DialogInterface.BUTTON_POSITIVE:
                        fragment.requestPermissions(permissions, permissionRequestCode);
                        break;
                }
            }
        };

        MiscUtils.displayConfirmationDialog(fragment.getContext(),
                title,
                message,
                "Ok", listener);
    }

    public static boolean isReadPhoneStateSuccess(Context context) {
        return checkPermission(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean isStoragePermissionSuccess(Context context) {
        return checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isCameraPermissionSuccess(Context context) {
        return checkPermission(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean isLocationPermissionSuccess(Context context) {
        return checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Object o, int permissionId, String... permissions) {
        if (o instanceof android.app.Activity) {
            ActivityCompat.requestPermissions((android.app.Activity) o, permissions, permissionId);
        } else if (o instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
        } else if (o instanceof Activity) {
            ((Activity) o).requestPermissions(permissions, permissionId);

        }
    }

    /**
     * Display a dialog explaining that user need to manually grant permission later after the user selected 'never ask again' and denied the permission request
     *
     * @param fragment
     * @param title
     * @param message
     */
    public static void displayManualPermissionRequestDialog(Fragment fragment, String title,
                                                            String message) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };

        MiscUtils.displayConfirmationDialog(fragment.getContext(), title, message,
                "Ok", listener);
    }

    private static String getPermissionWithNeverAskAgainPrefix(String permission) {
        return NEVER_ASK_AGAIN_PREFIX + permission;
    }

    /**
     * Check if a permission has been denied by user with 'never ask again'
     *
     * @param permission
     * @return
     */
    public static boolean isPermissionDeniedWithNeverAskAgain(Context context, String permission) {
        return SharedPrefUtil.getSharedPreferences(context).getBoolean(getPermissionWithNeverAskAgainPrefix(permission), false);
    }

    /**
     * Set if a permission has been denied by user with 'never ask again'
     *
     * @param permission
     * @param isDeniedWithNeverAskAgain
     */
    public static void setPermissionDeniedWithNeverAskAgain(Context context, String permission, boolean isDeniedWithNeverAskAgain) {
        SharedPrefUtil.getSharedPreferences(context).edit().putBoolean(getPermissionWithNeverAskAgainPrefix(permission), isDeniedWithNeverAskAgain).commit();
    }

    /**
     * Remove the record of a permission being denied by user with 'never ask again'
     *
     * @param permission
     */
    private static void removePermissionDeniedWithNeverAskAgain(Context context, String permission) {
        SharedPrefUtil.getSharedPreferences(context).edit().remove(getPermissionWithNeverAskAgainPrefix(permission)).commit();
    }
}
