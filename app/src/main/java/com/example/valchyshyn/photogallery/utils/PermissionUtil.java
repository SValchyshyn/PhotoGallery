package com.example.valchyshyn.photogallery.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.valchyshyn.photogallery.R;

/**
 * Created by valchyshyn on 14.02.17.
 */

public class PermissionUtil {

  public static final int REQUEST_PERMISSION_WRITE_STORAGE = 2;

  public static boolean checkWriteStoragePermission(Activity activity, View view, int requestCode) {
    return checkOrAksPermission(activity, view, requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getString(R.string.deny_write_storage));
  }

  public static void showSnackbarForWriteStoragePermission(Activity activity, View view) {
    askPermission(activity, view, REQUEST_PERMISSION_WRITE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getString(R.string.deny_write_storage));
  }

  private static boolean checkOrAksPermission(Activity activity, View view, int requestCode, final String permission, String message) {
    if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
        askPermission(activity, view, requestCode, permission, message);
      } else {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
      }
      return false;
    }
    return true;
  }

  private static void askPermission(@NonNull final Activity activity, View view, final int requestCode, final String permission, String message) {
    final boolean isShouldShowRequest = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    CenteredSnackbar.make(
        view,
        message,
        Snackbar.LENGTH_LONG
    ).setAction(
        isShouldShowRequest ?
            activity.getString(R.string.permission_action_grant) :
            activity.getString(R.string.permission_action_setting),
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (isShouldShowRequest) {
              ActivityCompat.requestPermissions(
                  activity,
                  new String[]{permission},
                  requestCode
              );
            } else {
              activity.startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
            }
          }
        }
    ).show();
  }
}