package com.example.valchyshyn.photogallery.utils;

import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by valchyshyn on 14.02.17.
 */

public class FileUtils {

  public static void deleteFile(Uri uri) {
    File file = new File(uri.getPath());
    if (file.exists()) {
      if (file.delete()) {
        Log.d("file Deleted :", uri.getPath());
      } else {
        Log.d("file not Deleted :", uri.getPath());
      }
    }
  }
}
