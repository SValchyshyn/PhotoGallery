package com.example.valchyshyn.photogallery.utils;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by valchyshyn on 14.02.17.
 */

public class ImageFileStorageUtil {

  private static final String appName = "PhotoGallery";

  @Nullable
  public static File getExternalImageCacheDir(boolean shouldCreateIfNotExists) {

    if (!canWriteToExternalStorage()) {
      return null;
    }

    File cacheDir = new File(Environment.getExternalStorageDirectory(), String.format("%s/%s", appName, "PhotoGallery Pictures"));

    if (shouldCreateIfNotExists && !createDirIfNotExists(cacheDir)) {
      return null;
    }

    return cacheDir;
  }

  private static boolean canWriteToExternalStorage() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  private static boolean createDirIfNotExists(@NonNull File cacheDir) {
    return !(!cacheDir.exists() && !cacheDir.mkdirs());
  }

}
