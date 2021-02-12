package com.example.fitastic.utility;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtility {

    /* Provides a central class to track which permissions are used by the app */

    // check if location permissions has been accepted already on device
    public static boolean hasLocationPermission(Context context) {
        // android Q (API 29 and above) requires user to accept background location perm
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // check if perm has been granted
            if (EasyPermissions.hasPermissions(context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                return true;
            } else
                return false;
        }
        else { /* else check if fine/coarse location perm has been accepted */
            if (EasyPermissions.hasPermissions(context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return true;
            } else
                return false;
        }
    }






}
